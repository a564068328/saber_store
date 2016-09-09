package com.itheima52.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.service.KillProcessService;
import com.itheima52.mobilesafe.utils.ServiceStatusUtils;

/**
 * 进程管理的设置界面
 */
public class TaskManagerSettingActivity extends Activity {

    private SharedPreferences preferences;
    private CheckBox cb_status_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager_setting);
        CheckBox cb_status = (CheckBox) findViewById(R.id.cb_status);
        preferences = getSharedPreferences("config", MODE_PRIVATE);
        boolean sysprocess = preferences.getBoolean("is_show_sysprocess", false);
        if (sysprocess) {
            cb_status.setChecked(true);
        } else {
            cb_status.setChecked(false);
        }
        cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    preferences.edit().putBoolean("is_show_sysprocess", true).commit();
                } else {
                    preferences.edit().putBoolean("is_show_sysprocess", false).commit();
                }
            }
        });
        //定时清理进程
        cb_status_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);
        final Intent intent = new Intent(this, KillProcessService.class);
        cb_status_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });


    }
    //看服务有没有运行
    @Override
    protected void onStart() {
        super.onStart();
        boolean running = ServiceStatusUtils.isServiceRunning(this, "com.itheima52.mobilesafe.service.KillProcessService");
        if(running){
            cb_status_kill_process.setChecked(true);
        }else{
            cb_status_kill_process.setChecked(false);
        }
    }
}
