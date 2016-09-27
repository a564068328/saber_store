package com.icloud.listenbook.ui.chipAct;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.io.DBHelper;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.pro.HallSocketManage;
import com.icloud.listenbook.unit.AccUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Baseuser;
/**
 * 登陆界面
 * @author asus
 *
 */
public class LoginAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	static final int TAG_REGINST = 1000 >> 2;
	View registerTxt, loginBtn, touristLoginBtn;
	EditText accountEdit, passwordEdit;
	ProgressDialog progressDialog;
	View back;
	String account, password;
	CheckBox autoLogin;
	View forgetPwd;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "登陆...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_login;
	}

	@Override
	public void findViews() {
		registerTxt = findViewById(R.id.registerTxt);
		loginBtn = findViewById(R.id.login);
		back = findViewById(R.id.back);
		touristLoginBtn = findViewById(R.id.tourist_login);
		accountEdit = (EditText) findViewById(R.id.account);
		passwordEdit = (EditText) findViewById(R.id.password);
		autoLogin = (CheckBox) findViewById(R.id.autoLogin);
		forgetPwd = findViewById(R.id.forgetPwd);

	}

	@Override
	public void setListeners() {
		registerTxt.setOnClickListener(this);
		registerTxt.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		loginBtn.setOnClickListener(this);
		loginBtn.setOnTouchListener(ViewUtils.instance().onTouchListener);
		touristLoginBtn.setOnClickListener(this);
		touristLoginBtn
				.setOnTouchListener(ViewUtils.instance().onTouchListener);
		forgetPwd.setOnClickListener(this);
		forgetPwd.setOnTouchListener(ViewUtils.instance().onTouchListener);

	}

	/** 快速登陆 */
	public void fastLogin(final Listener<JSONObject> responseList,
			final ErrorListener erroList) {
		if (!progressDialog.isShowing())
			progressDialog.show();
		Baseuser baseuser = DBHelper.getInstance(GameApp.instance())
				.getLastUser(Login.TYPE_FAST);
		/**
		 * 是游客账号 直接登陆 不是获取之后再登陆
		 * */
		if (baseuser != null && baseuser.getType().equals(Login.TYPE_FAST)) {
			account = baseuser.getAccount();
			password = baseuser.getPwd();
			Login.login(account, password, baseuser.getType(), responseList,
					erroList);
		} else {
			Login.getDeviceId(new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject json) {
					progressDialog.dismiss();
					try {
						LogUtil.e(Tag, "getDeviceId" + json.toString());
						int res = json.optInt("result", -1);
						if (res == 0) {
							JSONArray cid = json.optJSONArray("deviceId");
							SharedPreferenceUtil.saveDeviceId(cid);
							account = json.optString("account", "");
							password = json.optString("pwd", "");
							Login.login(account, password, Login.TYPE_FAST,
									responseList, erroList);
						} else {
							String msg = json.optString("msg");
							if (!android.text.TextUtils.isEmpty(msg)) {
								ToastUtil.showMessage(msg);
							}
						}
					} catch (Exception e) {
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					progressDialog.dismiss();
					ToastUtil.showMessage(R.string.network_erro);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.tourist_login:
			if (ToolUtils.isNetworkAvailableTip(this))
				fastLogin(this, this);
			break;
		case R.id.login: {
			if (ToolUtils.isNetworkAvailableTip(this))
				login();
			break;
		}
		case R.id.registerTxt:
			LoadingTool.launchResultActivity(this, RegisterAct.class,
					TAG_REGINST);
			break;
		case R.id.forgetPwd:
			LoadingTool.launchResultActivity(this, ForgetPwdAct.class,
					TAG_REGINST);
			break;
		}
	}

	private void login() {
		account = accountEdit.getText().toString();
		password = passwordEdit.getText().toString();
		if (TextUtils.checkAcc(account) && TextUtils.checkPwd(password)) {
			if (!progressDialog.isShowing())
				progressDialog.show();
			Login.login(account, password, Login.TYPE_YZX, this, this);
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
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "login:" + response.toString());

			if (res == 0) {
				UserInfo.instance().parse(response);
				IoUtils.instance().delUserInType(Login.TYPE_REG);
				if (autoLogin.isChecked()) {
					IoUtils.instance().saveUser(
							UserInfo.instance().getUserMod(password));
				}
				Login.loginSucceed();
				ToastUtil.showMessage("登陆成功");
				loginSucceed();
			} else {
				String msg = response.optString("msg");
				if (!android.text.TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
			}
		} catch (Exception e) {
		}

	}

	protected void loginSucceed() {
		/** 新用户提示完善账号 */
		if (UserInfo.instance().isNew()) {
			DialogManage.showAlertDialog(this, null,
					R.string.register_complete_tip, R.string.cancel,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginAct.this.finish();
						}
					}, R.string.complete,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginAct.this.finish();
							LoadingTool.launchActivity(LoginAct.this,
									UpUserInfoAct.class);
						}
					}, false);
		} else
			LoginAct.this.finish();
	}

	protected void registerResult(Intent data) {
		String account = data.getStringExtra("account");
		String password = data.getStringExtra("password");
		if (!android.text.TextUtils.isEmpty(account))
			accountEdit.setText(account);
		if (!android.text.TextUtils.isEmpty(password))
			passwordEdit.setText(password);
	}
//得到注册界面的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAG_REGINST:
			if (resultCode == RESULT_OK) {
				registerResult(data);
			}
			break;
		}
	}
}
