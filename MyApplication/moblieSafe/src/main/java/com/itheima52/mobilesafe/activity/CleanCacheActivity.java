package com.itheima52.mobilesafe.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.bean.APPInfo;
import com.itheima52.mobilesafe.utils.UIUtils;


/**
 * 描 述 ：
 * <p/>
 * 缓存清理
 * 修订历史 ：
 * <uses-permission android:name="android.permission.GET_PACKAGE_SIZE" />
 * <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
 * ============================================================
 **/
public class CleanCacheActivity extends Activity {

    private PackageManager packageManager;
    private List<cacheInfo> cacheInfos;
    private ListView list_view;
    private LinearLayout ll_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);


        list_view = (ListView) findViewById(R.id.list_view);
        ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar);
        //需要清理垃圾的集合
        cacheInfos = new ArrayList<cacheInfo>();
        packageManager = getPackageManager();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
    }

    private void initUI() {

        /**
         * 接收2个参数
         * 第一个参数接收一个包名
         * 第二个参数接收aidl的对象
         */
//		  * @hide
//		     */
//		    public abstract void getPackageSizeInfo(String packageName,
//		            IPackageStatsObserver observer);
//		packageManager.getPackageSizeInfo();
        ll_progressbar.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                //安装到手机上面所有的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                for (PackageInfo packageInfo : installedPackages) {
                    getCacheSize(packageInfo);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_progressbar.setVisibility(View.INVISIBLE);
                        list_view.setAdapter(new cacheAdapter());
                        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                cacheInfo clickAppInfo = cacheInfos.get(i);
                                Intent detail_intent = new Intent();
                                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                                detail_intent.setData(Uri.parse("package:" + clickAppInfo.packageName));
                                startActivity(detail_intent);
                            }
                        });
                    }
                });


            }
        }.start();


    }


    //获取到缓存的大小
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //Class<?> clazz = getClassLoader().loadClass("packageManager");
            //通过反射获取到当前的方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            /**
             * 第一个参数表示反射的方法是由谁调用的
             * 第二个参数为可变参数，表示所反射的方法的参数
             */
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new myIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private class myIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo packageInfo;

        public myIPackageStatsObserver(PackageInfo packageInfo) {
            super();
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存的大小
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0) {

                final cacheInfo cacheinfo = new cacheInfo();
                cacheinfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheinfo.cacheSize = cacheSize;
                cacheinfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheinfo.packageName = packageInfo.packageName;
                cacheInfos.add(cacheinfo);

            }
        }
    }

    /**
     * 一键清理
     *
     * @param v
     */
    public void cleanAll(View v) {

        //获取到当前应用程序的所有方法
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            //判断当前方法名
            if (method.getName().equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new myIPackageDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                UIUtils.showToast(CleanCacheActivity.this, "一键清理完毕！");
            }
        }
    }

    static class cacheInfo {
        Drawable icon;
        long cacheSize;
        String appName;
        String packageName;
    }

    private class cacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return cacheInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            cacheInfo cacheinfo = null;
            viewHolder holder = null;
            if (view == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache, null);
                holder = new viewHolder();
                cacheinfo = cacheInfos.get(i);
                holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.appName = (TextView) view.findViewById(R.id.tv_name);
                holder.cacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
                cacheinfo = cacheInfos.get(i);
            }

            holder.appName.setText(cacheinfo.appName);
            holder.icon.setImageDrawable(cacheinfo.icon);
            holder.cacheSize.setText("缓存大小：" + Formatter.formatFileSize(CleanCacheActivity.this, cacheinfo.cacheSize));
            return view;
        }
    }

    static class viewHolder {
        ImageView icon;
        TextView cacheSize;
        TextView appName;
    }

    private class myIPackageDataObserver extends IPackageDataObserver.Stub {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
