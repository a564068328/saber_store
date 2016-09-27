package com.icloud.listenbook.sdk.yunwa;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.View;

import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.chipAct.TeachVedioList;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.yunva.live.sdk.LiveListener;
import com.yunva.live.sdk.YayaLiveApi;
import com.yunva.live.sdk.interfaces.logic.model.CurrencyInfo;
import com.yunva.live.sdk.interfaces.logic.model.RoomInfo;
import com.yunva.live.sdk.lib.logic.WhatType;

public class yunwaSDKInit {
	private String TAG="com.icloud.listenbook.sdk.yunwa.yunwaSDKInit";
	private static yunwaSDKInit yunwaSDK;
	private Activity act;
	private YayaLiveApi yunvaLive = null;
	private LiveListener listener;
    private boolean yunwaSDKInitOK=false;
	private yunwaSDKInit(Activity act) {
		this.act = act;
	}

	public static yunwaSDKInit instance(Activity act) {
		if (yunwaSDK == null) {
			yunwaSDK = new yunwaSDKInit(act);
		}
		return yunwaSDK;

	}

	public void init() {
		// LvieListenerImpl调用了 SDK初始化完成回调方法initComplete
		// 回调完成后发消息给了这里activity的Handler
		listener = new LvieListenerImpl(UIHandler);
		Display display = act.getWindowManager().getDefaultDisplay();
		// 初始化yayaSDK
		yunvaLive = YayaLiveApi.getInstance(act, LvieListenerImpl.APPID,
				display.getWidth(), display.getHeight(), listener, true, false);
	}
	
	Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
//			if (TeachVedioList.this.isFinishing())
//				return;
			switch (msg.what) {
			case WhatType.DISCONNECTED_NOTIFY:
			case WhatType.SDK_INIT_COMPLETE:
				/*** sdk 初始化完成 自动登陆 */
				if (yunvaLive != null) {

					try {
						// JSONObject jb = new JSONObject();
						// jb.put("uid", UserInfo.instance().getUid());
						// jb.put("token", UserInfo.instance().getToken());
						String jb = "token=" + UserInfo.instance().getToken()
								+ "&uid=" + UserInfo.instance().getUid();
						yunvaLive.binding(jb.toString());
//						yyTag.setText(R.string.yy_load_01);
//						yyTag.setVisibility(View.VISIBLE);
						LogUtil.e(TAG, "呀呀sdk 初始化完成 自动登陆 ");
					} catch (Exception e) {
					}

				}
				break;
			case WhatType.BINDING_SUCCEE:// 登录账号成功
				/** 获取播放列表 */
				if (yunvaLive != null) {
					yunvaLive.getPlayListReq();
				}
				/** 设置支付 */
				{
					yunvaLive.setConsumptionType("2");
					yunvaLive.setRechargeType(0);
					List<CurrencyInfo> list = new ArrayList<CurrencyInfo>();
					list.add(new CurrencyInfo("47", "元"));
					yunvaLive.setCurrencyInfoList(list);
					yunvaLive.setUserHeadUrl(ServerIps.getLoginAddr()
							+ UserInfo.instance().getIcon());
				}
				LogUtil.e(TAG, "呀呀sdk 登录账号成功 ");
				yunwaSDKInitOK=true;
//				yyTag.setText(R.string.yy_load_02);
//				yyTag.setVisibility(View.VISIBLE);
				break;
			case WhatType.BINDING_FAIL:
//				yyTag.setText(R.string.yy_load_erro);
//				yyTag.setVisibility(View.VISIBLE);
//				ToastUtil.showMessage("获取失败,请稍后再试!");
				UIHandler.sendEmptyMessageDelayed(WhatType.SDK_INIT_COMPLETE, 1000);
				LogUtil.e(TAG, "呀呀sdk 获取失败,请稍后再试! ");
				break;
//			case UP_ROOM_VIEW:
//				yyTag.setText(R.string.yy_load_03);
//				upRoomInfo((ArrayList<RoomInfo>) msg.obj);
//				yyTag.setVisibility(View.GONE);
//				break;
			}
		};
	};
    public boolean getstate(){
    	
		return yunwaSDKInitOK;
		
    }
}
