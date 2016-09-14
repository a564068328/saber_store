package com.naman14.timber.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.utils.ScreenUtil;

/**
 * Created by Administrator on 2016/9/13.
 * 创建一个BottomPopView类，将popupwindow的创建和一系列初始化操作封装到内部进行，
 * 提供一些回调方法，供外部自定义按钮的文字和事件即可。
 */
public abstract class BottomPopView {
    private Context mContext;
    private View anchor;
    private LayoutInflater mInflater;
    private TextView mTvTop;
    private TextView mTvBottom;
    private TextView mTvCancel;
    private PopupWindow mPopupWindow;
    private TextView tv_title;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    Window window;
    boolean isDarkTheme;
    /**
     * @param context
     * @param anchor  依附在哪个View下面
     */
    public BottomPopView(Activity context, View anchor) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.anchor = anchor;
        windowManager = context.getWindowManager();
        window = context.getWindow();
        params = context.getWindow().getAttributes();
        init();
    }

    public void init() {
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("dark_theme", false);
        View view = mInflater.inflate(R.layout.bottom_pop_window, null);
        params.dimAmount = 0.5f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        mTvBottom = (TextView) view.findViewById(R.id.tv_choose_photo);
        mTvTop = (TextView) view.findViewById(R.id.tv_take_photo);
        mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        mTvTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onTopButtonClick();
            }
        });
        mTvBottom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBottomButtonClick();
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPopupWindow = new PopupWindow(view, ScreenUtil.getScreenWidth(mContext), LinearLayout.LayoutParams.WRAP_CONTENT);
        //监听PopupWindow的dismiss，当dismiss时屏幕恢复亮度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                window.setAttributes(params);
            }
        });
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 动画效果 从底部弹起
        mPopupWindow.setAnimationStyle(R.style.popWindow_animation);
        if(!isDarkTheme){
            mTvTop.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryLightDefault));
            mTvBottom.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryLightDefault));
            mTvCancel.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryLightDefault));
            tv_title.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryLightDefault));
        }else{
            mTvTop.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryDarkDefault));
            mTvBottom.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryDarkDefault));
            mTvCancel.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryDarkDefault));
            tv_title.setBackground(ContextCompat.getDrawable(mContext,R.color.colorPrimaryDarkDefault));
        }
        mTvTop.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryBlack));
        mTvBottom.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryBlack));
        mTvCancel.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryBlack));
        tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryBlack));
    }

    /**
     * 显示底部对话框
     */
    public void show() {
        mPopupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
        params.alpha = 0.5f;
        window.setAttributes(params);
    }

    /**
     * 第一个按钮被点击的回调
     */
    public abstract void onTopButtonClick();

    /**
     * 第二个按钮被点击的回调
     */
    public abstract void onBottomButtonClick();

    public void setTopText(String text) {
        mTvTop.setText(text);
    }

    public void setBottomText(String text) {
        mTvBottom.setText(text);
    }
    public void dismiss(){
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }
}