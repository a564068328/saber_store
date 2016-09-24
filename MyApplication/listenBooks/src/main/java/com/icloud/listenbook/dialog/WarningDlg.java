package com.icloud.listenbook.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.icloud.listenbook.R;

public class WarningDlg extends BaseDialog {
	Button submitBtn;
	Button cancelBtn;
	TextView titleTxt;
	TextView msgTxt;
	CheckBox checkBox;
	OnClickListener positiveList;
	OnClickListener negativeList;

	public WarningDlg(Context context) {
		super(context);
	}

	public static WarningDlg Builder(Context ctx) {
		return new WarningDlg(ctx);
	}

	public WarningDlg setTitle(String title) {
		titleTxt.setText(title);
		titleTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public WarningDlg setMsg(String msg) {
		msgTxt.setText(msg);
		msgTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public WarningDlg setCheckBox(int str) {
		checkBox.setText(str);
		checkBox.setVisibility(View.VISIBLE);
		return this;
	}

	public WarningDlg setMsg(int msg) {
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
		return R.layout.dlg_warning;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void setListener() {
		addListener(submitBtn);
		addListener(cancelBtn);

	}

	public boolean isCheck() {
		return checkBox != null && checkBox.isChecked();
	}

	@Override
	protected void findView() {
		submitBtn = (Button) findViewById(R.id.submit);
		cancelBtn = (Button) findViewById(R.id.cancel);
		titleTxt = (TextView) findViewById(R.id.title);
		msgTxt = (TextView) findViewById(R.id.msg);
		checkBox = (CheckBox) findViewById(R.id.checkBox);

	}

	@Override
	protected void setData() {
		submitBtn.setVisibility(View.GONE);
		cancelBtn.setVisibility(View.GONE);
		titleTxt.setVisibility(View.GONE);
		msgTxt.setVisibility(View.GONE);
		checkBox.setVisibility(View.GONE);
	}

	public WarningDlg setPositiveButton(int pBtn, OnClickListener list) {
		submitBtn.setText(pBtn);
		this.positiveList = list;
		submitBtn.setVisibility(View.VISIBLE);
		return this;
	}

	public WarningDlg setNegativeButton(int nbrt, OnClickListener list) {
		cancelBtn.setText(nbrt);
		this.negativeList = list;
		cancelBtn.setVisibility(View.VISIBLE);
		return this;
	}

}
