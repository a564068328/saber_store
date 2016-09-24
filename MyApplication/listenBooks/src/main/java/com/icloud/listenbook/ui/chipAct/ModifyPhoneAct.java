package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.unit.SMSUtil;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ModifyPhoneAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	EditText oldPhoneEdit, phoneEdit, smsCodeEdit;
	View submit;
	Button smsGet;
	ProgressDialog progressDialog;
	SMSUtil sMSUtil;
	String phone;
	View back;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "修改...");
	}

	@Override
	public void setDatas() {
		super.setDatas();
		sMSUtil = new SMSUtil(this, smsGet);
	}

	@Override
	public int getLayout() {
		return R.layout.act_up_phone;
	}

	@Override
	public void findViews() {
		oldPhoneEdit = (EditText) findViewById(R.id.oldPhone);
		phoneEdit = (EditText) findViewById(R.id.phone);
		submit = findViewById(R.id.submit);
		smsGet = (Button) findViewById(R.id.smsGet);
		smsCodeEdit = (EditText) findViewById(R.id.smsCode);
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		submit.setOnClickListener(this);
		submit.setOnTouchListener(ViewUtils.instance().onTouchListener);
		smsGet.setOnClickListener(this);
		smsGet.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void getSMS() {
		sMSUtil.sendSMS(oldPhoneEdit.getText().toString(), 3);
	}

	protected void up() {
		phone = phoneEdit.getText().toString();
		String oldPhone = oldPhoneEdit.getText().toString();
		String smsCode = smsCodeEdit.getText().toString();
		if (oldPhone.length() < 11) {
			ToastUtil.showMessage("旧手机格式填写不正确");
			return;
		}
		if (phone.length() < 11) {
			ToastUtil.showMessage("新手机格式填写不正确");
			return;
		}
		if (smsCode.length() < 4) {
			ToastUtil.showMessage("验证码长度填写不正确");
			return;
		}

		progressDialog.show();
		HttpUtils.modifyPhone(phone, oldPhone, smsCode, this, this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.smsGet:
			getSMS();
			break;
		case R.id.back:
			this.finish();
			break;
		case R.id.submit: {
			up();
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
			LogUtil.e(Tag, "modifyPhone:" + response.toString());
			int res = response.optInt("result", -1);
			if (res == 0) {
				ToastUtil.showMessage("修改成功");
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
