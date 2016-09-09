package com.itheima52.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by asus on 2016/1/29.
 */
public class APPInfo {
    //Drawable可以表示可以放在drawable目录下的资源（图片，xml文件等）Bitmap只表示图片
    private Drawable icon;
    //private Bitmap icon;
    private String apkName;

    private long apkSize;
    //是用户app还是系统app
    private boolean userApp;

    //存放位置，是在Rom还是在SD
    private boolean isRom;
    //应用程序的UID
    private int uid;
    //使用的流量是否为0
    private boolean uesTrafficIsZero;
    //上传的流量
    private String upTraffic;
    //下载的流量
    private String downTraffic;
    //总流量
    private String tatolTraffic;

    private String apkPackageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean getUesTrafficIsZero() {
        return uesTrafficIsZero;
    }

    public void setUesTrafficIsZero(boolean uesTrafficIsZero) {
        this.uesTrafficIsZero = uesTrafficIsZero;
    }

    public String getUpTraffic() {
        return upTraffic;
    }

    public void setUpTraffic(String upTraffic) {
        this.upTraffic = upTraffic;
    }

    public String getDownTraffic() {
        return downTraffic;
    }

    public void setDownTraffic(String downTraffic) {
        this.downTraffic = downTraffic;
    }

    public String getTatolTraffic() {
        return tatolTraffic;
    }

    public void setTatolTraffic(String tatolTraffic) {
        this.tatolTraffic = tatolTraffic;
    }

    @Override
    public String toString() {
        return "APPInfo{" +
                "apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }
}
