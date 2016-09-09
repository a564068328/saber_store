package com.itheima52.mobilesafe.bean;

/**
 * ============================================================
 * <p/>
 * 版     权 ： 黑马程序员教育集团版权所有(c) 2015
 * <p/>
 * 作     者  :  马伟奇
 * <p/>
 * 版     本 ： 1.0
 * <p/>
 * 创 建日期 ： 2015/2/27  9:52
 * <p/>
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public class BlackNumberInfo {
    /**
     * 黑名单电话号码
     */
    private String number;
    /**
     * 黑名单拦截模式
     * 1 全部拦截 电话拦截 + 短信拦截
     * 2 电话拦截
     * 3 短信拦截
     */
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
