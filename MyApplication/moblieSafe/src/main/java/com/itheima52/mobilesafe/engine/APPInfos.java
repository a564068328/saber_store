package com.itheima52.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.text.format.Formatter;

import com.itheima52.mobilesafe.bean.APPInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/1/29.
 */
public class APPInfos {
    public static List<APPInfo> getAppInfos(Context context){

        List<APPInfo> infos=new ArrayList<APPInfo>();
        //获取包的管理器
        PackageManager manager=context.getPackageManager();
        //获取到安装包
        List<PackageInfo> installedPackages=manager.getInstalledPackages(0);

        for(PackageInfo installedPackage:installedPackages){
            APPInfo info=new APPInfo();
            //获取应用程序的图标
            Drawable drawable=installedPackage.applicationInfo.loadIcon(manager);
            //获取应用程序的名称
            String apkName=installedPackage.applicationInfo.loadLabel(manager).toString();
            //获取应用程序的包名
            String packageName =installedPackage.packageName;
            //获取应用程序的资源路径
            String sourceDir =installedPackage.applicationInfo.sourceDir;
            //获取应用程序的uid
            int uid=installedPackage.applicationInfo.uid;

            File file = new File(sourceDir);
            //获取应用程序的大小
            long apkSize =file.length();
            //获取应用程序的标记
            int flag=installedPackage.applicationInfo.flags;
            //ApplicationInfo为包管理器中的信息
            if((ApplicationInfo.FLAG_SYSTEM&flag)!=0){
                //表示系统APP
                info.setUserApp(false);
            }else{
                info.setUserApp(true);
            }
            if((ApplicationInfo.FLAG_EXTERNAL_STORAGE&flag)!=0){
                //表示在SD卡
                info.setIsRom(false);
            }else{
                //rom中
                info.setIsRom(true);
            }
            //判断流量大小
            if(TrafficStats.getUidRxBytes(uid)+TrafficStats.getUidTxBytes(uid)>1024)
            {
                info.setUesTrafficIsZero(false);
                info.setUpTraffic("上传"+Formatter.formatFileSize(context,
                        TrafficStats.getUidTxBytes(uid)));
                info.setDownTraffic("下载"+Formatter.formatFileSize(context,
                        TrafficStats.getUidRxBytes(uid)));
                info.setTatolTraffic(Formatter.formatFileSize(context,
                        TrafficStats.getUidTxBytes(uid)+TrafficStats.getUidRxBytes(uid)));
            }else{
                info.setUesTrafficIsZero(true);
            }

            info.setIcon(drawable);
            info.setApkName(apkName);
            info.setApkPackageName(packageName);
            info.setApkSize(apkSize);

            infos.add(info);
        }
        return infos;
    }
}
