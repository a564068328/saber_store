package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class RegisterAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	View register, back;
	EditText accountEdit, passwordEdit, confirmPasswordEdit;
	ProgressDialog progressDialog;
	String account;
	String password;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "注册...");

	}

	@Override
	public int getLayout() {
		return R.layout.act_register;
	}

	@Override
	public void findViews() {
		accountEdit = (EditText) findViewById(R.id.account);
		passwordEdit = (EditText) findViewById(R.id.password);
		confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password);
		register = findViewById(R.id.register);
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		register.setOnClickListener(this);
		register.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void regist() {
		account = accountEdit.getText().toString();
		password = passwordEdit.getText().toString();
		String confirmPassword = confirmPasswordEdit.getText().toString();
		if (TextUtils.checkAcc(account)
				&& TextUtils.checkPwd(password, confirmPassword)) {
			if (!progressDialog.isShowing())
				progressDialog.show();
			Login.regist(account, password, this, this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.register:
			if (ToolUtils.isNetworkAvailableTip(this))
				regist();
			break;
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
			LogUtil.e(Tag, "Register:" + response.toString());
			int res = response.optInt("result", -1);
			/** 不正确就删除用户 **/
			if (res == 0) {
				ToastUtil.showMessage("注册成功");
				Intent intent = new Intent();
				intent.putExtra("account", account);
				intent.putExtra("password", password);
				this.setResult(RESULT_OK, intent);
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
