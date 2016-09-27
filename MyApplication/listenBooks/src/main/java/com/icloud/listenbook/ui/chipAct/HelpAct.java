package com.icloud.listenbook.ui.chipAct;

import android.view.View;
import android.view.View.OnClickListener;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class HelpAct extends BaseActivity implements OnClickListener {
	View back;

	@Override
	public void init() {

	}

	@Override
	public int getLayout() {
		return R.layout.act_help;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		}

	}

}
