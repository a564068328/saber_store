package com.itheima52.mobilesafe.engine;


import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.text.format.Formatter;


import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 宋尧
 * date on 2016/2/10.
 * des：
 */
public class TaskInfoParse {
    public static List<TaskInfo> getTaskInfo(Context context) {

        PackageManager packageManager = context.getPackageManager();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        //获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取到所有运行中的进程
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:processes){
            TaskInfo taskInfo=new TaskInfo();
            //得到进程名
            String processName = info.processName;
            taskInfo.setPackageName(processName);
            try {

                // 获取到内存基本信息
                /**
                 * 这个里面一共只有一个数据
                 */
                MemoryInfo[] memoryInfo = activityManager
                        .getProcessMemoryInfo(new int[]{info.pid});
                // Dirty弄脏
                // 获取到总共弄脏多少内存(当前应用程序占用多少内存)
                int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty()*1024;


                PackageInfo packageInfo = packageManager.getPackageInfo(
                        processName, 0);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                //获取应用程序的标记
                int flag=packageInfo.applicationInfo.flags;
                //ApplicationInfo.FLAG_SYSTEM表示系统应用
                if((ApplicationInfo.FLAG_SYSTEM&flag)!=0){
                    //表示系统APP
                    taskInfo.setIsUserApp(false);
                }else{
                    taskInfo.setIsUserApp(true);
                }
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);


                taskInfo.setAppName(appName);
                taskInfo.setIcon(drawable);
                taskInfo.setMemorySize( totalPrivateDirty);


                taskInfos.add(taskInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //给没有图标的系统核心进程设置图标
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName("系统核心进程");
            }
        }
        return taskInfos;
    }
}
