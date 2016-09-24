package com.icloud.listenbook.http;

import java.net.InetAddress;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.icloud.listenbook.base.ActivityUtils;
import com.icloud.listenbook.base.OnThreadTask;
import com.icloud.listenbook.base.ThreadTask;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;

public class ServerIps {
	/*** gg.988wan.com:8083 */
	/*** 192.168.0.120/tingshu */
	/*** xuetong111.com:8083 */
	public static final String dLoginAddr = "xuetong111.com:8083";// 默认登录地址
	public static String loginAddr;// 登录地址

	public static JSONObject roomAddrs = new JSONObject();

	public static void setRoomAddr(String addrs) {
		try {
			LogUtil.d("roomAddrs", addrs);
			roomAddrs = new JSONObject(addrs);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/** 获取登录地址 **/
	public static String getLoginAddr() {
		if (loginAddr == null || loginAddr.length() == 0) {
			loginAddr = dLoginAddr;
			reSetLoginAddr();
		}
		if (loginAddr.startsWith("http://")) {
			return loginAddr;
		} else {
			loginAddr = "http://" + loginAddr;
			return loginAddr;
		}
	}

	public static void reSetLoginAddr() {
		Activity act = ActivityUtils.instance().getCurrAct();
		if (act == null)
			return;
		ThreadTask.start(act, null, true, new OnThreadTask() {
			@Override
			public String onThreadRun() {
				ServerIps.setLoginAddr();
				return null;
			}

			@Override
			public void onAfterUIRun(String result) {

			}
		});
	}

	public static void setLoginAddr() {
		loginAddr = getIP();
	}

	private static String getIP() {
		String lastIp = SharedPreferenceUtil.getLastDNSIp();
		LogUtil.d("preferece lastIp ", lastIp);
		if (lastIp != null && lastIp.length() > 10) {
			return "http://" + SharedPreferenceUtil.getLastDNSIp();
		}

		InetAddress address = null;
		try {
			address = InetAddress.getByName(FLORD_DNS);
			String ip = address.getHostAddress().toString();
			SharedPreferenceUtil.setLastDNSIp(ip);
			return "http://" + ip;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "http://" + FLORD_DNS;
	}

	private static String FLORD_DNS = "xuetong111.com:8083";

	public static String getHallIp() {
		return "xuetong111.com";
	}

	public static int getHallPort() {
		return 18600;
	}

}
