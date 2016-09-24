package com.icloud.listenbook.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.icloud.listenbook.R;

public class InputDlg extends BaseDialog {
	Button submitBtn;
	Button cancelBtn;
	TextView titleTxt;
	EditText inputEdit;
	OnClickListener positiveList;
	OnClickListener negativeList;

	public InputDlg(Context context) {
		super(context);
	}

	public static InputDlg Builder(Context ctx) {
		return new InputDlg(ctx);
	}

	public InputDlg setTitle(String title) {
		titleTxt.setText(title);
		titleTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public InputDlg setHint(String msg) {
		inputEdit.setHint(msg);
		return this;
	}

	public InputDlg setInputType(int type) {
		inputEdit.setInputType(type);
		return this;
	}

	public InputDlg setHint(int msg) {
		return setHint(this.getContext().getString(msg));
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
		return R.layout.dlg_input;
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
		inputEdit = (EditText) findViewById(R.id.msg);

	}

	@Override
	protected void setData() {
		submitBtn.setVisibility(View.GONE);
		cancelBtn.setVisibility(View.GONE);
		titleTxt.setVisibility(View.GONE);
	}

	public String getInput() {
		if (inputEdit != null) {
			return inputEdit.getText().toString();
		}
		return null;
	}

	public InputDlg setPositiveButton(int pBtn, OnClickListener list) {
		submitBtn.setText(pBtn);
		this.positiveList = list;
		submitBtn.setVisibility(View.VISIBLE);
		return this;
	}

	public InputDlg setNegativeButton(int nbrt, OnClickListener list) {
		cancelBtn.setText(nbrt);
		this.negativeList = list;
		cancelBtn.setVisibility(View.VISIBLE);
		return this;
	}

}
