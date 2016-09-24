package com.icloud.listenbook.ui.chipAct;

import android.view.View;
import android.view.View.OnClickListener;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;

public class TeachAbstractAct extends BaseActivity{

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_teachabstract;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		View tv_exit=findViewById(R.id.tv_exit);
		tv_exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		
	}

}
