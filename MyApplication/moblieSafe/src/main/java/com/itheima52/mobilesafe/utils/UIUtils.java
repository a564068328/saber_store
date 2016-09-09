package com.itheima52.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 安全弹吐司的工具类
 */
public class UIUtils {
	public static void showToast(final Activity context,final String msg){
		if("main".equals(Thread.currentThread().getName())){
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}else{
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
