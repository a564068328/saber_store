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
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.unit.SMSUtil;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ForgetPwdAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	EditText accountEdit, passwordEdit, confirmPasswordEdit;
	EditText phoneEdit, smsCodeEdit;
	ProgressDialog progressDialog;
	View back;
	Button submit;
	SMSUtil sMSUtil;
	Button smsGet;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "找回...");
	}

	@Override
	public void setDatas() {
		super.setDatas();
		sMSUtil = new SMSUtil(this, smsGet);
	}

	@Override
	public int getLayout() {
		return R.layout.act_forge_pwd;
	}

	@Override
	public void findViews() {
		accountEdit = (EditText) findViewById(R.id.account);
		passwordEdit = (EditText) findViewById(R.id.password);
		phoneEdit = (EditText) findViewById(R.id.phone);
		smsCodeEdit = (EditText) findViewById(R.id.smsCode);
		confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password);
		submit = (Button) findViewById(R.id.submit);
		back = findViewById(R.id.back);
		smsGet = (Button) findViewById(R.id.smsGet);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		smsGet.setOnClickListener(this);
		smsGet.setOnTouchListener(ViewUtils.instance().onTouchListener);
		submit.setOnClickListener(this);
		submit.setOnTouchListener(ViewUtils.instance().onTouchListener);
		submit.setOnClickListener(this);
		submit.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void getSMS() {
		sMSUtil.sendSMS(phoneEdit.getText().toString(), 2);
	}

	protected void up() {
		String phone = phoneEdit.getText().toString();
		String smsCode = smsCodeEdit.getText().toString();

		if (phone.length() < 11) {
			ToastUtil.showMessage("手机格式填写不正确");
			return;
		}
		if (smsCode.length() < 4) {
			ToastUtil.showMessage("验证码长度填写不正确");
			return;
		}
		String account = accountEdit.getText().toString();
		String password = passwordEdit.getText().toString();
		String confirmPassword = confirmPasswordEdit.getText().toString();
		if (TextUtils.checkAcc(account)
				&& TextUtils.checkPwd(password, confirmPassword)) {
			if (!progressDialog.isShowing())
				progressDialog.show();
			HttpUtils.forgetPwd(account, password, phone, smsCode, this, this);
		}

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
			LogUtil.e(Tag, "ForgetPwd:" + response.toString());
			int res = response.optInt("result", -1);
			if (res == 0) {
				ToastUtil.showMessage("修改成功");
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
