package com.itheima52.mobilesafe.activity;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.fragment.LockFragment;
import com.itheima52.mobilesafe.fragment.UnLockFragment;

/**
 * Created by 宋尧
 * date on 2016/2/19.
 * des：程序锁界面
 */
public class AppLockActivity extends FragmentActivity {

    private FrameLayout fl_content;
    private TextView tv_unlock;
    private TextView tv_lock;
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_applock);

        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        //获取到fragment的管理者

        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();

        unLockFragment = new UnLockFragment();
        lockFragment = new LockFragment();
        /**
         * 替换界面
         * 1 需要替换的界面的id
         * 2具体指某一个fragment的对象
         * commit以后就会调用unLockFragment的生命周期方法
         */
        mTransaction.replace(R.id.fl_content, unLockFragment).commit();


        tv_unlock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                //没有加锁
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                //
                ft.replace(R.id.fl_content, unLockFragment).commit();
                System.out.println("切换到lockFragment");
            }
        });
        tv_lock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                //加锁
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                ft.replace(R.id.fl_content, lockFragment).commit();
                System.out.println("切换到unlockFragment");
            }
        });
    }
}
