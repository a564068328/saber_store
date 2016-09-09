package com.sy.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.sy.zhbj.base.BasePager;


/**
 * 智慧服务
 * 
 * @author Kevin
 * 
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("初始化智慧服务数据....");

		tvTitle.setText("生活");
		setSlidingMenuEnable(true);// 打开侧边栏

		TextView text = new TextView(mActivity);
		text.setText("智慧服务");
		text.setTextColor(Color.RED);
		text.setTextSize(25);
		text.setGravity(Gravity.CENTER);

		// 向FrameLayout中动态添加布局
		flContent.addView(text);
	}

}
