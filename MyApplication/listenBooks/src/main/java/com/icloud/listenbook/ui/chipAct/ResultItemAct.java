package com.icloud.listenbook.ui.chipAct;

import java.util.List;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.ResultItemInfo;
import com.icloud.listenbook.recyclerplus.DividerItemDecoration;
import com.icloud.listenbook.ui.adapter.ResultItemAdapet;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;



public class ResultItemAct extends BaseActivity implements
OnClickListener{
	View back;
    RecyclerView recyclerview;
    //RecyclerView recyclerviewitem;
    ResultItemAdapet resultItemAdapet;
    List<ResultItemInfo> lists;
    LinearLayoutManager manager;
    String dateString;
    TextView title;
	@Override
	public void init() {
		// TODO Auto-generated method stub
		lists=GameApp.instance().ResultItemlist;
		dateString=getIntent().getStringExtra("date");
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_result_detile;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		title=(TextView) findViewById(R.id.title);
		back=findViewById(R.id.back);
		recyclerview=(RecyclerView) findViewById(R.id.recyclerview);
	}
	
	@Override
	public void setDatas() {
		// TODO Auto-generated method stub
		super.setDatas();
		title.setText(dateString+"详情");
		manager=new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		resultItemAdapet=new ResultItemAdapet(this,lists);
		recyclerview.setLayoutManager(manager);
		recyclerview.setAdapter(resultItemAdapet);
//		recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	
}
