package com.itheima52.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ============================================================
 * <p/>
 * 版     权 ： 黑马程序员教育集团版权所有(c) 2015
 * <p/>
 * 作     者  :  马伟奇
 * <p/>
 * 版     本 ： 1.0
 * <p/>
 * 创 建日期 ： 2015/2/27  11:12
 * <p/>
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {


    public List<T> lists;

    public Context mContext;

    protected MyBaseAdapter(List<T> lists, Context mContext) {
        this.lists = lists;
        this.mContext = mContext;
    }

    protected MyBaseAdapter() {
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
