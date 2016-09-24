package com.icloud.listenbook.unit;

import android.content.Context;

import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;

public class DataUpManage {
	// static final int Update_interval = 1000 * 60 * 60 * 12;
	static final int Update_interval = 1000 * 60 * 60 * 12 * 0;
	public static final int Version_interval = (24 * 60 * 60 * 1000);

	public static boolean isUp(Context context, String name) {
		name = context.getClass().getName() + "_" + name;
		long time = SharedPreferenceUtil.getPreference().getLong(name, 0);
		return (System.currentTimeMillis() - time) >= Update_interval;
	}

	public static void save(Context context, String key) {
		key = context.getClass().getName() + "_" + key;
		SharedPreferenceUtil.getPreference().edit()
				.putLong(key, System.currentTimeMillis()).commit();
	}

	public static void saveVerCode(String key, String value) {
		SharedPreferenceUtil.getPreference().edit().putString(key, value)
				.commit();
	}

	public static String getVerCode(String key) {
		return SharedPreferenceUtil.getPreference().getString(key, "0");
	}
}
