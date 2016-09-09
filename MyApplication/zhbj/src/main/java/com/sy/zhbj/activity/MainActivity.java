package com.sy.zhbj.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sy.zhbj.R;
import com.sy.zhbj.fragment.ContentFragment;
import com.sy.zhbj.fragment.LeftMenuFragment;

/**
 * 主页面
 *
 * @author Kevin
 *
 */
public class MainActivity extends SlidingFragmentActivity {

    private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
    private static final String FRAGMENT_CONTENT = "fragment_content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏，要在setContentView之前写
        setContentView(R.layout.activity_main);

        setBehindContentView(R.layout.left_menu);// 设置侧边栏
        SlidingMenu slidingMenu = getSlidingMenu();// 获取侧边栏对象
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸
        slidingMenu.setBehindOffset(600);// 设置预留屏幕的宽度

        initFragment();
    }

    /**
     * 初始化fragment, 将fragment数据填充给布局文件
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();// 开启事务

        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(),
                FRAGMENT_LEFT_MENU);// 用fragment替换framelayout
        transaction.replace(R.id.fl_content, new ContentFragment(),
                FRAGMENT_CONTENT);

        transaction.commit();// 提交事务
        // Fragment leftMenuFragment = fm.findFragmentByTag(FRAGMENT_LEFT_MENU);
    }

    // 获取侧边栏fragment
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm
                .findFragmentByTag(FRAGMENT_LEFT_MENU);

        return fragment;
    }

    // 获取主页面fragment
    public ContentFragment getContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment fragment = (ContentFragment) fm
                .findFragmentByTag(FRAGMENT_CONTENT);

        return fragment;
    }

}
