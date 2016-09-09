package com.itheima52.mobilesafe.utils;

import android.text.TextUtils;

/**
 * Created by 宋尧
 * date on 2016/2/6.
 * des：解决字符串存入XML时"<",">"等字符XML解析错误
 */
public class ResolvingStringToXmlUtils {
    public static String TransitionString(String str){

        if(TextUtils.isEmpty(str)){
            return "";
        }

        str.replace("<","&lt;");
        str.replace(">","&gt;");
        str.replace("&","&amp;");
        str.replace("'","&apos;");
        str.replace("\"","&quot;");
        return str;
    }
}
