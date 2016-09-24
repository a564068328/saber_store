package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ShareAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	EditText edit;
	View exchange, back, copy, reward, send, rewardRL;
	TextView txt;
	ProgressDialog progressDialog;

	@Override
	public void init() {
		if (!UserInfo.instance().isLogin()) {
			LoadingTool.launchActivity(this, LoginAct.class);
		}
		progressDialog = DialogManage.ProgressDialog(this, "提示", "兑换ing...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_shart;
	}

	@Override
	public void findViews() {
		rewardRL = findViewById(R.id.rewardRL);
		txt = (TextView) findViewById(R.id.txt);
		back = findViewById(R.id.back);
		exchange = findViewById(R.id.exchange);
		copy = findViewById(R.id.copy);
		reward = findViewById(R.id.reward);
		send = findViewById(R.id.send);
		edit = (EditText) findViewById(R.id.edit);
	}

	TextWatcher tw = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			exchange.setEnabled(edit.getText().length() > 0 ? true : false);

		}
	};

	@Override
	public void setListeners() {
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		exchange.setOnTouchListener(ViewUtils.instance().onTouchListener);
		exchange.setOnClickListener(this);
		copy.setOnTouchListener(ViewUtils.instance().onTouchListener);
		copy.setOnClickListener(this);
		reward.setOnTouchListener(ViewUtils.instance().onTouchListener);
		reward.setOnClickListener(this);
		send.setOnTouchListener(ViewUtils.instance().onTouchListener);
		send.setOnClickListener(this);
		edit.addTextChangedListener(tw);
	}

	private String getJUrl() {

		return "http://" + ServerIps.dLoginAddr + "/user/friendshare?uid="
				+ UserInfo.instance().getUid();
	}

	private void copy() {
		String url = getJUrl();
		String desc = this.getString(R.string.app_name)
				+ this.getString(R.string.pop_share_desc);
		ClipboardManager cmb = (ClipboardManager) this
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(desc + "\n" + url);

		ToastUtil.showMessage(R.string.pop_share_past_tip);
	}

	private void shares() {
		String url = getJUrl();
		String desc = this.getString(R.string.app_name)
				+ this.getString(R.string.pop_share_desc);

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				this.getString(R.string.pop_share_title_tip));
		intent.putExtra(Intent.EXTRA_TEXT, desc + "\n" + url);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(Intent.createChooser(intent,
				this.getString(R.string.app_name)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		setDatas();
	}

	@Override
	public void setDatas() {
		super.setDatas();
		String url = getJUrl();
		String tipTxt = this.getString(R.string.pop_share_tip,
				this.getString(R.string.app_name));
		tipTxt += ("\n" + url);
		txt.setText(tipTxt);
	}

	protected void exchange() {
		String code = edit.getText().toString();
		if (!TextUtils.isEmpty(code)) {
			progressDialog.show();
			HttpUtils.Thanks(code, this, this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.reward:
			boolean bool = rewardRL.getVisibility() == View.VISIBLE;
			rewardRL.setVisibility(bool ? View.GONE : View.VISIBLE);
			break;
		case R.id.copy:
			copy();
			break;
		case R.id.send:
			shares();
			break;
		case R.id.exchange:
			exchange();
			break;

		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progressDialog.dismiss();
		ToastUtil.showMessage(R.string.network_erro);
	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			progressDialog.dismiss();
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "ShareAct:" + response.toString());

			if (res == 0) {
				UserInfo.instance().setCurrency(
						response.optDouble("currency", UserInfo.instance()
								.getCurrency()));
				ToastUtil.showMessage("答谢成功!");
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
