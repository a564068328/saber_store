package com.icloud.listenbook.socket.pro;

import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.listenbook.unit.UserIconManage;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.OtherUser;
import com.yunva.live.sdk.interfaces.logic.model.GetAwardsNotify;

public class HallSocketManage {
	static HallSocketManage instance;
	protected HallSocket hallSocket;
	private static final int NOTIFY_ID = 123595;

	public static HallSocketManage instance() {
		if (instance == null)
			instance = new HallSocketManage();
		return instance;
	}

	private HallSocketManage() {

	}

	/** 系统返回添加 */
	public void addChat(long uid, String value) {
		if (uid != UserInfo.instance().getUid()) {
			Msg msg = JsonUtils.fromJson(value, Msg.class);
			ChatMsg chatMsg = new ChatMsg();
			chatMsg.setDateline(System.currentTimeMillis());
			chatMsg.setSuid(uid);
			chatMsg.setRuid(ChatMsgManage.CHAR_TAG);
			chatMsg.setName(msg.N);
			chatMsg.setMsg(msg.M);
			chatMsg.setIcon(UserIconManage.instance().getUserIcon(uid));
			ChatMsgManage.instance().addMsg(chatMsg);
			// 通知栏显示
			boolean ChatMsgActIsFinish=true;
			ActivityManager am = (ActivityManager) GameApp.getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> list = am.getRunningTasks(1);
			if (list != null && list.size() > 0) {
				ComponentName cpn = list.get(0).topActivity;
				if ("com.icloud.listenbook.ui.chipAct.ChatMsgAct"
						.equals(cpn.getClassName())) {
					ChatMsgActIsFinish=false;
				}
			}
			if (SharedPreferenceUtil.getChatmsgNotifySwith()&& ChatMsgActIsFinish) {
				String nmsg = chatMsg.getMsg();
				String name = chatMsg.getName();
				Intent intent = new Intent(GameApp.getContext(),
						ChatMsgAct.class);
				ToolUtils.showNotification(GameApp.getContext(), NOTIFY_ID,
						"聊天室消息", name, nmsg, intent,
						Notification.FLAG_AUTO_CANCEL, false);
			}
		}
	}

	/*** 自己发送添加 */
	public void addChat(long uid, Msg msg) {
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setDateline(System.currentTimeMillis());
		chatMsg.setSuid(uid);
		chatMsg.setRuid(ChatMsgManage.CHAR_TAG);
		chatMsg.setName(msg.N);
		chatMsg.setMsg(msg.M);
		chatMsg.setIcon(UserInfo.instance().getIcon());
		ChatMsgManage.instance().addMsg(chatMsg);

	}

	/** 系统返回添加 */
	public void addMsgToFriend(long uid, String msg) {
		addMsgToFriend(uid, msg, System.currentTimeMillis());

	}

	public void addSystemChatMsg(String msg, long time) {
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setDateline(time);
		chatMsg.setSuid(ChatMsgManage.SYS_CHAR_TAG);
		chatMsg.setRuid(UserInfo.instance().getUid());
		chatMsg.setName("系统消息");
		chatMsg.setMsg(msg);
		ChatMsgManage.instance().addSystemChatMsg(chatMsg);
	}

	public void addUnlineChatMsg(int suid, String msg, String name, long time) {
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setDateline(time);
		chatMsg.setSuid(suid);
		chatMsg.setRuid(ChatMsgManage.CHAR_TAG);
		chatMsg.setName(name);
		chatMsg.setMsg(msg);
		chatMsg.setIcon(UserIconManage.instance().getUserIcon(suid));
		ChatMsgManage.instance().addUnlineMsg(chatMsg);
	}

	public void addSystemChatMsg(String msg) {
		addSystemChatMsg(msg, System.currentTimeMillis());
	}

	public void addMsgToFriend(long uid, String msg, long data) {
		if (uid != UserInfo.instance().getUid()) {
			ChatMsg chatMsg = new ChatMsg();
			chatMsg.setDateline(data);
			chatMsg.setSuid(uid);
			chatMsg.setRuid(UserInfo.instance().getUid());
			chatMsg.setName("");
			chatMsg.setMsg(msg);
			ChatMsgManage.instance().addMsgToFriend(chatMsg);
		}

	}

	/*** 自己发送添加 */
	protected void addLocalMsgToFriend(long uid, String msg) {
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setDateline(System.currentTimeMillis());
		chatMsg.setSuid(UserInfo.instance().getUid());
		chatMsg.setRuid(uid);
		chatMsg.setName(UserInfo.instance().getNick());
		chatMsg.setIcon(UserInfo.instance().getIcon());
		chatMsg.setMsg(msg);
		ChatMsgManage.instance().addMsgToFriend(chatMsg);

	}

	public boolean sendMsgToFriend(long uid, String str) {
		if (hallSocket != null && hallSocket.isConnect()) {
			hallSocket.sendMsgToFriend((int) uid, str);
			addLocalMsgToFriend(uid, str);
			return true;
		}
		return false;
	}

	public boolean sendChat(String name, String str, String icon) {
		if (hallSocket != null && hallSocket.isConnect()) {
			Msg msg = new Msg(name, str);
			String snedStr = JsonUtils.toJson(msg);
			hallSocket.sendBroadcast(snedStr);
			addChat(UserInfo.instance().getUid(), msg);
			return true;
		}
		return false;
	}

	public void connSocket() {
		if (hallSocket != null && hallSocket.isConnect())
			hallSocket.close();
		hallSocket = new HallSocket(ServerIps.getHallIp(),
				ServerIps.getHallPort());
		hallSocket.connect();
	}

	class Msg {
		String N;
		String M;

		public Msg(String N, String M) {
			this.N = N;
			this.M = M;
		}
	}

}
