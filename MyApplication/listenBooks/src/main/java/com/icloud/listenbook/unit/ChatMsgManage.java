package com.icloud.listenbook.unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import android.R.integer;
import android.content.Intent;
import android.support.v4.util.LongSparseArray;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.pro.objectbuffer;
import com.icloud.listenbook.ui.chipFrage.UserInfoFrage;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.ChatMsg;

public class ChatMsgManage {
	private static String TAG = "ChatMsgManage";
	public static final int CHAR_TAG = 147753;
	public static final int SYS_CHAR_TAG = 10000;
	public static final String CHAT_MSG_ADD_ACTION = "com.icloud.listenbook.ui.CHAT_MSG_ACTION.add";
	public static final String FRIEND_MSG_ADD_ACTION = "com.icloud.listenbook.ui.FRIEND_MSG_ACTION.add";
	public static final String SYSTEM_MSG_ACTION = "com.icloud.listenbook.ui.SYSTEM_MSG_ACTION.add";
	public static final String UNCHAT_MSG_ADD_ACTION = "com.icloud.listenbook.ui.UNCHAT_MSG_ADD_ACTION.add";
	public static final String ONCHAT_MSG_ADD_ACTION = "com.icloud.listenbook.ui.ONCHAT_MSG_ADD_ACTION.add";;
	static ChatMsgManage instance;
	public static final int CHAT_COUNT_LIMIT = 50;
	protected ArrayList<ChatMsg> chatMsgList;
	protected ArrayList<ChatMsg> systemMsgList;
	protected ArrayList<ChatMsg> unlineChatMsgList;
	MessageListener messageListener;
	FriendMessageListener friendMessageListener;
	int chatSize;
	int unlineChatSize;
	int totalSize;
	LongSparseArray<Boolean> friendMsgTip;
    
	public static ChatMsgManage instance() {
		if (instance == null) {
			instance = new ChatMsgManage();
		}
		return instance;
	}

	public ArrayList<ChatMsg> getData() {
		// LogUtil.e(CHAR_TAG, chatMsgList.toString());
		return chatMsgList;
	}

	public void setData(ArrayList<ChatMsg> dataList) {
		chatMsgList.clear();
		chatMsgList.addAll(dataList);
	}

	private ChatMsgManage() {
		// 读取聊天室的本地数据
		//LogUtil.e(CHAR_TAG, "读取聊天室的本地数据\n");
		chatMsgList = new ArrayList<ChatMsg>();
		systemMsgList = new ArrayList<ChatMsg>();
		unlineChatMsgList = new ArrayList<ChatMsg>();
		/** 读取聊天记录 */
		friendMsgTip = new LongSparseArray<Boolean>();
		chatSize = 0;
		// unlineChatSize = 0;
	}

	public synchronized void addSystemChatMsg(ChatMsg chatMsg) {
		systemMsgList.add(chatMsg);
		/** 触发页面刷新 */
		sendSystemMsg();
		/** 消息栏框 */
		ToolUtils.showSystemNotification(chatMsg.getMsg());
		/* 保存到数据库中 */
		IoUtils.instance().saveChatMsg(chatMsg);
	}

	public synchronized void addMsgToFriend(ChatMsg chatMsg) {
		if (friendMessageListener != null)
			friendMessageListener.add(chatMsg);
		else {

			friendMsgTip.put(chatMsg.getSuid(), true);
			/** 触发页面刷新 */
			sendAddFriendMsg(chatMsg);
		}
		IoUtils.instance().saveChatMsg(chatMsg);

	}

	public int chatSize() {
		// LogUtil.e("chatSize:", "" + chatSize);
		// LogUtil.e("unlineChatSize:", "" + unlineChatSize);
		return chatSize + unlineChatSize;
	}

	public int getUnlineChatSize() {
		return unlineChatSize;
	}

	public void setUnlineChatSize(int unlineSize) {
		unlineChatSize = unlineSize;
		sendAddMsg();
	}

	public synchronized void addMsg(ChatMsg chatMsg) {
		totalSize++;
		if (chatMsgList.size() > 200)
			chatMsgList.remove(0);
		//chatMsgList.add(chatMsg);
		IoUtils.instance().saveChatMsg(chatMsg);
		if (messageListener != null) {
			messageListener.add();
			chatSize = 0;
		} else {
			chatSize++;
			totalSize++;
			//LogUtil.e(CHAR_TAG, "" + totalSize);
			sendAddMsg();
		}
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	// 先保存到数据库中再进行排序
	public synchronized void addUnlineMsg(ChatMsg chatMsg) {

		IoUtils.instance().saveChatMsg(chatMsg);
		totalSize++;
		//LogUtil.e(CHAR_TAG, "" + totalSize);

	}

	// 排序
	public void Listcompare() {
		TreeSet<ChatMsg> ts = new TreeSet<ChatMsg>(new Comparator<ChatMsg>() {

			@Override
			public int compare(ChatMsg lhs, ChatMsg rhs) {
				// TODO Auto-generated method stub
				return (int) (lhs.getDateline() - rhs.getDateline());
			}
		});
		// IoUtils.instance().clearChatMsgToRid(CHAR_TAG);
		chatMsgList.clear();
		long date = objectbuffer.instance().getHallPacketIml().standardDate;
		if (IoUtils.instance().getUnlineandelayOnlineMsg(date) == null)
			return;
		chatMsgList.addAll((ArrayList<ChatMsg>) IoUtils.instance()
				.getUnlineandelayOnlineMsg(date));
		IoUtils.instance().clearnUnlineandelayOnlineMsg(date);
		chatSize = 0;
		for (ChatMsg list : chatMsgList) {

			ts.add(list);
		}

		chatMsgList.clear();
		// IoUtils.instance().clearChatMsgToRid(CHAR_TAG);
		// 最多200条信息
		for (ChatMsg list : ts) {
			chatMsgList.add(list);
			if (chatMsgList.size() > 200) {
				IoUtils.instance().removeCharMsg(chatMsgList.get(0));
				chatMsgList.remove(0);
				IoUtils.instance().saveChatMsg(list);
				continue;
			}
			chatSize++;
			IoUtils.instance().saveChatMsg(list);
			// LogUtil.e(CHAR_TAG,list.getMsg());
		}

		// 显示聊天室红点
		sendAddMsg();
		ts.clear();
		unlineChatMsgList.clear();
		chatMsgList.clear();
	}

	public int getunlineChatSize() {
		return unlineChatSize;
	}

	public static void sendAddFriendMsg(ChatMsg chatMsg) {
		Intent intent = new Intent();
		intent.putExtra("uid", chatMsg.getSuid());
		intent.setAction(FRIEND_MSG_ADD_ACTION);
		// 向后台Service发送播放控制的广播
		GameApp.instance().sendBroadcast(intent);
	}

	public static void sendSystemMsg() {
		Intent intent = new Intent();
		intent.setAction(SYSTEM_MSG_ACTION);
		// 向后台Service发送播放控制的广播
		GameApp.instance().sendBroadcast(intent);
	}

	public static void sendAddMsg() {
		Intent intent = new Intent();
		intent.setAction(CHAT_MSG_ADD_ACTION);
		// 向后台Service发送播放控制的广播
		GameApp.instance().sendBroadcast(intent);
	}

	public boolean getChatTip() {
		return chatSize > 0;
	}

	public ArrayList<ChatMsg> getSystemMsgList() {
		return systemMsgList;
	}

	public LongSparseArray<Boolean> getFriendMsgTip() {
		return friendMsgTip;
	}

	public void clearSystemMsgTip() {
		systemMsgList.clear();
	}

	public void clearFriendMsgTip() {
		friendMsgTip.clear();
	}

	public void clearFriendMsgTip(long uid) {
		friendMsgTip.remove(uid);
	}

	public void clearchatMsg() {
		chatSize = 0;
		chatMsgList.clear();
		IoUtils.instance().clearChatMsgToRid(CHAR_TAG);
	}

	public void addMessageListener(MessageListener list) {
		chatSize = 0;
		this.messageListener = list;
	}

	public void addFriendMessageListener(FriendMessageListener list) {
		this.friendMessageListener = list;
	}

	public interface FriendMessageListener {
		public void add(ChatMsg chatMsg);
	}

	public interface MessageListener {
		public void add();
	}

}
