package com.icloud.listenbook.unit;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.widget.Button;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;

public class SMSUtil {
	public static final int INIT = 0;
	public static final int DISABLE_INIT = INIT + 1;
	public static final int UP_TIME = DISABLE_INIT + 1;

	public static final String Tag = "SMSUtil";
	public static final int MAX_TIME = 120;
	int postion;
	Activity act;
	Button send;
	String tip;
	Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (act.isFinishing())
				return;
			switch (msg.what) {
			case INIT:
				initBtn();
				break;
			case DISABLE_INIT:
				disableBtn();
				break;
			case UP_TIME:
				upBtnTime();
				break;
			}
		}
	};

	public SMSUtil(Activity act, Button send) {
		this.act = act;
		this.send = send;
		tip = act.getString(R.string.delay_get_code);
		postion = 0;
	}

	protected void initBtn() {
		if (send == null)
			return;
		send.setText(R.string.get_code);
		send.setBackgroundResource(R.color.green);
		send.setEnabled(true);
	}

	protected void upBtnTime() {
		if (send == null)
			return;
		send.setText(String.format(tip, postion));
		postion -= 1;
		if (postion > 0)
			UIHandler.sendMessageDelayed(UIHandler.obtainMessage(UP_TIME),
					1 * 1000);
		else
			UIHandler.obtainMessage(INIT).sendToTarget();
	}

	protected void disableBtn() {
		if (send == null)
			return;
		send.setEnabled(false);
		send.setBackgroundResource(R.color.thingrey);
		send.setText(String.format(tip, MAX_TIME));
		postion = MAX_TIME;
		UIHandler
				.sendMessageDelayed(UIHandler.obtainMessage(UP_TIME), 1 * 1000);
	}

	public void sendSMS(String phone,int type) {
		if (phone.length() < 11) {
			ToastUtil.showMessage("手机长度不正确");
			return;
		}
		HttpUtils.getSms(phone,type,new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LogUtil.e(Tag, "getSms:" + response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						UIHandler.obtainMessage(DISABLE_INIT).sendToTarget();
					}
					String msg = response.optString("msg");
					if (!android.text.TextUtils.isEmpty(msg)) {
						ToastUtil.showMessage(msg);
					}

				} catch (Exception e) {
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtil.showMessage(R.string.network_erro);
			}
		});
	}
}
