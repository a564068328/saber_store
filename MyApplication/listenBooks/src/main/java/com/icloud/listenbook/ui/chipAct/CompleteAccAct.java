package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.unit.SMSUtil;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class CompleteAccAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	View bind, back;
	ProgressDialog progressDialog;
	EditText phoneEdit, smsCodeEdit;
	Button smsGet;
	SMSUtil sMSUtil;
	String phone;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "完善...");

	}

	@Override
	public int getLayout() {
		return R.layout.act_complete_accout;
	}

	@Override
	public void setDatas() {
		super.setDatas();
		sMSUtil = new SMSUtil(this, smsGet);
	}

	@Override
	public void findViews() {
		bind = findViewById(R.id.bind);
		back = findViewById(R.id.back);
		phoneEdit = (EditText) findViewById(R.id.phone);
		smsGet = (Button) findViewById(R.id.smsGet);
		smsCodeEdit = (EditText) findViewById(R.id.smsCode);
	}

	@Override
	public void setListeners() {
		bind.setOnClickListener(this);
		bind.setOnTouchListener(ViewUtils.instance().onTouchListener);
		smsGet.setOnClickListener(this);
		smsGet.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void bind() {
		phone = phoneEdit.getText().toString();
		String smsCode = smsCodeEdit.getText().toString();
		if (phone.length() >= 11 && smsCode.length() >= 4) {
			progressDialog.show();
			HttpUtils.setPhone(phone, smsCode, this, this);
		} else {
			ToastUtil.showMessage("手机未填写或者验证码未填写");
		}
	}

	protected void getSMS() {
		sMSUtil.sendSMS(phoneEdit.getText().toString(), 1);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.smsGet:
			if (ToolUtils.isNetworkAvailableTip(this))
				getSMS();
			break;
		case R.id.back:
			this.finish();
			break;
		case R.id.bind: {
			if (ToolUtils.isNetworkAvailableTip(this))
				bind();
			break;
		}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		ToastUtil.showMessage(R.string.network_erro);

	}

	@Override
	public void onResponse(JSONObject response) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		try {
			LogUtil.e(Tag, "setPhone:" + response.toString());
			int res = response.optInt("result", -1);
			if (res == 0) {
				ToastUtil.showMessage("绑定成功");
				UserInfo.instance().setPhone(phone);
				this.finish();
			} else {
				String msg = response.optString("msg");
				if (!android.text.TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
			}

		} catch (Exception e) {
		}

	}

}
