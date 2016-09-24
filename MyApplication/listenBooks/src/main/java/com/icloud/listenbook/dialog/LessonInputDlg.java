package com.icloud.listenbook.dialog;

import android.R.integer;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.icloud.listenbook.R;

public class LessonInputDlg extends BaseDialog {
	Button submitBtn;
	Button cancelBtn;
	TextView titleTxt;
	TextView inputEdit;
	OnClickListener positiveList;
	OnClickListener negativeList;
    LinearLayout ll_lesson;
    static String[] opition;
	public LessonInputDlg(Context context) {
		super(context);
	}

	public static LessonInputDlg Builder(Context ctx ,String[] opitionbuf) {
		opition=opitionbuf;
		return new LessonInputDlg(ctx);
	}

	public LessonInputDlg setTitle(String title) {
		titleTxt.setText(title);
		titleTxt.setVisibility(View.VISIBLE);
		return this;
	}

	public LessonInputDlg setHint(String msg) {
		inputEdit.setHint(msg);
		return this;
	}

	public LessonInputDlg setInputType(int type) {
		inputEdit.setInputType(type);
		return this;
	}

	public LessonInputDlg setHint(int msg) {
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
		return R.layout.dlg_lesson;
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
        ll_lesson=(LinearLayout) findViewById(R.id.ll_option);
        for(int i=0;i<opition.length;i++){
//        	EditText et=new EditText(context);
//		    LayoutParams params = new LinearLayout.LayoutParams(
//					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//		    et.setBackgroundDrawable(context.getResources().getDrawable(
//					R.drawable.bg_edittext));
//		    box.setText(item.option[i]);
//		    box.setTextColor(act.getResources().getColor(
//					R.color.black));
//		    box.setId(i);
//		    box.setTag(position);
//		    ll_group.addView(box, params);
//		    ll_group.setOrientation(LinearLayout.VERTICAL);
        }
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

	public LessonInputDlg setPositiveButton(int pBtn, OnClickListener list) {
		submitBtn.setText(pBtn);
		this.positiveList = list;
		submitBtn.setVisibility(View.VISIBLE);
		return this;
	}

	public LessonInputDlg setNegativeButton(int nbrt, OnClickListener list) {
		cancelBtn.setText(nbrt);
		this.negativeList = list;
		cancelBtn.setVisibility(View.VISIBLE);
		return this;
	}

}
