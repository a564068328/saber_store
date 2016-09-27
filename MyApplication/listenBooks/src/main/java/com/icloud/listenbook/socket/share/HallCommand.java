package com.icloud.listenbook.socket.share;

public interface HallCommand {

	public static final int HALL_LOGIN = 0x01;// 大厅登录
	public static final int CLIENT_HALL_LOGOUT = 0x02;// 离开大厅(游戏)
	public static final int GET_ONLINE = 0x06;// 在线人数
	public static final int CLIENT_REQ_BROAD = 0x07;// 发送小喇叭

	public static final int CLIENT_SEND_FRI_MSG = 0x11;// 发送好友消息
	public static final int CLIENT_SEND_SYS_MSG = 0x12;// 发送客服消息
	public static final int CLIENT_REQ_FRIEND = 0x13;// 邀请好友
	public static final int CLIENT_REQ_BroadRecord =0x15; // 请求离线喇叭 ,这个是你请求离线消息的请求
	public static final int CLIENT_HEART = 0x21;// 心跳

	public static final int SERVER_RESP_LOGIN_OK = 0x30;// 登录OK
	public static final int SERVER_RESP_LOGIN_FAIL = 0x31;// 登录Fail
	public static final int SERVER_SELF_LOGOUT = 0x32;// 自己离开房间成功

	public static final int SERVER_RESP_ONLINE = 0x36; // 在线人数
	public static final int SERVER_HALL_BROAD = 0x37;// 玩家发送广播回应(+广播)

	public static final int SERVER_MATCH_TIP = 0x40;// 比赛场弹窗

	public static final int SERVER_FRIEND_MSG = 0x41;// 好友消息
	public static final int SERVER_TIP_MSG = 0x42;// 系统提示弹窗
	public static final int SERVER_FRIEND_REQ = 0x43;// 好友邀请
	public static final int SERVER_KEFU_MSG = 0x44;// 客服消息
	public static final int SERVER_UINFO_SYNC = 0x4A;// 信息修改同步(增量)
	
	public static final int SERVER_ADDRS_CHANGE = 0x4B;// 房间IP切换
	public static final int SERVER_UNLINE_MSG = 0x4C;// 房间IP切换
	public static final int SERVER_UNLINE_BROAD_COUNT = 0x4D; // 离线喇叭数目
	public static final int SERVER_UNLINE_BROAD = 0x4E;// 离线喇叭消息 ，这个是离线消息请求的响应
	
	public static final int SERVER_RESP_HEART = 0x51; // 服务器回应心跳
	public static final int SERVER_BE_KICKED = 0x52;// 强制下线
	
	


}
