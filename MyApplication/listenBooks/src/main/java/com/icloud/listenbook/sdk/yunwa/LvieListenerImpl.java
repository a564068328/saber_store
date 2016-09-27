package com.icloud.listenbook.sdk.yunwa;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.icloud.listenbook.ui.chipAct.TeachVedioList;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.yunva.live.sdk.LiveListener;
import com.yunva.live.sdk.constant.LiveConstants;
import com.yunva.live.sdk.interfaces.logic.model.BalanceInfo;
import com.yunva.live.sdk.interfaces.logic.model.BindCpUserInfoResp;
import com.yunva.live.sdk.interfaces.logic.model.GetPlayListResp;
import com.yunva.live.sdk.interfaces.logic.model.RoomInfo;
import com.yunva.live.sdk.interfaces.logic.model.UserGiveGiftResp;
import com.yunva.live.sdk.lib.logic.WhatType;
import com.yunva.live.sdk.ui.channel.LiveRoomActivity;

public class LvieListenerImpl implements LiveListener {
	public static final String APPID = "1000069";
	private final String TAG = "LvieListenerImpl";
	Handler UIHandler;
	int balance;

	public LvieListenerImpl(Handler UiHandler) {
		this.UIHandler = UiHandler;
	}

	@Override
	public void initComplete() {
		// SDK初始化完成回调
		if (UIHandler != null) {
			UIHandler.sendEmptyMessage(WhatType.SDK_INIT_COMPLETE);
		}

	}

	@Override
	public void onBindingResp(int result, String msg) {
		// 绑定云娃账号回应
		if (result == LiveConstants.REQUEST_OK) {
			// 绑定成功
			Log.d(TAG, "绑定成功");
			if (UIHandler != null) {
				UIHandler.sendEmptyMessage(WhatType.BINDING_SUCCEE);
			}
		} else {
			Log.d(TAG, "绑定失败 : " + msg);
			if (LiveRoomActivity.mActivity != null) {
				Message message = LiveRoomActivity.mActivity.handler
						.obtainMessage(WhatType.BINDING_FAIL);
				message.obj = msg;
				message.sendToTarget();
			}
		}
	}

	@Override
	public void onDisconnectedNotify() {
		// 连接掉线通知
		if (LiveRoomActivity.mActivity != null) {
			LiveRoomActivity.mActivity.handler
					.sendEmptyMessage(WhatType.DISCONNECTED_NOTIFY);
		}
	}

	/**
	 * 去充值通知
	 */
	@Override
	public void onToRechargeNotify(Context context, String ext) {
		// 收到该通知，需要去调用游戏(或应用)的充值功能
		Toast.makeText(context, "我需要充值，请调用充值功能.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBindCpUserInfoResp(BindCpUserInfoResp bindCpUserInfoResp) {
		// 绑定cp用户信息回应

	}

	/**
	 * 获取货币余额
	 */
	@Override
	public void onGetCurrencyBalanceInfo(Handler mHandler, int what) {
		// TODO 收到该回调，这里需要游戏(或应用)将当前的用户账号余额通过handler发送通知给SDK显示
//		BalanceInfo balanceInfo = new BalanceInfo();
//		balanceInfo.setBalance(balance);// 账号余额大小
//		balanceInfo.setName("元");// 货币名称
//		Message message = mHandler.obtainMessage(what);
//		message.obj = balanceInfo;
//		message.sendToTarget();
	}

	@Override
	public void onUserBalanceNotify(Integer balance) {
		this.balance = balance;
	}

	@Override
	public void onGetPlayListResp(GetPlayListResp resp) {
		List<RoomInfo> roomInfoArray = new ArrayList<RoomInfo>();
		if (resp.getResult() == 0) {
			roomInfoArray = resp.getPlayList();
		}
		if (roomInfoArray == null)
			roomInfoArray = new ArrayList<RoomInfo>();
		if (UIHandler != null) {
			UIHandler.obtainMessage(TeachVedioList.UP_ROOM_VIEW, roomInfoArray)
					.sendToTarget();
		}
	}
}
