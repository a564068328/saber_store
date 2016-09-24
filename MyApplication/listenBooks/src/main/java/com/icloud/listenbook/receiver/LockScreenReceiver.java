package com.icloud.listenbook.receiver;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.VolleyUtils;
import com.icloud.listenbook.http.datas.HttpConfig;
import com.icloud.listenbook.ui.LoadingActivity;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.Article;

public class LockScreenReceiver extends BroadcastReceiver {
	private static final int DELTA = 1 * 24 * 60 * 60 * 1000;
	private static final int NOTIFY_ID = 123589;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 当解除锁屏 or 链接网络 or 系统状态修改
		if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())
				|| intent.getAction().equals(
						"android.net.conn.CONNECTIVITY_CHANGE")
				|| intent.getAction().equals(
						"android.media.RINGER_MODE_CHANGED")) {
			LogUtil.e("LockScreenReceiver", intent.getAction());
			long last = SharedPreferenceUtil.getLastPull();
			long delta = System.currentTimeMillis() - last;
			if (delta >= 1 * LockScreenReceiver.DELTA
					&& HttpUtils.isNetworkAvailable(context)) {
				checkAds(context);
			}

		}
	}

	private void checkAds(final Context ctx) {
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.appToast, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							int result = json.optInt("result", -1);
							if (result == 0) {
								/*** 保存启动时间 为了不多次显示 */
								SharedPreferenceUtil.saveLastPull(System
										.currentTimeMillis());
								String msg = json.optString("msg", "");
								String title = json.optString("title", "");
								JSONObject list = json.optJSONObject("list");
								Intent intent = new Intent(
										TablesActivity.OPEN_MEDIA);
								intent.setClass(ctx, LoadingActivity.class);
								intent.putExtra("data", list.toString());
								ToolUtils.showNotification(ctx, NOTIFY_ID,
										title, msg, intent,
										Notification.FLAG_AUTO_CANCEL);
							}
						} catch (Exception e) {
						}
					}
				}, null);
	}
}
