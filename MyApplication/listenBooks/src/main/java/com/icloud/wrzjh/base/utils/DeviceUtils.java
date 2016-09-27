package com.icloud.wrzjh.base.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @description
 * @project: AndroidDemos
 * @Date:2012-12-4
 * @version 1.0
 */
public class DeviceUtils {
	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public static Map<String, String> collectDeviceInfo(Context ctx) {
		Map<String, String> tInfos = new HashMap<String, String>();
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				tInfos.put("versionName", versionName);
				tInfos.put("versionCode", versionCode);

				LogUtil.i(versionName + "*" + versionCode);
			}
		} catch (NameNotFoundException e) {
			LogUtil.e("an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				tInfos.put(field.getName(), field.get(null).toString());
				LogUtil.i(field.getName() + "*" + field.get(null).toString());
			} catch (Exception e) {
				LogUtil.e("an error occured when collect crash info", e);
			}
		}
		return tInfos;
	}

	/**
	 * px转sp
	 * */
	public static float pixelsToSp(Context context, float px) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px / scaledDensity;
	}

	public static String getDeviceName(Context pContext) {
		Build build = new Build();
		// build.
		TelephonyManager tm = (TelephonyManager) pContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		LogUtil.e("info", sb.toString());

		return null;
	}

	public static String getDeviceId(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getDeviceModel(Context mContext) {
		return Build.MODEL;
	}

	/**
	 * 获得imei，如果无法获得会返回wifi mac。如果都没有，返�?"
	 * 
	 * @return
	 */
	public static String getMachineId(Context mContext) {
		String imei = null;
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			imei = telephonyManager.getDeviceId();
		}
		if (imei == null) {
			WifiManager mgr = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			if (mgr != null) {
				WifiInfo wifiinfo = mgr.getConnectionInfo();
				if (wifiinfo != null) {
					imei = wifiinfo.getMacAddress();
				}
			}
		}
		if (imei == null) {
			imei = "";
		}
		return imei;
	}
}
