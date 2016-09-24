package com.icloud.wrzjh.base.net.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.icloud.wrzjh.base.net.socket.packet.DataPacket;
import com.icloud.wrzjh.base.utils.LogUtil;

public abstract class NIOSocket implements Runnable {

	public static final String shareKey = "cf79b00806591e4f8bfd411ef334a948";

	private Selector selector;
	private SocketChannel channel;
	private Thread thread;

	private final AtomicBoolean connected = new AtomicBoolean(false);

	private static final int READ_BUFFER_SIZE = 0x4000;
	private static final int WRITE_BUFFER_SIZE = 0x4000;
	private static final long INITIAL_RECONNECT_INTERVAL = 1000;
	private static final long MAXIMUM_RECONNECT_INTERVAL = 6000;

	private long reconnectInterval = INITIAL_RECONNECT_INTERVAL;

	private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUFFER_SIZE);
	private ByteBuffer writeBuf = ByteBuffer.allocateDirect(WRITE_BUFFER_SIZE);

	private String ip;
	private int port;

	public NIOSocket(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void connect() {
		try {
			if (thread == null) {
				thread = new Thread(this);
			}
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCmd(DataPacket dp) {

		if (dp != null && isConnect()) {
			dp.logWrite();
			// 创建一个容量为dp.getLength（）字节的ByteBuffer
			ByteBuffer buf = ByteBuffer.allocate(dp.getLength());
			// put数据入缓冲区
			buf.put(dp.getBuffer(), 0, dp.getLength());
			// 回绕缓冲区，
			//这个方法用来将缓冲区准备为数据传出状态,执行以上方法后,
			//输出通道会从数据的开头而不是末尾开始.回绕保持缓冲区中的数据不变,只是准备写入而不是读取.
			buf.flip();
			dp.Recycle();
			try {
				//
				write(buf);
			} catch (Exception e) {
				closeSocket();
				e.printStackTrace();
			}
		}
	}

	public boolean isConnect() {
		if (channel != null) {
			return channel.isConnected();
		}
		return false;
	}

	public void close() {
		closeSocket();
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
	}

	public void reConnected() {
		closeSocket();
	}

	private void closeSocket() {
		try {
			connected.set(false);
			onDisconnected();
			writeBuf.clear();
			readBuf.clear();

			if (channel != null)
				channel.close();
			if (selector != null)
				selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUtil.e("closeSocket", "connection closed");
	}

	public void run() {
		InetSocketAddress address = new InetSocketAddress(ip, port);
		while (!Thread.interrupted()) {
			try {
				LogUtil.i("start connected to " + ip + port);
				selector = Selector.open();
				channel = SocketChannel.open();
				configureChannel(channel);

				try {
					channel.connect(address);
					channel.register(selector, SelectionKey.OP_CONNECT);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(reconnectInterval - INITIAL_RECONNECT_INTERVAL);
					if (reconnectInterval < MAXIMUM_RECONNECT_INTERVAL) {
						reconnectInterval *= 2;
					}
				} catch (InterruptedException e) {
					break;
				}
				try {
					while (!thread.isInterrupted() && channel.isOpen()) {
						if (selector.select() > 0) {
							processSelectedKeys(selector.selectedKeys());
						}
					}
				} catch (Exception e) {
					LogUtil.e("exception", e);
				} finally {
					closeSocket();
				}

				try {
					Thread.sleep(reconnectInterval);
					if (reconnectInterval < MAXIMUM_RECONNECT_INTERVAL) {
						reconnectInterval *= 2;
					}
					LogUtil.i("reconnecting to " + address);
				} catch (InterruptedException e) {
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processSelectedKeys(Set<SelectionKey> keys) throws Exception {
		Iterator<SelectionKey> itr = keys.iterator();
		while (itr.hasNext()) {
			SelectionKey key = itr.next();
			if (key.isValid()) {
				if (key.isReadable())
					processRead(key);
				if (key.isWritable())
					processWrite(key);
				if (key.isConnectable())
					processConnect(key);
				if (key.isAcceptable())
					;
			}
			itr.remove();
		}
	}

	private void processConnect(SelectionKey key) throws Exception {
		SocketChannel ch = (SocketChannel) key.channel();
		if (ch.finishConnect()) {
			LogUtil.i("connected to " + ip + port);
			key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			reconnectInterval = INITIAL_RECONNECT_INTERVAL;
			connected.set(true);
			onConnected();
		}
	}
    
	private void processRead(SelectionKey key) throws Exception {
		ReadableByteChannel ch = (ReadableByteChannel) key.channel();

		int bytesOp = 0, bytesTotal = 0;
		while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0) {
			bytesTotal += bytesOp;
		}

		LogUtil.d("processRead bytesTotal ", "" + bytesTotal);
		if (bytesTotal > 0) {
			readBuf.flip();
			onRead(readBuf);
			readBuf.compact();
		} else if (bytesOp == -1) {
			LogUtil.e("", "peer closed read channel");
			ch.close();
		}
	}

	private void processWrite(SelectionKey key) throws IOException {
		WritableByteChannel ch = (WritableByteChannel) key.channel();
		synchronized (writeBuf) {
			writeBuf.flip();

			int bytesOp = 0, bytesTotal = 0;
			while (writeBuf.hasRemaining()
					&& (bytesOp = ch.write(writeBuf)) > 0) {
				bytesTotal += bytesOp;
			}

			if (writeBuf.remaining() == 0) {
				key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
			}

			if (bytesTotal > 0) {
				writeBuf.notify();
			} else if (bytesOp == -1) {
				LogUtil.i("peer closed write channel");
				ch.close();
			}
			writeBuf.compact();
		}
	}

	/**
	 * @param buffer
	 *            data to send, the buffer should be flipped (ready for read)
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void write(ByteBuffer buffer) throws InterruptedException,
			IOException {
		if (!connected.get())
			throw new IOException("not connected");
		synchronized (writeBuf) {
			if (writeBuf.remaining() < buffer.remaining()) {
				throw new IOException("send buffer full");
			}

			writeBuf.put(buffer);

			if (writeBuf.hasRemaining()) {
				SelectionKey key = channel.keyFor(selector);
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				selector.wakeup();
			}
		}
	}

	protected void onRead(ByteBuffer buf) throws Exception {
		if (!isConnect()) {
			return;
		}
		DataPacket dp = DataPacket.allocPacket();

		int limit = buf.limit();
		int remain = buf.remaining();

		int body_len = buf.getShort(limit - remain);

		if (remain != body_len) {
			LogUtil.e("readBuffer", " remain  * " + remain + " * body_len * "
					+ "" + body_len);
		}

		if (remain >= body_len) {
			buf.get(dp.getBuffer(), 0, body_len);
			dp.setReadLength(body_len);
			byte cmd = dp.getBuffer()[3];
			dp.setCmd(cmd);
			dp.logRead();
			handCMD(dp);
		} else {
			LogUtil.e("readBuffer", "readBuffer error");
			return;
		}
		int remain1 = buf.remaining();
		LogUtil.d("", "readBuffer * remain1  * " + "" + remain1);
		if (remain1 >= DataPacket.HEADER_SIZE) {
			onRead(buf);
		}
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	protected abstract void handCMD(DataPacket dp);

	protected abstract void onConnected() throws Exception;

	protected abstract void onDisconnected();

	private void configureChannel(SocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		channel.socket().setSendBufferSize(0x100000); // 10K
		channel.socket().setReceiveBufferSize(0x100000); // 10K
		channel.socket().setKeepAlive(true);
		channel.socket().setReuseAddress(true);
		channel.socket().setSoLinger(false, 0);
		channel.socket().setSoTimeout(0);
		channel.socket().setTcpNoDelay(true);
	}
}
