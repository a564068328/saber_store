package com.icloud.listenbook.socket.pro;

import org.json.JSONObject;

import android.R;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.socket.share.HallCommand;
import com.icloud.wrzjh.base.net.socket.NIOSocket;
import com.icloud.wrzjh.base.net.socket.packet.DataPacket;
import com.icloud.wrzjh.base.utils.ClientDeviceInfo;
import com.icloud.wrzjh.base.utils.ClockUtil;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;

public class HallSocket extends NIOSocket {

	private HallPacketIml mProcess;

	public HallSocket(String ip, int port) {
		super(ip, port);
		// super("192.168.1.99", port);
		mProcess = new HallPacketIml(this);
		sendHeart();
	}

	// NIOSocket的onRead接收到服务器消息后调用handCMD来处理
	@Override
	protected void handCMD(DataPacket dp) {
		if (mProcess != null) {
			int cmd = dp.getCmd();
			mProcess.proCmd(dp, cmd);
			dp.Recycle();
		}
	}

	@Override
	public void connect() {
		super.connect();
	}

	@Override
	protected void onConnected() throws Exception {

		// 登陆
		enteHall();
		// 请求在线人数
		sendOnline();
		
		objectbuffer.instance().setHallSocket(this);
		// getUnlineMsg();
	}

	@Override
	protected void onDisconnected() {

	}

	public void onServerPacket(int cmd, DataPacket dp) {
		if (mProcess != null) {
			mProcess.proCmd(dp, cmd);
			dp.Recycle();
		}
	}

	// 多次重连失败
	public void onFailed() {

	}

	/** 登录 */
	private void enteHall() {
		if (isConnect()) {
			int cmd = HallCommand.HALL_LOGIN;
			DataPacket dp = DataPacket.allocPacket();// 得到一个包体
			dp.writeBegin(cmd);// 协议号
			ClientDeviceInfo.getInstance().getNetmode(GameApp.getContext());// 得到网咯类型
			dp.writeInt((int) UserInfo.instance().getUid());// 协议字段
			// dp.writeString(UserInfo.instance().getToken());
			dp.writeByte(ClientDeviceInfo.getInstance().netmode);
			dp.writeEnd();// 包体关闭
			this.sendCmd(dp);// 发送
		}
	}

	public static boolean showOnline = true;

	/** 心跳 */
	private void sendHeart() {
		HandlerUtils.instance().postDelayed(new Runnable() {
			public void run() {
				if (isConnect()) {
					int cmd = HallCommand.CLIENT_HEART;
					DataPacket dp = DataPacket.allocPacket();
					dp.writeBegin(cmd);
					dp.writeEnd();
					sendCmd(dp);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendOnline();
				}
				sendHeart();
			}
		}, 25 * 1000);
	}

	/** 在线人数 */
	private void sendOnline() {
		if (isConnect()) {
			int cmd = HallCommand.GET_ONLINE;
			DataPacket dp = DataPacket.allocPacket();
			dp.writeBegin(cmd);
			dp.writeEnd();
			this.sendCmd(dp);
		}
	}

	/** 小喇叭 用户向聊天室发消息时调用 **/
	public void sendBroadcast(String text) {
		if (isConnect()) {
			int cmd = HallCommand.CLIENT_REQ_BROAD;
			DataPacket dp = DataPacket.allocPacket();
			dp.writeBegin(cmd);
			dp.writeString(text);
			dp.writeEnd();
			this.sendCmd(dp);
		}
	}

	/** 邀请好友 */
	public void reqFriend(int roomType, int uid, int roomId) {
		if (isConnect()) {
			int cmd = HallCommand.CLIENT_REQ_FRIEND;
			DataPacket dp = DataPacket.allocPacket();
			dp.writeBegin(cmd);
			dp.writeInt(uid);
			dp.writeInt(roomType);
			dp.writeInt(roomId);
			dp.writeEnd();
			this.sendCmd(dp);
		}
	}

	/** 向好友发送消息 */
	public void sendMsgToFriend(int uid, String msg) {
		if (isConnect()) {
			int cmd = HallCommand.CLIENT_SEND_FRI_MSG;
			DataPacket dp = DataPacket.allocPacket();
			dp.writeBegin(cmd);
			dp.writeInt(uid);
			dp.writeString(msg);
			dp.writeEnd();
			this.sendCmd(dp);

			JSONObject jb = new JSONObject();
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 获取离线消息
	 */
	public void getUnlineMsg() {
		int cmd = HallCommand.CLIENT_REQ_BroadRecord;
		DataPacket dp = DataPacket.allocPacket();
		dp.writeBegin(cmd);
		dp.writeEnd();
		this.sendCmd(dp);// 发送
		// LogUtil.e("获取离线消息", "\n");
	}
}
