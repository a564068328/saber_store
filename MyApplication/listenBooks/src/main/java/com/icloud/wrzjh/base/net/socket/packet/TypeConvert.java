package com.icloud.wrzjh.base.net.socket.packet;

import java.util.Formatter;

public class TypeConvert {

	public static int MAX_HEX_LENGTH = 200;
	private byte btmp;

	public short byte2short(byte[] b) {
		return (short) (b[1] & 0xFF | (b[0] & 0xFF) << 8);
	}

	public static short byte2short(byte[] b, int offset) {
		return (short) (b[(offset + 1)] & 0xFF | (b[(offset + 0)] & 0xFF) << 8);
	}

	public int byte2int(byte[] b) {
		return (b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24);
	}

	public int byte2int(byte[] b, int offset) {
		return (b[(offset + 3)] & 0xFF | (b[(offset + 2)] & 0xFF) << 8
				| (b[(offset + 1)] & 0xFF) << 16 | (b[offset] & 0xFF) << 24);
	}

	public long byte2Uint(byte[] b, int offset) {
		return (b[(offset + 3)] & 0xFFL | (b[(offset + 2)] & 0xFFL) << 8
				| (b[(offset + 1)] & 0xFFL) << 16 | (b[offset] & 0xFFL) << 24);
	}

	public long byte2long(byte[] b) {
		return (b[7] & 0xFF | (b[6] & 0xFF) << 8 | (b[5] & 0xFF) << 16
				| (b[4] & 0xFF) << 24 | (b[3] & 0xFF) << 32
				| (b[2] & 0xFF) << 40 | (b[1] & 0xFF) << 48 | b[0] << 56);
	}

	public long byte2long(byte[] b, int offset) {
		return (b[(offset + 7)] & 0xFF | (b[(offset + 6)] & 0xFF) << 8
				| (b[(offset + 5)] & 0xFF) << 16
				| (b[(offset + 4)] & 0xFF) << 24
				| (b[(offset + 3)] & 0xFF) << 32
				| (b[(offset + 2)] & 0xFF) << 40
				| (b[(offset + 1)] & 0xFF) << 48 | b[offset] << 56);
	}

	public byte[] int2byte(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n >> 24);
		b[1] = (byte) (n >> 16);
		b[2] = (byte) (n >> 8);
		b[3] = (byte) n;
		return b;
	}

	public void int2byte(int n, byte[] buf, int offset) {
		buf[offset] = (byte) (n >> 24);
		buf[(offset + 1)] = (byte) (n >> 16);
		buf[(offset + 2)] = (byte) (n >> 8);
		buf[(offset + 3)] = (byte) n;
	}

	public byte[] short2byte(int n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n >> 8);
		b[1] = (byte) n;
		return b;
	}

	public void short2byte(int n, byte[] buf, int offset) {
		buf[offset] = (byte) (n >> 8);
		buf[(offset + 1)] = (byte) n;
	}

	public byte[] long2byte(long n) {
		byte[] b = new byte[8];

		b[0] = (byte) (int) (n >> 56);
		b[1] = (byte) (int) (n >> 48);
		b[2] = (byte) (int) (n >> 40);
		b[3] = (byte) (int) (n >> 32);
		b[4] = (byte) (int) (n >> 24);
		b[5] = (byte) (int) (n >> 16);
		b[6] = (byte) (int) (n >> 8);
		b[7] = (byte) (int) n;
		return b;
	}

	public void long2byte(long n, byte[] buf, int offset) {
		buf[offset] = (byte) (int) (n >> 56);
		buf[(offset + 1)] = (byte) (int) (n >> 48);
		buf[(offset + 2)] = (byte) (int) (n >> 40);
		buf[(offset + 3)] = (byte) (int) (n >> 32);
		buf[(offset + 4)] = (byte) (int) (n >> 24);
		buf[(offset + 5)] = (byte) (int) (n >> 16);
		buf[(offset + 6)] = (byte) (int) (n >> 8);
		buf[(offset + 7)] = (byte) (int) n;
	}

	public String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; ++i) {
			buffer.append(toHexString(b[i]));
		}
		return buffer.toString();
	}

	public String toHexString(byte[] b, int offset, int length) {
		StringBuffer buffer = new StringBuffer();
		length += offset;
		int max = 0;
		for (int i = offset; i < length; ++i) {
			max++;
			if (max >= MAX_HEX_LENGTH) {
				break;
			}
			buffer.append(toHexString(b[i]));
		}
		return buffer.toString() + (max >= MAX_HEX_LENGTH ? "..." : "");
	}

	public String toHexString2(byte[] b, int offset, int length) {
		StringBuffer buffer = new StringBuffer();
		Formatter formatter = new Formatter(buffer);
		length += offset;
		int max = 0;
		for (int i = offset; i < length; ++i) {
			max++;
			if (max >= MAX_HEX_LENGTH) {
				break;
			}
			formatter.format("%02x", b[i]);
		}
		return buffer.toString() + (max >= MAX_HEX_LENGTH ? "..." : "");
	}

	public String toHexString(byte b) {
		char[] buffer = new char[2];
		buffer[0] = Character.forDigit((b >>> 4) & 0x0F, 16);
		buffer[1] = Character.forDigit(b & 0x0F, 16);
		return new String(buffer);
	}

	public void reverse2Byte(byte[] b, int offset) {
		btmp = b[offset + 0];
		b[offset + 0] = b[offset + 1];
		b[offset + 1] = btmp;
	}

	public void reverse4Byte(byte[] b, int offset) {
		btmp = b[offset + 0];
		b[offset + 0] = b[offset + 3];
		b[offset + 3] = btmp;

		btmp = b[offset + 1];
		b[offset + 1] = b[offset + 2];
		b[offset + 2] = btmp;
	}

	public void reverse8Byte(byte[] b, int offset) {
		btmp = b[offset + 0];
		b[offset + 0] = b[offset + 7];
		b[offset + 7] = btmp;

		btmp = b[offset + 1];
		b[offset + 1] = b[offset + 6];
		b[offset + 6] = btmp;

		btmp = b[offset + 2];
		b[offset + 2] = b[offset + 5];
		b[offset + 5] = btmp;

		btmp = b[offset + 3];
		b[offset + 3] = b[offset + 4];
		b[offset + 4] = btmp;
	}

}
