package com.icloud.wrzjh.base.net.socket.packet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import com.icloud.wrzjh.base.utils.LogUtil;

public class DataPacket {

	private String TAG = this.getClass().getSimpleName();
	// for debug
	private int cmd;
	//
	public static final int INIT_SIZE = 2;
	public static final int HEADER_SIZE = 6;
	public static final int MAX_BODY_LENGTH = Short.MAX_VALUE / 2;
	public static final int MAX_STRING_LENGTH = Short.MAX_VALUE / 2;
	public static final int PACKET_INIT_SIZE = 2 * 1024;
	public static final int PACKET_EXTEND_SIZE = 1 * 1024;
	public static final String EMPTY_STRING = "";

	// tmp stack variables
	private byte[] bytep1;
	private short s1;
	private int i1;
	private long n1;
	//
	private byte[] buffer = new byte[PACKET_INIT_SIZE];
	private int position; // 记录字节数组中的位置
	private int readLength;// 服务端包的长度
	private boolean EOP;
	private boolean inUse;

	public TypeConvert tc = new TypeConvert();

	private static ArrayList<DataPacket> list = new ArrayList<DataPacket>();
	private static byte[] sync = new byte[0];

	public static DataPacket allocPacket() {

		synchronized (sync) {
			DataPacket dp;
			for (int i = 0; i < list.size(); i++) {
				dp = list.get(i);
				if (!dp.inUse) {
					dp.inUse = true;
					return dp;
				}
			}
			dp = new DataPacket();
			dp.reset();
			list.add(dp);
			dp.inUse = true;
			return dp;
		}
	}

	public void Recycle() {
		synchronized (sync) {
			reset();
		}
	}

	private DataPacket() {
		reset();
	}

	private void reset() {
		position = 0;
		readLength = 0;
		inUse = false;
		EOP = false;
	}

	public void reservedHead(int lenOfBytes) {
		if (lenOfBytes < 1)
			return;
		if ((position + lenOfBytes) > buffer.length) {
			realloc(lenOfBytes);
		}
		System.arraycopy(buffer, 0, buffer, lenOfBytes, position);
		position = position + lenOfBytes;
	}

	public void setReadLength(int len) {
		readLength = len;
		i1 = readLength - buffer.length;
		if (i1 > 0) {
			realloc(i1);
		}
	}

	public int getLength() {
		return position;
	}

	public boolean isEOP() {
		return EOP;
	}

	private void realloc(int min) {
		i1 = PACKET_EXTEND_SIZE;
		while (i1 < min) {
			i1 += PACKET_EXTEND_SIZE;
		}
		bytep1 = new byte[buffer.length + i1];
		System.arraycopy(buffer, 0, bytep1, 0, position);
		buffer = bytep1;
		bytep1 = null;
	}

	public void writeByte(byte data) {
		if ((position + 1) > buffer.length) {
			realloc(1);
		}
		buffer[position++] = (byte) data;

	}

	public void writeShort(short data) {
		if ((position + 2) > buffer.length) {
			realloc(2);
		}
		tc.short2byte(data, buffer, position);
		// tc.reverse2Byte(buffer, position);
		position = position + 2;
	}

	public void writeInt(int data) {
		if ((position + 4) > buffer.length) {
			realloc(4);
		}
		tc.int2byte(data, buffer, position);
		// tc.reverse4Byte(buffer, position);
		position = position + 4;
	}

	public void writeInt64(long data) {
		if ((position + 8) > buffer.length) {
			realloc(8);
		}
		tc.long2byte(data, buffer, position);
		// tc.reverse8Byte(buffer, position);
		position = position + 8;
	}

	public void writeString(String data) {
		bytep1 = null;
		LogUtil.e(TAG, "writeString:"+data);
		try {
			if (data == null) {
				bytep1 = EMPTY_STRING.getBytes("utf-8");
			} else {
				bytep1 = data.getBytes("utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		writeShort((short) (bytep1.length));

		i1 = bytep1.length;
		if ((position + i1) > buffer.length) {
			realloc(i1);
		}
		System.arraycopy(bytep1, 0, buffer, position, bytep1.length);
		position = position + bytep1.length;
	}

	public void writeBegin(int cmd) {
		int ver = 0, subver = 0;
		this.cmd = cmd;
		position = HEADER_SIZE;

		buffer[2] = 1;
		buffer[3] = (byte) cmd;
		// ver
		buffer[4] = (byte) ver;
	}

	public void writeEnd() {
		// bodylen 0、1
		tc.short2byte(position, buffer, 0);
		i1 = BufferEncrypt.EncryptBuffer(buffer, HEADER_SIZE, position,
				BufferEncrypt.sendMap);
		buffer[5] = (byte) i1;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void readBegin() {
		BufferEncrypt.CrevasseBuffer(buffer, HEADER_SIZE, buffer.length,
				buffer[8], BufferEncrypt.recvMap);
		position = HEADER_SIZE;
	}

	// 所有read，为安全，先判定是否还有字节可读
	public byte readByte() {
		if ((position + 1) > readLength) {
			readError();
			EOP = true;
			return 0;
		}
		return buffer[position++];
	}

	public short readShort() {
		if ((position + 2) > readLength) {
			readError();
			EOP = true;
			return 0;
		}

		// tc.reverse2Byte(buffer, position);
		s1 = tc.byte2short(buffer, position);
		position += 2;
		return s1;
	}

	public int readInt() {
		if ((position + 4) > readLength) {
			readError();
			EOP = true;
			return 0;
		}
		// tc.reverse4Byte(buffer, position);
		i1 = tc.byte2int(buffer, position);
		position += 4;
		return i1;
	}

	/** 无符号int32 */
	public long readUInt() {
		if ((position + 4) > readLength) {
			readError();
			EOP = true;
			return 0;
		}
		// tc.reverse4Byte(buffer, position);
		n1 = tc.byte2Uint(buffer, position);
		position += 4;
		return n1;
	}

	public long readInt64() {
		if ((position + 8) > readLength) {
			readError();
			EOP = true;
			return 0L;
		}
		// tc.reverse8Byte(buffer, position);
		n1 = tc.byte2long(buffer, position);
		position += 8;
		return n1;
	}

	public String readString() {
		n1 = readShort();
		if (n1 < 1) {
			LogUtil.e(TAG, "empty string");
			return EMPTY_STRING;
		}
		if (n1 > MAX_STRING_LENGTH) {
			readError();
			LogUtil.e(TAG, tc.toHexString(buffer, 0, readLength));
			EOP = true;
			return EMPTY_STRING;
		}
		try {
			i1 = position;
			position += n1;
			return new String(buffer, i1, (int) (n1), "utf-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, e);
			readError();
			EOP = true;
			return EMPTY_STRING;
		}
	}

	public void packetError() {
		LogUtil.e(TAG,
				"error check packet " + tc.toHexString2(buffer, 0, readLength));
	}

	private void readError() {
		LogUtil.e(TAG,
				"error read packet " + tc.toHexString2(buffer, 0, readLength));
	}

	// for debug
	public void logWrite() {
		LogUtil.w(TAG, "send cmd=0x" + Integer.toHexString(cmd) + " length="
				+ Integer.toString(position));
//		LogUtil.e(TAG, "send" + tc.toHexString2(buffer, 0, position));

	}

	public void logRead() {
		LogUtil.w(TAG, "recv cmd=0x" + Integer.toHexString(cmd) + " length="
				+ Integer.toString(readLength));
		// Gdx.app.log(TAG, "recv" + tc.toHexString2(buffer, 0, readLength));
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getCmd() {
		return this.cmd;
	}

	@Override
	public String toString() {
		return "DataPacket [cmd=" + cmd + ", bytep1=" + Arrays.toString(bytep1)
				+ ", s1=" + s1 + ", i1=" + i1 + ", n1=" + n1 + ", buffer="
				+ Arrays.toString(buffer) + ", position=" + position
				+ ", readLength=" + readLength + ", EOP=" + EOP + ", inUse="
				+ inUse + "]";
	}

	public int getReadLength() {
		return this.readLength;
	}
}
