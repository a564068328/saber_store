package com.sy.zhbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

	public static final String SP_NAME = "config";
	
	public static void saveBoolean(Context context,String key , boolean value){
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context,String key,boolean defValue){
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getBoolean(key, defValue);
		 
	}

	public static void saveString(Context context,String key , String value){
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}

	public static String getString(Context context,String key,String defValue){
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getString(key, defValue);

	}
}
