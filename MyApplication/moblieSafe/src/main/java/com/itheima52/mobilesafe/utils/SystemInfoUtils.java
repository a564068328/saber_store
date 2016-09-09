package com.itheima52.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by 宋尧
 * date on 2016/2/10.
 * des：获取总RAM内存和可用Ram内存
 */
public class SystemInfoUtils {

    private BufferedReader br;

    //获取到所有运行中的进程个数
    public int getRunningTaskCount(Context context) {
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到所有运行中的进程
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        return processes.size();
    }


    /**
     * 获取总RAM内存和可用Ram内存
     */
    public void getRamInfo(Context context, String[] str) {
        if (str.length < 2) {
            throw new IllegalArgumentException();
        }
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到所有运行中的进程
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        //得到内存的基本信息
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        //long totalMem = memoryInfo.totalMem; 这个地方不能直接跑到低版本的手机上
        ///proc/meminfo为配置文件的路径
        StringBuilder builder = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            String readline = br.readLine();
            for (char c : readline.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    builder.append(c);
                }
            }
            long totalMem = Long.parseLong(builder.toString()) * 1024;
            str[0] = Formatter.formatFileSize(context, availMem);
            str[1] = Formatter.formatFileSize(context, totalMem);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
