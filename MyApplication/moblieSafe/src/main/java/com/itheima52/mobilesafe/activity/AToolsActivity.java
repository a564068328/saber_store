package com.itheima52.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.utils.ToastUtils;
import com.itheima52.mobilesafe.utils.smsUtils;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

/**
 * 高级工具
 *
 * @author Kevin
 */
public class AToolsActivity extends Activity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 归属地查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    /**
     * 短信备份
     */
    public void smsBackUp(View v) {
        //初始化一个进度条的对话框
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在复制短信，请骚等。。。");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        new Thread() {
            public void run() {
                boolean backUp = smsUtils.backUp(AToolsActivity.this, new smsUtils.BackUpSmsCallback() {
                    @Override
                    public void befor(int count) {
                        dialog.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int curcount) {
                        dialog.setProgress(curcount);
                    }
                });

                if (backUp) {
                    //子线程刷新UI用以下方法直接实现
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份成功");
                    Looper.loop();
                } else {
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份失败");
                    Looper.loop();
                }
                Looper.prepare();
                    dialog.dismiss();
                Looper.loop();

            }
        }.start();

    }

    //程序锁
    public void appLock(View v){
        Intent intent = new Intent(this,AppLockActivity.class);
        startActivity(intent);
    }

    //广告推荐
    public void appRecomment(View v){
       // 调用方式:直接打开全屏积分墙，并且监听积分墙退出的事件onDestory
        OffersManager.getInstance(this).showOffersWall(
                new Interface_ActivityListener() {

                    /**
                     * 但积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
                     */
                    @Override
                    public void onActivityDestroy(Context context) {
                        Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
