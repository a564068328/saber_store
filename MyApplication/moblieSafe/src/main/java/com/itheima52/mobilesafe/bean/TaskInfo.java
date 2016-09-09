package com.itheima52.mobilesafe.bean;

import android.graphics.drawable.Drawable;



/**
 * Created by 宋尧
 * date on 2016/2/10.
 * des：进程信息
 */
public class TaskInfo {
    private Drawable icon;
    private String packageName;
    private String appName;
    private int memorySize;
    //是系统APP还是用户APP
    private boolean isUserApp;

    //条目是否被勾选
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "packageName='" + packageName + '\'' +
                ", isUserApp=" + isUserApp +
                ", memorySize=" + memorySize +
                ", appName='" + appName + '\'' +
                '}';
    }
}
