package com.icloud.listenbook.ui.chipAct;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.ClientDeviceInfo;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class SettingAct extends BaseActivity implements OnClickListener {
	View back;
	View userInfoL, helpL, aboutL, upAcc, logout, upVersions;
	TextView accInfoTxt;
	ProgressDialog progressDialog;
	TextView versions;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "获取更新...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_setting;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		accInfoTxt = (TextView) findViewById(R.id.accInfoTxt);
		userInfoL = findViewById(R.id.userInfo_l);
		helpL = findViewById(R.id.help_l);
		aboutL = findViewById(R.id.about_l);
		upAcc = findViewById(R.id.up_acc);
		logout = findViewById(R.id.logout);
		upVersions = findViewById(R.id.up_versions_l);
		versions = (TextView) findViewById(R.id.versions);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		userInfoL.setOnClickListener(this);
		userInfoL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		helpL.setOnClickListener(this);
		helpL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		aboutL.setOnClickListener(this);
		aboutL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		upAcc.setOnClickListener(this);
		upAcc.setOnTouchListener(ViewUtils.instance().onTouchListener);
		logout.setOnClickListener(this);
		logout.setOnTouchListener(ViewUtils.instance().onTouchListener);
		upVersions.setOnClickListener(this);
		upVersions.setOnTouchListener(ViewUtils.instance().onTouchListener);

	}

	public void upInfo() {
		String version = "1.0.0";
		try {
			JSONObject json = new JSONObject(
					ClientDeviceInfo.getInstance().version);
			version = json.optString("name", "1.0.0");
		} catch (Exception e) {
		}
		versions.setText(String.format(this.getString(R.string.versionsTip),
				version));
		if (!UserInfo.instance().isLogin()) {
			userInfoL.setVisibility(View.GONE);
			upAcc.setVisibility(View.GONE);
			logout.setVisibility(View.GONE);
		} else {
			if (UserInfo.instance().isFastLogin())
				accInfoTxt.setText(R.string.bind_acc);
			else {
				if (android.text.TextUtils.isEmpty(UserInfo.instance()
						.getPhone())) {
					accInfoTxt.setText(R.string.acc_complete);
				} else {
					accInfoTxt.setText(R.string.acc_up_bind);
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		upInfo();
	}

	@Override
	public void setDatas() {
		super.setDatas();
		upInfo();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.userInfo_l:
			Class<? extends Activity> activity = BindAccount.class;
			if (UserInfo.instance().isFastLogin())
				activity = BindAccount.class;
			else {
				if (android.text.TextUtils.isEmpty(UserInfo.instance()
						.getPhone())) {
					activity = CompleteAccAct.class;
				} else {
					activity = ModifyPhoneAct.class;
				}
			}
			LoadingTool.launchActivity(this, activity);
			break;
		case R.id.logout:
			IoUtils.instance().delUser(UserInfo.instance().getAccount());
			UserInfo.instance().reset();
			ToastUtil.showMessage("注销成功!");
			upInfo();
			break;
		case R.id.up_acc:
			IoUtils.instance().delUser(UserInfo.instance().getAccount());
			UserInfo.instance().reset();
			LoadingTool.launchActivity(this, LoginAct.class);
			break;
		case R.id.up_versions_l:
			if (ToolUtils.isNetworkAvailableTip(this))
				checkVersion();
			break;
		case R.id.about_l:
			LoadingTool.launchActivity(this, AboutAct.class);
			break;
		case R.id.help_l:
			LoadingTool.launchActivity(this, HelpAct.class);
			break;
		}

	}

	protected void checkVersion() {
		progressDialog.show();
		HttpUtils.checkVersion(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					progressDialog.dismiss();
					int res = response.optInt("result", -1);
					if (res == 0) {
						String url = response.optString("url", "");
						String msg = response.optString("msg", "");
						String version = response.optString("version", "");
						boolean isUp = response.optInt("strong", 0) == 0;
						if (!android.text.TextUtils.isEmpty(url)) {
							DialogManage.showUpVersion(url, msg, version, isUp);
						}
					} else {
						String msg = response.optString("msg");
						if (!android.text.TextUtils.isEmpty(msg)) {
							ToastUtil.showMessage(msg);
						}
					}
				} catch (Exception e) {
					e.toString();
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
