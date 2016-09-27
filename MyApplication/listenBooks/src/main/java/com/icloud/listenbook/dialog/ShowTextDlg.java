package com.icloud.listenbook.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.icloud.listenbook.R;

public class ShowTextDlg extends BaseDialog {
	Button submitBtn;
	Button cancelBtn;
	TextView titleTxt;
	TextView ShowText;
	OnClickListener positiveList;
	OnClickListener negativeList;

	public ShowTextDlg(Context context) {
		super(context);
	}

	public static ShowTextDlg Builder(Context ctx) {
		return new ShowTextDlg(ctx);
	}

	public ShowTextDlg setTitle(String title) {
		titleTxt.setText(title);
		titleTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public ShowTextDlg setHint(String msg) {
		ShowText.setHint(msg);
		return this;
	}

//	public ShowTextDlg setInputType(int type) {
//		ShowText.setInputType(type);
//		return this;
//	}

	public ShowTextDlg setHint(int msg) {
		return setHint(this.getContext().getString(msg));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.submit:
			if (positiveList != null)
//				positiveList.onClick(this, R.id.submit);
				this.dismiss();
			break;
//		case R.id.cancel:
//			if (negativeList != null)
//				negativeList.onClick(this, R.id.cancel);
//			this.dismiss();
//			break;
		}

	}

	@Override
	protected int getLayout() {
		return R.layout.dlg_showtext;
	}

//	public void mutilShow() {
//		if (inputEdit != null) {
//			inputEdit.setSingleLine(false);
//			inputEdit.setHorizontallyScrolling(false);
//		}
//	}

	@Override
	protected void init() {

	}

	@Override
	protected void setListener() {
		addListener(submitBtn);
//		addListener(cancelBtn);

	}

	@Override
	protected void findView() {
		submitBtn = (Button) findViewById(R.id.submit);
//		cancelBtn = (Button) findViewById(R.id.cancel);
		titleTxt = (TextView) findViewById(R.id.title);
		ShowText = (TextView) findViewById(R.id.msg);

	}

	public void setBody(String str) {
		if (str!=null) {
			ShowText.setText(str);
		}
	}

	@Override
	protected void setData() {
		submitBtn.setVisibility(View.GONE);
		//cancelBtn.setVisibility(View.GONE);
		titleTxt.setVisibility(View.GONE);
	}

	public String getInput() {
		if (ShowText != null) {
			return ShowText.getText().toString();
		}
		return null;
	}

	public ShowTextDlg setPositiveButton(int pBtn, OnClickListener list) {
//		submitBtn.setText(pBtn);
		this.positiveList = list;
		submitBtn.setVisibility(View.VISIBLE);
		return this;
	}

//	public ShowTextDlg setNegativeButton(int nbrt, OnClickListener list) {
//		cancelBtn.setText(nbrt);
//		this.negativeList = list;
//		cancelBtn.setVisibility(View.VISIBLE);
//		return this;
//	}

}
