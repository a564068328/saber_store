package com.itheima52.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;

/**
 * 备份短信工具类
 */
public class smsUtils {


    public interface BackUpSmsCallback {
        void befor(int count);

        void onBackUpSms(int curcount);
    }

    public static boolean backUp(Context context, BackUpSmsCallback backup) {

        //判断sd卡状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //得到短信的内容观察者
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            // type = 1 接收短信
            // type = 2 发送短信
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            if (cursor == null) {
                return false;
            }
            //获取当前一共有多少条短信
            int count = cursor.getCount();
            int curcount = 0;
            backup.befor(count);
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "smsBackUp.xml");
                FileOutputStream os = new FileOutputStream(file);
                //得到xml序列化器 在android系统中所有xml解析都是pull解析
                XmlSerializer xs = Xml.newSerializer();
                // 把短信序列化到sd卡然后设置编码格式
                xs.setOutput(os, "utf-8");
                //存储短信
                //第二个参数表示是否为独立文件，true表示为独立文件
                xs.startDocument("utf-8", true);
                //设置开始的节点，第一个参数为命名空间
                xs.startTag(null, "smss");
                //设置smss节点上面的属性值 第二个参数是名字。第三个参数是值
                xs.attribute(null, "size", String.valueOf(count));
                while (cursor.moveToNext()) {
                    // System.out.println("---------------------");
                    //System.out.println("号码"+cursor.getString(0));
                    //System.out.println("日期："+cursor.getString(1));
                    //System.out.println(cursor.getString(2));
                    //System.out.println("内容"+cursor.getString(3));
                    xs.startTag(null, "sms");
                    xs.startTag(null, "address");
                    if (TextUtils.isEmpty(cursor.getString(0))) {
                        xs.text(" ");
                    } else {
                        xs.text(cursor.getString(0));
                    }
                    xs.endTag(null, "address");
                    xs.startTag(null, "date");
                    if (TextUtils.isEmpty(cursor.getString(1))) {
                        xs.text(" ");
                    } else {
                        xs.text(cursor.getString(1));
                    }
                    xs.endTag(null, "date");
                    xs.startTag(null, "type");
                    if (TextUtils.isEmpty(cursor.getString(2))) {
                        xs.text(" ");
                    } else {
                        xs.text(cursor.getString(2));
                    }
                    xs.endTag(null, "type");
                    xs.startTag(null, "body");
                    if (TextUtils.isEmpty(cursor.getString(3))) {
                        xs.text(" ");
                    } else {
                        //String str=ResolvingStringToXmlUtils.TransitionString(cursor.getString(3));
                        //读取短信的内容
                        /**
                         * 加密：第一个参数表示加密种子(密钥)
                         *     第二个参数表示加密的内容
                         */
                        xs.text(Crypto.encrypt("123", cursor.getString(3)));

                    }
                    xs.endTag(null, "body");
                    xs.endTag(null, "sms");

                    curcount++;
                    backup.onBackUpSms(curcount);
                    SystemClock.sleep(20);

                }

                xs.endTag(null, "smss");
                xs.endDocument();

                os.flush();

                os.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }

        return false;
    }
}
