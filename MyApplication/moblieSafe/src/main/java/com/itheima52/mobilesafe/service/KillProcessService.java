package com.itheima52.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * 杀死进程的服务
 */
public class KillProcessService extends Service {

    private LockScreenReceiver lockScreenReceiver;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取到进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所以正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //锁屏广播接受者
        lockScreenReceiver = new LockScreenReceiver();
        //注册锁屏广播接受者
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, intentFilter);

        //		Timer timer = new Timer();
//
//		TimerTask task = new TimerTask() {
//
//			@Override
//			public void run() {
//				// 写我们的业务逻辑
//				System.out.println("我被调用了");
//			}
//		};
//		//进行定时调度
//		/**
//		 * 第一个参数  表示用那个类进行调度
//		 *
//		 * 第二个参数表示时间,第三个为次数
//		 */
//		timer.schedule(task, 0,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册广播
        unregisterReceiver(lockScreenReceiver);
        lockScreenReceiver=null;
    }
}
