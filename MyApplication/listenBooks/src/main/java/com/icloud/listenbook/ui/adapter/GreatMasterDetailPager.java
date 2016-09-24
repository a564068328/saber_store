package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import com.icloud.listenbook.R;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


public class GreatMasterDetailPager  {

	Activity act;
	StaggeredGridLayoutManager staggeredGridLM;
	PinterestLikeAdapter adapter;
	ArrayList<PinterestLikeItem> data;
	RecyclerView list;
	public View mRootView;// 根布局对象
	public GreatMasterDetailPager(Activity activity,ArrayList<PinterestLikeItem> data ) {
		act=activity;
		this.data=data;
		init();
		mRootView=findViews();
		setListeners();
		setData();
	}

	

	private void setData() {

	}

	private void setListeners() {
		list.setLayoutManager(staggeredGridLM);
		list.setAdapter(adapter);
	}

	private View findViews() {
        View view=View.inflate(act, R.layout.great_master_recycleview, null);
        list=(RecyclerView) view.findViewById(R.id.list);
        return view;
	}

	private void init() {
		staggeredGridLM = new StaggeredGridLayoutManager(4,
				StaggeredGridLayoutManager.VERTICAL);
		adapter = new PinterestLikeAdapter(act);
	}
	public void upData(ArrayList<PinterestLikeItem> data){
		adapter.upData(data);
	}
}
