package com.icloud.listenbook.base;

import android.os.Environment;

/***
 * 和服务器交互常量配置
 */
public class ProjectConfig {

	public static final byte PLATFORM_ID = 0x03;

	/** 网络类型 */
	public static final byte NET_STATE_WIFI = 0x01;
	public static final byte NET_STATE_2G = 0x02;
	public static final byte NET_STATE_3G = 0x03;
	public static final byte NET_STATE_4G = 0x04;

	/** 场次类型 **/
	public static final int SERVER_GAME_PRIMARY = 0x01;// 初级
	public static final int SERVER_GAME_MIDDLE = 0x02;// 中级
	public static final int SERVER_GAME_SENIOR = 0x03;// 高级
	public static final int SERVER_GAME_FA = 0x04;// 发哥场
	public static final int SERVER_GAME_Close_Look = 0x0b;// 禁看场
	public static final int SERVER_MATCH_SENIOR = 0x05;// 高级比赛场
	public static final int SERVER_GAME_TOOL = 0x06;// 道具 场
	public static final int SERVER_GAME_CloseLook = 0x0b;// 秒杀场
	public static final int SERVER_GAME_BIGMATCH = 0x0c;// 大奖赛

	/** 活动场 - 元宝 **/
	public static final int SERVER_ACT_YUANBAO = 0x07;
	/** 私人房间 **/
	public static final int SERVER_PRIVATE = 0x08;
	// 斗牛场site ID
	public static final int SERVER_NiuNiu = 0x09;

	public static final int SERVER_FRUIT = 0x0a;
	public static final int SERVER_MERRY = 0x0e;
	/** 道具物品id **/
	public static final int TOOL_KICK = 4;
	public static final int TOOL_CHANGE = 5;
	public static final int TOOL_NO_CMP = 6;
	public static final int TOOL_BROADCAST = 7;
	public static final int TOOL_CMP_5 = 8;
	public static final int TOOL_CMP_10 = 18;

	/** 礼物物品id **/
	public static final int GIFT_FLOWER = 0x01;
	public static final int GIFT_EGG = 0x02;
	public static final int GIFT_CAR = 0x03;
	public static final int GIFT_SHIP = 0x04;
	public static final int GIFT_PLANE = 0x05;

	/** 礼物金币 **/
	public static final int GIFT_FLOWER_MONEY = 3000;
	public static final int GIFT_EGG_MONEY = 3000;
	public static final int GIFT_CAR_MONEY = 60000;
	public static final int GIFT_SHIP_MONEY = 300000;
	public static final int GIFT_PLANE_MONEY = 500000;

	/** 智能托管模式 **/
	public static final byte UNLINE_MODE_1 = 0x01;
	public static final byte UNLINE_MODE_2 = 0x02;
	public static final byte UNLINE_MODE_3 = 0x03;

	public static final byte ROOM_USER_UNREADY = 0x00;// 用户在房间,没准备
	public static final byte ROOM_USER_READY = 0x01;// 用户在房间,准备
	public static final byte ROOM_USER_PLAY = 0x02;// 用户在房间,在玩
	public static final byte ROOM_USER_PLAY_CURR = 0x03;// 用户在房间,在玩
	public static final byte ROOM_USER_PLAY_GIVEUP = 0x04;// 用户在房间,在玩

	public static final byte ROOM_STATE_WAIT = 0x01;// 房间,不在玩
	public static final byte ROOM_STATE_PLAY = 0x02;// 房间,在玩

	public static final String ICON_CACHE = Environment
			.getExternalStorageDirectory() + "/wrzjh/";

	public static final String MARKET_CACHE = Environment
			.getExternalStorageDirectory() + "/.icloudwr/";

	public static final String MUSIC_CACHE = Environment
			.getExternalStorageDirectory() + "/wrzjh/music/";

}
