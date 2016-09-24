package com.icloud.listenbook.unit;

import android.content.Context;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.sunflower.FlowerCollector;

public class IflytekManage {
	private static String APPID = "appid=57b17905";

	public static void initIflyteK(Context context) {
		SpeechUtility.createUtility(context, APPID);
		
		// 开启调试模式
		FlowerCollector.setDebugMode(false);
		// 开启页面统计模式 系统默认通过OnResume 和OnPause 统计页面，
		FlowerCollector.openPageMode(false);
		// 开启自动获取位置信息
		FlowerCollector.setAutoLocation(false);
		Setting.setLocationEnable(false); //关闭定位请求
//		// 开启后会在自动捕获程序的异常信息，默认关闭。
//		FlowerCollector.setCaptureUncaughtException(false);

	}
}
