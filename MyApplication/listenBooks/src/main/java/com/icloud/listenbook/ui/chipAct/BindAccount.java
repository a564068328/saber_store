package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class BindAccount extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	EditText accountEdit, passwordEdit, confirmPasswordEdit;
	View bind, back;
	ProgressDialog progressDialog;
	String account, password;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "绑定...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_bind_accout;
	}

	@Override
	public void findViews() {
		bind = findViewById(R.id.bind);
		back = findViewById(R.id.back);
		accountEdit = (EditText) findViewById(R.id.account);
		passwordEdit = (EditText) findViewById(R.id.password);
		confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password);
	}

	@Override
	public void setListeners() {
		bind.setOnClickListener(this);
		bind.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void bind() {
		account = accountEdit.getText().toString();
		password = passwordEdit.getText().toString();
		String confirmPassword = confirmPasswordEdit.getText().toString();
		if (TextUtils.checkAcc(account)
				&& TextUtils.checkPwd(password, confirmPassword)) {
			if (!progressDialog.isShowing())
				progressDialog.show();
			Login.updateUserType(account, password, this, this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
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
	}

	@Override
	public void onResponse(JSONObject response) {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "BindAccount:" + response.toString());
			if (res == 0) {
				/*** 设置账号 保存密码 */
				UserInfo.instance().setType(Login.TYPE_YZX);
				UserInfo.instance().setAccount(account);
				IoUtils.instance().saveUser(
						UserInfo.instance().getUserMod(password));
				ToastUtil.showMessage("绑定成功");
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
