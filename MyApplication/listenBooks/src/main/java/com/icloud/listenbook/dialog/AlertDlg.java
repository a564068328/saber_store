package com.icloud.listenbook.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icloud.listenbook.R;

public class AlertDlg extends BaseDialog {
	Button submitBtn;
	Button cancelBtn;
	TextView titleTxt;
	TextView msgTxt;
	OnClickListener positiveList;
	OnClickListener negativeList;

	public AlertDlg(Context context) {
		super(context);
	}

	public static AlertDlg Builder(Context ctx) {
		return new AlertDlg(ctx);
	}

	public AlertDlg setTitle(String title) {
		titleTxt.setText(title);
		titleTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public AlertDlg setMsg(String msg) {
		msgTxt.setText(msg);
		msgTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public AlertDlg setMsg(int msg) {
		return setMsg(this.getContext().getString(msg));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.submit:
			if (positiveList != null)
				positiveList.onClick(this, R.id.submit);
			break;
		case R.id.cancel:
			if (negativeList != null)
				negativeList.onClick(this, R.id.cancel);
			this.dismiss();
			break;
		}

	}

	@Override
	protected int getLayout() {
		return R.layout.dlg_alert;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void setListener() {
		addListener(submitBtn);
		addListener(cancelBtn);

	}

	@Override
	protected void findView() {
		submitBtn = (Button) findViewById(R.id.submit);
		cancelBtn = (Button) findViewById(R.id.cancel);
		titleTxt = (TextView) findViewById(R.id.title);
		msgTxt = (TextView) findViewById(R.id.msg);

	}

	@Override
	protected void setData() {
		submitBtn.setVisibility(View.GONE);
		cancelBtn.setVisibility(View.GONE);
		titleTxt.setVisibility(View.GONE);
		msgTxt.setVisibility(View.GONE);
	}

	public AlertDlg setPositiveButton(int pBtn, OnClickListener list) {
		submitBtn.setText(pBtn);
		this.positiveList = list;
		submitBtn.setVisibility(View.VISIBLE);
		return this;
	}

	public AlertDlg setNegativeButton(int nbrt, OnClickListener list) {
		cancelBtn.setText(nbrt);
		this.negativeList = list;
		cancelBtn.setVisibility(View.VISIBLE);
		return this;
	}

}
