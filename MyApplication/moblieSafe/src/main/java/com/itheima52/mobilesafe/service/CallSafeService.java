package com.itheima52.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima52.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private BlackNumberDao dao;
    private InnerReceiver innerReceiver;
    private TelephonyManager tm;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);

        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener pl = new myPhoneStateListener();
        tm.listen(pl, PhoneStateListener.LISTEN_CALL_STATE);
        //初始化短信的广播
        innerReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);
    }

    private class myPhoneStateListener extends PhoneStateListener {
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state) {
                //电话铃响的状态
                case TelephonyManager.CALL_STATE_RINGING:

                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1 全部拦截 电话拦截 + 短信拦截
                     * 2 电话拦截
                     * 3 短信拦截
                     */
                    if (mode.equals("1") || mode.equals("2")) {
                        System.out.println("挂断黑名单电话号码");

                        Uri uri = Uri.parse("content://call_log/calls");
                        //注册内容观察者，除去通话记录中的黑名单来电记录
                        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));

                        //挂断电话
                        endCall();

                    }
                    break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {

        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //  内容观察者
    private class MyContentObserver extends ContentObserver {
        private String incomingNumber;

        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);

        }
    }

    //删掉黑名单号码的来电记录
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("短信来了");

            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {// 短信最多140字节,
                // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                String messageBody = message.getMessageBody();// 短信内容
                //通过短信的电话号码查询拦截的模式
                String mode = dao.findNumber(originatingAddress);
                /**
                 * 黑名单拦截模式
                 * 1 全部拦截 电话拦截 + 短信拦截
                 * 2 电话拦截
                 * 3 短信拦截
                 */
                if (mode.equals("1")) {
                    abortBroadcast();
                } else if (mode.equals("3")) {
                    abortBroadcast();
                }
                //智能拦截模式 发票  你的头发漂亮 分词
                if (messageBody.contains("fapiao")) {
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //注销短信广播接受者
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
            innerReceiver=null;
        }
        super.onDestroy();
    }
}
