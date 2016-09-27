package com.icloud.listenbook.ui.chipAct;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;

public class TeachNotifyAct extends BaseActivity {

	View tv_exit;
	TextView tv_body;
	CheckBox checkBox;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_teachabnotify;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		tv_exit = findViewById(R.id.tv_exit);
		tv_body = (TextView) findViewById(R.id.tv_body);
		checkBox = (CheckBox) findViewById(R.id.checkBox);
		String msg = SharedPreferenceUtil.getTEACH_NOTIFY();
		if (TextUtils.isEmpty(msg)) {
			tv_body.setText("暂无公告");
			tv_body.setGravity(Gravity.CENTER);
		} else {
			tv_body.setText(msg);
		}
		if (SharedPreferenceUtil.getTeachNotify()) {
			checkBox.setChecked(true);
		}else{
			checkBox.setChecked(false);
		}
	}

	@Override
	public void setListeners() {
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					SharedPreferenceUtil.saveTeachNotify(true);
				} else {
					SharedPreferenceUtil.saveTeachNotify(false);
				}
			}
		});
		tv_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public boolean isCheck() {
		return checkBox != null && checkBox.isChecked();
	}
}
