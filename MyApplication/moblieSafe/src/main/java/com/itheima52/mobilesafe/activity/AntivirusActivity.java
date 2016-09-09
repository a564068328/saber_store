package com.itheima52.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.db.dao.AntivirusDao;
import com.itheima52.mobilesafe.utils.MD5Utils;

import java.util.List;

/**
 * 病毒查杀界面
 */
public class AntivirusActivity extends Activity {

    // 扫描开始
    protected static final int BEGING = 1;
    // 扫描中
    protected static final int SCANING = 2;
    // 扫描结束
    protected static final int FINISH = 3;
    private Message message;
    private int size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case BEGING:
                    tv_init_virus.setText("初始化八核引擎");
                    break;

                case SCANING:
                    // 病毒扫描中：
                    TextView child = new TextView(AntivirusActivity.this);

                    scanInfo scanInfo = (scanInfo) msg.obj;
                    // 如果为true表示有病毒
                    if (scanInfo.desc) {
                        child.setTextColor(Color.RED);

                        child.setText(scanInfo.appName + "有病毒");

                    } else {

                        child.setTextColor(Color.BLACK);
//					// 为false表示没有病毒
                        child.setText(scanInfo.appName + "扫描安全");


                    }

                    ll_content.addView(child,0);
                    //自动滚动
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            //一直往上面进行滚动

                            scrollView.fullScroll(scrollView.FOCUS_UP);

                        }
                    });


                    System.out.println(scanInfo.appName + "扫描安全");
                    break;
                case FINISH:
                    // 当扫描结束的时候。停止动画
                    iv_scanning.clearAnimation();
                    final AlertDialog.Builder dialog= new AlertDialog.Builder(AntivirusActivity.this);
                    dialog.setTitle("病毒查杀完毕");
                    dialog.setMessage("共扫描" + size + "个程序，未发现病毒，您的手机很安全。");
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.show();
                    break;
            }

        }
    };
    private void initData() {
        new Thread() {
            @Override
            public void run() {

                message = Message.obtain();
                message.what = BEGING;
                handler.sendMessage(message);

                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedpackages = packageManager.getInstalledPackages(0);
                // 返回手机上面安装了多少个应用程序
                size = installedpackages.size();
                // 设置进度条的最大值
                pb.setMax(size);

                int progress = 0;
                for (PackageInfo info : installedpackages) {
                    scanInfo scaninfo = new scanInfo();
                    //获得app名字
                    String appName = info.applicationInfo.loadLabel(packageManager).toString();
                    scaninfo.appName = appName;
                    scaninfo.appPackageName=info.applicationInfo.packageName;
                    //获得app目录
                    String sourceDir = info.applicationInfo.sourceDir;
                    //获得目录文件的MD5值
                    String md5file = MD5Utils.getMD5file(sourceDir);
                    //判断MD5值是否在病毒数据库中
                    String desc = AntivirusDao.checkFileVirus(md5file);
                    //desc==null说明没有病毒
                    if (desc == null) {
                        scaninfo.desc=false;
                    } else {
                        scaninfo.desc=true;
                    }

                    progress++;
                    SystemClock.sleep(100);
                    pb.setProgress(progress);

                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scaninfo;
                    handler.sendMessage(message);
                }

                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();

    }

    static class scanInfo {
        boolean desc;
        String appName;
        String appPackageName;
    }

    private TextView tv_init_virus;
    private ProgressBar pb;
    private ImageView iv_scanning;
    private LinearLayout ll_content;
    private ScrollView scrollView;

    private void initUI() {
        setContentView(R.layout.activity_antivirus);

        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);

        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);

        pb = (ProgressBar) findViewById(R.id.progressBar1);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        /**
         * 第一个参数表示开始的角度 第二个参数表示结束的角度 第三个参数表示参照自己 初始化旋转动画
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        // 设置动画的时间
        rotateAnimation.setDuration(5000);
        // 设置动画无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        // 开始动画
        iv_scanning.startAnimation(rotateAnimation);
    }

}
