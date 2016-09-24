package com.icloud.listenbook.socket.pro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.util.Log;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.share.HallCommand;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.wrzjh.base.net.socket.packet.DataPacket;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
//处理服务器信息
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.ChatPeopleInfo;

public class HallPacketIml implements HallCommand {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private String TAG = HallPacketIml.class.getSimpleName();
	public long standardDate;
	private HallSocket hallProcess;

	public HallPacketIml(HallSocket hallProcess) {
		this.hallProcess = hallProcess;

		objectbuffer.instance().setHallPacketIml(this);
	}

	public void proCmd(DataPacket dp, int cmd) {
		dp.readBegin();// 读取内容开始
		switch (cmd) {
		case SERVER_RESP_LOGIN_OK:// 登录OK
			String ips = dp.readString();// 协议字段
			int onLine = dp.readInt();
			int siteId = dp.readInt();
			loginSucc(ips, onLine, siteId);
			IoUtils.instance().delChatPeopleInfo(UserInfo.instance().getUid());
			break;
		case SERVER_RESP_LOGIN_FAIL:// 登录FAIL

			break;
		case SERVER_RESP_ONLINE:// 在线人数
			int online = dp.readInt();
			int idCount = dp.readInt(); // ID个数
//			LogUtil.e(TAG, "idCount"+idCount);
			List<ChatPeopleInfo> idArray = new ArrayList<ChatPeopleInfo>();
			for (int i = 0; i < idCount; i++) {
				//Long interval_count, String icon, String nick, String sex, String signature, Boolean isfriend
				long uid=(long)dp.readInt();
//				LogUtil.e(TAG, "uid"+uid);
				ChatPeopleInfo info = new ChatPeopleInfo(uid,
						new Date(System.currentTimeMillis()),(long)0,null,null,null,null,null,false);
				idArray.add(info);
			}
			IoUtils.instance().saveChatPeopleInfoList(idArray);
			break;
		case SERVER_HALL_BROAD:// 用户发送广播回应(+广播)
			byte type = dp.readByte();
			String broad_msg = dp.readString();
			int uid = dp.readInt();
			HallSocketManage.instance().addChat(uid, broad_msg);
			// Log.e("用户发送广播SERVER_HALL_BROAD：", broad_msg);
			break;

		case SERVER_MATCH_TIP:

			break;
		case SERVER_FRIEND_MSG:// 好友消息
			tipFriendMsg(dp);
			break;
		case SERVER_TIP_MSG:// 系统提示弹窗
			tipSystemMsg(dp);
			break;
		case SERVER_KEFU_MSG:// 客服消息
			tipKefuMsg(dp);
			break;
		case SERVER_FRIEND_REQ:
			break;

		case SERVER_UINFO_SYNC:
			break;

		case SERVER_UNLINE_MSG:// 好友离线消息
			tipUnlineMsg(dp);
			break;
		case SERVER_BE_KICKED:// 强制下线
			int errCode = dp.readByte();
			String msg = dp.readString();
			beKicked(errCode, msg);
			break;
		case SERVER_UNLINE_BROAD_COUNT:// 聊天室离线消息数量
			short count = dp.readShort();
			// Log.e("聊天室离线消息数量", ""+count+"\n");
			ChatMsgManage.instance().setUnlineChatSize(count);
			break;
		case SERVER_UNLINE_BROAD:// 聊天室离线消息
			SharedPreferenceUtil.setHaveUnlineChatmsg(true);
			tipUnlineChat(dp);
			ChatMsgManage.instance().Listcompare();
			Intent intent = new Intent();
			intent.setAction(ChatMsgManage.UNCHAT_MSG_ADD_ACTION);
			GameApp.getContext().sendBroadcast(intent);

			break;
		}
	}

	private void tipUnlineChat(DataPacket dp) {
		// TODO Auto-generated method stub
		short len = dp.readShort();
		LogUtil.e("dp.readShort()", "" + len);
		for (short i = 0; i < len; i++) {
			String item = dp.readString();
			// LogUtil.e("离线消息chatMsg：", item);
			long date = dp.readInt64();
			date = date * 1000 + i;
			if (SharedPreferenceUtil.getHaveUnlineChatmsg()) {
				standardDate = date;
				SharedPreferenceUtil.setHaveUnlineChatmsg(false);
			}
			int suid = dp.readInt();
			byte type = dp.readByte();
			try {
				JSONObject json = new JSONObject(item);
				String msg = json.optString("M");
				String name = json.optString("N");
				LogUtil.e("离线消息msg：", "" + msg);
				LogUtil.e("离线消息name：", "" + name);
				if (type == 2) {
					HallSocketManage.instance().addUnlineChatMsg(suid, msg,
							name, date);
				} else {
					HallSocketManage.instance().addSystemChatMsg(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loginSucc(String ips, int onLine, final int siteId) {
		LogUtil.e("登陆成功loginSucc", ips + "在线人数" + onLine);
		ServerIps.setRoomAddr(ips);
		// LogUtil.d("loginSucc", ips);
	}

	// 消息列表
	// [
	// 　　{'uid':12432,'msg':'test','date':'2013-03-23 00:00:00','type':1},
	// 　　{'uid':12432,'msg':'test','date':'2013-03-23 00:00:00','type':1},
	// 　　{'uid':12432,'msg':'test','date':'2013-03-23 00:00:00','type':1},
	// ]

	/** 离线消息 **/
	private void tipUnlineMsg(DataPacket dp) {
		int len = dp.readInt();

		for (int i = 0; i < len; i++) {
			String item = dp.readString();
			// LogUtil.e("离线消息tipUserUnlineMsg：", item);
			try {
				JSONObject json = new JSONObject(item);
				int uid = json.optInt("uid");
				String msg = json.optString("msg");
				String strDate = json.optString("date");
				Date date = sdf.parse(strDate);
				if (uid == ChatMsgManage.CHAR_TAG)
					HallSocketManage.instance().addSystemChatMsg(msg,
							date.getTime());
				else
					// LogUtil.e("离线消息", "收到朋友的离线信息咯！！！\n\n");
					HallSocketManage.instance().addMsgToFriend(uid, msg,
							date.getTime());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/** 好友消息 */
	private void tipFriendMsg(DataPacket dp) {
		final int friUid = dp.readInt();
		final String msg = dp.readString();
		// LogUtil.e("收到朋友的在线信息咯！！！",friUid+" ; " +msg+"\n\n");
		HallSocketManage.instance().addMsgToFriend(friUid, msg);
	}

	/** 系统消息 */
	private void tipSystemMsg(DataPacket dp) {
		final int type = dp.readByte();
		final String msg = dp.readString();
		if (type == 66)
			ToastUtil.showMessage(msg);
		else {
			HallSocketManage.instance().addSystemChatMsg(msg);
		}
	}

	/** 客服消息 */
	private void tipKefuMsg(DataPacket dp) {
		final int type = dp.readByte();
		final String msg = dp.readString();
		HallSocketManage.instance().addSystemChatMsg(msg);

	}

	public void beKicked(int errCode, final String msg) {
		hallProcess.close();

	}

}
