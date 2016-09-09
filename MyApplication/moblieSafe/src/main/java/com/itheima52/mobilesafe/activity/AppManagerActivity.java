package com.itheima52.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.bean.APPInfo;
import com.itheima52.mobilesafe.engine.APPInfos;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;


import java.util.ArrayList;
import java.util.List;


/**
 * 软件管理页面
 */
public class AppManagerActivity extends Activity {

    private List<APPInfo> list;
    private List<APPInfo> userApplist;
    private List<APPInfo> sysApplist;
    private PopupWindow window;
    private APPInfo clickAppInfo;
    //利用XUtils架包的注解来findViewById
    @ViewInject(R.id.app_list_view)
    ListView app_list_view;
    @ViewInject(R.id.tv_rom)
    TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    TextView tv_sd;
    @ViewInject(R.id.ll_appmanegerpb)
    LinearLayout ll_appmanegerpb;
    @ViewInject(R.id.tv_app)
    TextView tv_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //注册卸载APP接收广播接受者,这里没做什么具体动作
        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

        initUI();
        initData();
    }

    class adapterbound {

        private ImageView iv_maneger;


        private TextView tv_appname;


        private TextView tv_applocation;


        private TextView tv_appsize;

    }


    private class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size() + 2;
        }

        @Override
        public Object getItem(int i) {
            if (i == 0) {
                return null;
            } else if (i == userApplist.size() + 1) {
                return null;
            }

            APPInfo info = null;
            if (i < userApplist.size() + 1) {
                info = userApplist.get(i - 1);
            } else {
                int location = 1 + userApplist.size() + 1;
                info = sysApplist.get(location);
            }
            return info;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //处理两个TextView的特殊条目，i=0为用户程序的开始
            if (i == 0) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setText("用户程序(" + userApplist.size() + ")");
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLUE);
                return tv;
            }
            //i=userApplist.size()+1为系统程序的开始
            else if (i == userApplist.size() + 1) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLUE);
                tv.setText("系统程序(" + sysApplist.size() + ")");
                return tv;
            }

            APPInfo info = null;
            if (i < userApplist.size() + 1) {
                info = userApplist.get(i - 1);
            } else {
                int location = 1 + userApplist.size() + 1;
                info = sysApplist.get(i - location);
            }

            adapterbound bound = null;
            //view instanceof LinearLayout为判断view对象是否为LinearLayout的子类对象，过滤掉两个TextView的特殊对象
            //为什么要过滤呢？暂时不太清楚
            if (view != null && view instanceof LinearLayout) {
                bound = (adapterbound) view.getTag();
            } else {

                view = View.inflate(AppManagerActivity.this, R.layout.item_maneger_view, null);

                bound = new adapterbound();
                bound.iv_maneger = (ImageView) view.findViewById(R.id.iv_maneger);
                bound.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                bound.tv_applocation = (TextView) view.findViewById(R.id.tv_applocation);
                bound.tv_appsize = (TextView) view.findViewById(R.id.tv_appsize);
                view.setTag(bound);
            }
            bound.iv_maneger.setBackground(info.getIcon());
            bound.tv_appname.setText(info.getApkName());
            bound.tv_appsize.setText(Formatter.formatFileSize(AppManagerActivity.this, info.getApkSize()));

            if (info.isRom()) {
                bound.tv_applocation.setText("在内存中");
            } else {

                bound.tv_applocation.setText("在SD卡中");
            }
            return view;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            ll_appmanegerpb.setVisibility(View.INVISIBLE);
            app_list_view.setAdapter(new myAdapter());
        }
    };

    private void initData() {
        //得到所有安装到手机的APP信息
        new Thread() {
            public void run() {
                list = APPInfos.getAppInfos(AppManagerActivity.this);
                //将list拆分成用户程序list和系统程序list
                userApplist = new ArrayList<APPInfo>();
                sysApplist = new ArrayList<APPInfo>();
                for (APPInfo info : list) {
                    if (info.isUserApp()) {
                        userApplist.add(info);
                    } else {
                        sysApplist.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        //利用XUtils架包来注入view和事件
        ViewUtils.inject(this);
        //获取到ram剩余大小
        long ram_freespace = Environment.getDataDirectory().getFreeSpace();
        long sd_freespace = Environment.getExternalStorageDirectory().getFreeSpace();
        //格式化ram_freespace，sd_freespace
        tv_rom.setText("内存可用：" + Formatter.formatFileSize(this, ram_freespace));
        tv_sd.setText("SD卡可用" + Formatter.formatFileSize(this, sd_freespace));
        //显示加载圈圈
        ll_appmanegerpb.setVisibility(View.VISIBLE);
        //监听listview的滚动事件,已达到提醒用户当前屏幕的第一个条目是用户app还是系统app
        app_list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /**
             *
             * @param absListView
             * @param i   第一个可见条目的位置
             * @param i1  一页可以展示条目数量
             * @param i2  总的条目数量
             */
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                //去掉之前的popPopupWindow
                if (window != null && window.isShowing()) {
                    window.dismiss();
                    window = null;
                }
                if (userApplist != null && sysApplist != null) {
                    if (i > userApplist.size() + 1) {
                        tv_app.setText("系统程序(" + sysApplist.size() + ")");
                    } else {
                        tv_app.setText("用户程序(" + userApplist.size() + ")");
                    }
                }

            }
        });
        //使用popPopupWindow窗口控件
        app_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //获取当前点击的listview对象
                Object obj = app_list_view.getItemAtPosition(i);

                if (obj != null && obj instanceof APPInfo) {
                    clickAppInfo = (APPInfo) obj;

                    View contentview = View.inflate(AppManagerActivity.this, R.layout.item_popupwindom, null);

                    LinearLayout ll_uninstall = (LinearLayout) contentview.findViewById(R.id.ll_uninstall);

                    LinearLayout ll_share = (LinearLayout) contentview.findViewById(R.id.ll_share);

                    LinearLayout ll_start = (LinearLayout) contentview.findViewById(R.id.ll_start);

                    LinearLayout ll_detail = (LinearLayout) contentview.findViewById(R.id.ll_detail);

                    //卸载
                    ll_uninstall.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                            startActivity(uninstall_localIntent);
                            //去掉之前的popPopupWindow
                            if (window != null && window.isShowing()) {
                                window.dismiss();
                                window = null;
                            }
                        }
                    });

                    //分享
                    ll_share.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent share_localIntent = new Intent("android.intent.action.SEND");
                            share_localIntent.setType("text/plain");
                            share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                            share_localIntent.putExtra("android.intent.extra.TEXT",
                                    "Hi！推荐您使用软件：" + clickAppInfo.getApkName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPackageName());
                            AppManagerActivity.this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                        }
                    });

                    //运行
                    ll_start.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent start_localIntent = AppManagerActivity.this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                            AppManagerActivity.this.startActivity(start_localIntent);
                            //去掉之前的popPopupWindow
                            if (window != null && window.isShowing()) {
                                window.dismiss();
                                window = null;
                            }
                        }
                    });

                    //详情
                    ll_detail.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent detail_intent = new Intent();
                            detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                            detail_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                            startActivity(detail_intent);
                            //去掉之前的popPopupWindow
                            if (window != null && window.isShowing()) {
                                window.dismiss();
                                window = null;
                            }
                        }
                    });

                    //去掉之前的popPopupWindow
                    if (window != null && window.isShowing()) {
                        window.dismiss();
                        window = null;
                    }
                    //创建PopupWindow对象，第二三个参数为contentview的宽高
                    window = new PopupWindow(contentview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //获取view展示到窗口的位置
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    //注意：使用PopupWindow必须设置背景，这里设成透明色，不然没有动画效果！！
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                     //显示PopupWindow 第一个为父view，第二个为显示位置，第三四个为到坐标轴的xy距离
                    window.showAtLocation(adapterView, Gravity.LEFT + Gravity.TOP, 150, location[1]);
                    //缩放效果动画
                    ScaleAnimation sa = new ScaleAnimation
                            (0.3f, 1f, 0.3f, 1f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.3f);
                    sa.setDuration(300);
                    contentview.startAnimation(sa);
                }

            }
        });


    }

    @Override
    protected void onDestroy() {

        //去掉之前的popPopupWindow
        if (window != null && window.isShowing()) {
            window.dismiss();
            window = null;
        }
        super.onDestroy();
    }

    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("上miss", "接收到广播了");
        }
    }
}
