package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.icloud.listenbook.R;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.recyclerplus.DividerItemDecoration;
import com.icloud.listenbook.ui.HomeSampleDivider;
import com.icloud.listenbook.ui.adapter.ResultAdapter;
import com.icloud.listenbook.ui.adapter.ResultItemAdapet;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;

public class ResultAct extends BaseActivity implements
OnClickListener, Listener<JSONObject>, ErrorListener{
    private boolean isAdult;
    View back;
    RelativeLayout ll_progress;
    RecyclerView recyclerview;
    //RecyclerView recyclerviewitem;
    ResultAdapter resultAdapter;
    private List<MeritTableAdult>AdultList;
    private List<MeritTableChildren>ChildrenList;
    LinearLayoutManager manager;
    //ResultItemAdapet resultItemAdapet;
	@Override
	public void init() {
		// TODO Auto-generated method stub	
		isAdult=getIntent().getBooleanExtra("isadult", false);
		if(isAdult){
//			if(GameApp.instance().MeritTableChildrenlists!=null)
//				GameApp.instance().MeritTableChildrenlists.clear();
			if(AdultList!=null)
				AdultList.clear();
			AdultList=GameApp.instance().MeritTableAdultlists;
			//GameApp.instance().MeritTableAdultlists.clear();
		}else{
//			if(GameApp.instance().MeritTableAdultlists!=null)
//				GameApp.instance().MeritTableAdultlists.clear();
			if(ChildrenList!=null)
				ChildrenList.clear();
			ChildrenList=GameApp.instance().MeritTableChildrenlists;
		}
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_result;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		back=findViewById(R.id.back);
		ll_progress=(RelativeLayout) findViewById(R.id.ll_progress);
		recyclerview=(RecyclerView) findViewById(R.id.recyclerview);
		

	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
	}
    @Override
    public void setDatas() {
    	// TODO Auto-generated method stub
    	super.setDatas();
    	if(isAdult==true){
    		ll_progress.setVisibility(View.GONE);
    		recyclerview.setVisibility(View.VISIBLE);
    		if(resultAdapter!=null)
    			resultAdapter=null;
    		resultAdapter=new ResultAdapter(this,AdultList,null,isAdult);
    		manager=new LinearLayoutManager(this);
    		recyclerview.setLayoutManager(manager); 
    		recyclerview.setAdapter(resultAdapter);
    		//setHasFixedSize()方法用来使RecyclerView保持固定的大小，该信息被用于自身的优化。
    		recyclerview.setHasFixedSize(true);
    		//添加分割线
    		//recyclerview.addItemDecoration(new HomeSampleDivider(recyclerview));
//    		recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    	}else{
    		if(resultAdapter!=null)
    			resultAdapter=null;
    		ll_progress.setVisibility(View.GONE);
    		recyclerview.setVisibility(View.VISIBLE);
    		resultAdapter=new ResultAdapter(this,null,ChildrenList,isAdult);
    		manager=new LinearLayoutManager(this);
    		recyclerview.setLayoutManager(manager); 
    		recyclerview.setAdapter(resultAdapter);
    		recyclerview.setHasFixedSize(true);
//    		recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    	}
    }
	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponse(JSONObject response) {
		// TODO Auto-generated method stub
		
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
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
//    	if(AdultList!=null)
//    		AdultList.clear();
//    	if(ChildrenList!=null)
//    		ChildrenList.clear();
    }
}
