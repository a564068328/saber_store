package com.itheima52.mobilesafe.activity;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.bean.TaskInfo;
import com.itheima52.mobilesafe.engine.TaskInfoParse;
import com.itheima52.mobilesafe.utils.SystemInfoUtils;
import com.itheima52.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;


import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends Activity {

    //利用XUtils架包的注解来findViewById
    @ViewInject(R.id.lv_task_listview)
    ListView lv_task_listview;
    @ViewInject(R.id.tv_task_process_count)
    TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    TextView tv_task_memory;
    @ViewInject(R.id.ll_taskmanegerpb)
    LinearLayout ll_taskmanegerpb;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> usertaskInfos;
    private List<TaskInfo> systaskInfos;
    private TaskViewAdapter taskViewAdapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();

    }

    /**
     * 区别：
     * <p/>
     * ActivityManager 活动管理器(任务管理器)
     * <p/>
     * packageManager 包管理器
     */
    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        //利用XUtils架包来注入view和事件
        ViewUtils.inject(this);

        //显示加载圈圈
        ll_taskmanegerpb.setVisibility(View.VISIBLE);

        lv_task_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //得到当前点击的ListView的对象，这里得到的是TaskViewAdapter中getItem的返回值
                Object object = lv_task_listview.getItemAtPosition(i);
                TaskInfo info = (TaskInfo) object;


                if (object != null && object instanceof TaskInfo) {
                    TaskViewHolder Holder = (TaskViewHolder) view.getTag();
                    if (info.isChecked()) {
                        Holder.cb_task.setChecked(false);
                        info.setChecked(false);
                    } else {
                        Holder.cb_task.setChecked(true);
                        info.setChecked(true);
                    }

                } else {
                    return;
                }
            }
        });
    }

    private class TaskViewHolder {
        private TextView taskname;
        private ImageView icon;
        private TextView taskramsize;
        private CheckBox cb_task;
    }

    //进入设置界面后根据设置的结果来更新LIstV
    @Override
    protected void onResume() {
        super.onResume();
        if (taskViewAdapter != null) {
            taskViewAdapter.notifyDataSetChanged();
        }
    }

    private class TaskViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //根据设置来决定要不要显示系统进程
            preferences = getSharedPreferences("config", MODE_PRIVATE);
            boolean showSysprocess = preferences.getBoolean("is_show_sysprocess", false);
            if (showSysprocess) {
                return systaskInfos.size() + 2 + usertaskInfos.size();
            } else {
                return usertaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int i) {
            if (i == 0) {
                return null;
            } else if (i == usertaskInfos.size() + 1) {
                return null;
            }

            TaskInfo info = null;
            if (i < usertaskInfos.size() + 1) {
                info = usertaskInfos.get(i - 1);
            } else {
                int location = i - (1 + usertaskInfos.size() + 1);
                info = systaskInfos.get(location);
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
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setText("用户程序(" + usertaskInfos.size() + ")");

                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLUE);
                return tv;
            }
            //i=userApplist.size()+1为系统程序的开始
            else if (i == usertaskInfos.size() + 1) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLUE);
                tv.setText("系统程序(" + systaskInfos.size() + ")");
                return tv;
            }
            TaskInfo taskInfo = null;
            if (0 < i && i < usertaskInfos.size() + 1) {
                taskInfo = usertaskInfos.get(i - 1);
            } else {
                int location = i - (usertaskInfos.size() + 2);
                taskInfo = systaskInfos.get(location);
            }
            TaskViewHolder taskViewHolder = null;
            if (view != null && view instanceof LinearLayout) {
                taskViewHolder = (TaskViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_view, null);
                taskViewHolder = new TaskViewHolder();
                taskViewHolder.icon = (ImageView) view.findViewById(R.id.iv_task);
                taskViewHolder.taskname = (TextView) view.findViewById(R.id.tv_taskname);
                taskViewHolder.taskramsize = (TextView) view.findViewById(R.id.tv_taskramsize);
                taskViewHolder.cb_task = (CheckBox) view.findViewById(R.id.cb_task);
                view.setTag(taskViewHolder);
            }
            taskViewHolder.icon.setImageDrawable(taskInfo.getIcon());
            taskViewHolder.taskname.setText(taskInfo.getAppName());
            taskViewHolder.taskramsize.setText("占用内存:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));
            if (taskInfo.getPackageName().equals(getPackageName())) {
                taskViewHolder.cb_task.setVisibility(View.INVISIBLE);
            } else {
                if (taskInfo.isChecked()) {
                    taskViewHolder.cb_task.setChecked(true);
                } else {
                    taskViewHolder.cb_task.setChecked(false);
                }
            }
            return view;
        }
    }

    private void initData() {
        new Thread() {
            public void run() {

                TaskInfoParse taskInfoParse = new TaskInfoParse();
                taskInfos = taskInfoParse.getTaskInfo(TaskManagerActivity.this);
                usertaskInfos = new ArrayList<TaskInfo>();
                systaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo info : taskInfos) {
                    if (info.isUserApp()) {
                        usertaskInfos.add(info);

                    } else {

                        systaskInfos.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        taskViewAdapter = new TaskViewAdapter();

                        ll_taskmanegerpb.setVisibility(View.INVISIBLE);

                        lv_task_listview.setAdapter(taskViewAdapter);

                        //获取到所有运行中的进程个数
                        SystemInfoUtils systemInfo = new SystemInfoUtils();
                        //tv_task_process_count.setText("进程：" + systemInfo.getRunningTaskCount(TaskManagerActivity.this) + "个");
                        tv_task_process_count.setText("进程：" + (usertaskInfos.size() + systaskInfos.size()) + "个");

                        //获取总RAM内存和可用Ram内存
                        String[] strings = new String[2];
                        systemInfo.getRamInfo(TaskManagerActivity.this, strings);
                        tv_task_memory.setText("剩余/总内存：" + strings[0] + "/" + strings[1]);
                    }
                });


            }
        }.start();
    }

    /**
     * 全选
     */
    public void selectall(View v) {
        for (TaskInfo info : usertaskInfos) {

            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.setChecked(true);

        }
        for (TaskInfo info : systaskInfos) {
            info.setChecked(true);

        }
        taskViewAdapter.notifyDataSetChanged();
    }

    /**
     * 反选
     */
    public void selectoppsite(View v) {
        for (TaskInfo info : usertaskInfos) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.setChecked(!info.isChecked());

        }
        for (TaskInfo info : systaskInfos) {
            info.setChecked(!info.isChecked());

        }
        taskViewAdapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     */
    public void killprocess(View v) {
        //想杀死进程，必须得到进程管理器
        //权限<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>
        ActivityManager Manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //杀死的进程个数
        int count = 0;
        //清理的内存大小
        int cleanmem = 0;
        //要杀死进程的集合
        List<TaskInfo> removelist = new ArrayList<TaskInfo>();
        for (TaskInfo info : usertaskInfos) {
            if (info.isChecked()) {
                if (info.getPackageName().equals(getPackageName())) {
                    continue;
                }
                //集合迭代的时候增删元素会发生并发修改异常
                //usertaskInfos.remove(info);
                removelist.add(info);
                count++;
                cleanmem += info.getMemorySize();
            }
        }
        for (TaskInfo info : systaskInfos) {
            if (info.isChecked()) {
                //Manager.killBackgroundProcesses(info.getPackageName());
                //systaskInfos.remove(info);
                removelist.add(info);
                count++;
                cleanmem += info.getMemorySize();
            }
        }
        /**
         * 注意： 当集合在迭代的时候。不能修改集合的大小
         */
        for (TaskInfo taskInfo : removelist) {
            Log.v("modliesafe", "" + usertaskInfos.size());
            // 判断是否是用户app
            if (taskInfo.isUserApp()) {


                // 杀死进程 参数表示包名
                Manager.killBackgroundProcesses(taskInfo
                        .getPackageName());
                usertaskInfos.remove(taskInfo);

            } else {

                // 杀死进程 参数表示包名
                Manager.killBackgroundProcesses(taskInfo
                        .getPackageName());

                systaskInfos.remove(taskInfo);
            }
        }
        Log.v("modliesafe", "" + usertaskInfos.size());


        new Thread() {
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //获取到所有运行中的进程个数
                        SystemInfoUtils systemInfo = new SystemInfoUtils();
                        //tv_task_process_count.setText("进程：" + systemInfo.getRunningTaskCount(TaskManagerActivity.this) + "个");
                        tv_task_process_count.setText("进程：" + (usertaskInfos.size() + systaskInfos.size()) + "个");

                        //获取总RAM内存和可用Ram内存
                        String[] strings = new String[2];
                        systemInfo.getRamInfo(TaskManagerActivity.this, strings);
                        tv_task_memory.setText("剩余/总内存：" + strings[0] + "/" + strings[1]);

                        taskViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

        UIUtils.showToast(
                TaskManagerActivity.this,
                "共清理"
                        + count
                        + "个进程,释放"
                        + Formatter.formatFileSize(TaskManagerActivity.this,
                        cleanmem) + "内存");

    }

    /**
     * 设置
     */
    public void setting(View v) {
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
