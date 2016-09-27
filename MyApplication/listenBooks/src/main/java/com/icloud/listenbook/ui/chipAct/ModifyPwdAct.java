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
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ModifyPwdAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	View back, submit;
	EditText oldPwdEdit, pwdEdit, npwdEdit;
	ProgressDialog progressDialog;
	String pwd;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "修改密码...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_modifypwd;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		submit = findViewById(R.id.submit);
		oldPwdEdit = (EditText) findViewById(R.id.oldPwd);
		pwdEdit = (EditText) findViewById(R.id.pwd);
		npwdEdit = (EditText) findViewById(R.id.npwd);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		submit.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	protected void modifyPwd() {
		pwd = pwdEdit.getText().toString();
		String oldPwd = oldPwdEdit.getText().toString();
		String npwd = npwdEdit.getText().toString();
		if (TextUtils.checkPwd(npwd, pwd) && TextUtils.checkPwd(oldPwd)
				&& TextUtils.checkPwd(pwd)) {
			progressDialog.show();
			Login.modifyPwd(pwd, oldPwd, this, this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.submit:
			modifyPwd();
			break;
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		ToastUtil.showMessage(R.string.network_erro);
		progressDialog.dismiss();
	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			progressDialog.dismiss();
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "modifyPwd:" + response.toString());

			if (res == 0) {
				if (IoUtils.instance()
						.getUser(UserInfo.instance().getAccount()) != null) {
					IoUtils.instance().saveUser(
							UserInfo.instance().getUserMod(pwd));
				}
				ToastUtil.showMessage("修改成功！");
				ModifyPwdAct.this.finish();
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
