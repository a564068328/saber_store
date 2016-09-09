package com.itheima52.mobilesafe.activity;

import android.net.TrafficStats;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima52.mobilesafe.R;
import com.itheima52.mobilesafe.bean.APPInfo;
import com.itheima52.mobilesafe.engine.APPInfos;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计
 */
public class TrafficManagerActivity extends Activity {
    private List<APPInfo> list;
    private List<APPInfo> Trafficlist;
    @ViewInject(R.id.ll_progressbar)
    LinearLayout ll_progressbar;
    @ViewInject(R.id.traffic_listview)
    ListView traffic_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_progressbar.setVisibility(View.INVISIBLE);
            traffic_listview.setAdapter(new myAdapter());
        }
    };
    private class myAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return Trafficlist.size();
        }

        @Override
        public Object getItem(int i) {
            return Trafficlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            trafficHolder holder=null;
            APPInfo info=Trafficlist.get(i);
            if(view ==null){
                view=View.inflate(TrafficManagerActivity.this,R.layout.item_traffic_view,null);
                holder=new trafficHolder();
                holder.iv_task= (ImageView) view.findViewById(R.id.iv_task);
                holder.tv_taskname= (TextView) view.findViewById(R.id.tv_taskname);
                holder.tv_taskup= (TextView) view.findViewById(R.id.tv_taskup);
                holder.tv_taskdown= (TextView) view.findViewById(R.id.tv_taskdown);
                holder.tv_total= (TextView) view.findViewById(R.id.tv_total);
                view.setTag(holder);
            }else{
                holder= (trafficHolder) view.getTag();
            }
            holder.iv_task.setImageDrawable(info.getIcon());
            holder.tv_taskname.setText(info.getApkName());
            holder.tv_taskup.setText(info.getUpTraffic());
            holder.tv_taskdown.setText(info.getDownTraffic());
            holder.tv_total.setText(info.getTatolTraffic());
            return view;
        }
    }

    private class trafficHolder{
        TextView tv_taskname;
        TextView tv_taskup;
        TextView tv_taskdown;
        TextView tv_total;
        ImageView iv_task;
    }

    private void initUI() {
        setContentView(R.layout.activity_traffic_manager);
        ViewUtils.inject(this);
        //获取到手机下载的流量
        //long rxBytes = TrafficStats.getMobileRxBytes();
        //TrafficStats.getUidRxBytes()
        ll_progressbar.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                list = APPInfos.getAppInfos(TrafficManagerActivity.this);
                Trafficlist=new ArrayList<APPInfo>();
                //去掉流量为0的
                for(APPInfo info:list){
                    if(!info.getUesTrafficIsZero()){
                        Trafficlist.add(info);
                    }
                }
                System.out.println(""+Trafficlist.size());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

}
