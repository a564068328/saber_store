package com.itheima52.mobilesafe.receiver;

import com.itheima52.mobilesafe.service.KillProcesWidgetService;


import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * 创建桌面小部件的步骤：
 * 1 需要在清单文件里面配置元数据
 * 2 需要配置当前元数据里面要用到xml
 *   res/xml
 * 3 需要配置一个广播接受者
 * 4 实现一个桌面小部件的xml
 *   (根据需求。桌面小控件涨什么样子。就实现什么样子)
 * @author Administrator
 *
 */
public class MyAppWidget extends AppWidgetProvider {

	/**
	 * 第一次创建的时候才会调用当前的生命周期的方法
	 *
	 * 当前的广播的生命周期只有10秒钟。
	 * 不能做耗时的操作
	 */
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.d("上MISS","onEnabled");

		Intent intent = new Intent(context,KillProcesWidgetService.class);
		context.startService(intent);
	}

	/**
	 * 当桌面上面所有的桌面小控件都删除的时候才调用当前这个方法
	 */
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Intent intent = new Intent(context,KillProcesWidgetService.class);
		context.stopService(intent);
		System.out.println("onDisabled");
	}
}
