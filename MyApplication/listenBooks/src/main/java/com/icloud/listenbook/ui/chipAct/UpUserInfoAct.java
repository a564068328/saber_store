package com.icloud.listenbook.ui.chipAct;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.CircleNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.Base64;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;

public class UpUserInfoAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	View back;
	View upPwd;
	EditText signature, area, phone, nick;
	TextView currency;
	TextView uid;
	Button up, cancel;
	boolean isCompile = false;
	ProgressDialog progressDialog;
	View upHead;
	public static boolean iconFlag;
	ImageLoader imageLoader;
	String urlHead;
	CircleNetworkImageView userIcon;

	@Override
	public void init() {
		progressDialog = DialogManage.ProgressDialog(this, "提示", "修改用户信息...");
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		userIcon.setImageUrl(urlHead + UserInfo.instance().getIcon(),
				imageLoader);
		userIcon.setDefaultImageResId(R.drawable.user_defa_icon);
		userIcon.setErrorImageResId(R.drawable.user_defa_icon);
		upUserInfo();
		if (isCompile) {
			toCompile();
		} else {
			toNoCompile();
		}
	}

	protected void upUserInfo() {
		signature.setText(UserInfo.instance().getSignature());
		area.setText(UserInfo.instance().getArea());
		phone.setText(UserInfo.instance().getPhone());
		nick.setText(UserInfo.instance().getNick());
		uid.setText(Html.fromHtml("<u>"
				+ String.valueOf(UserInfo.instance().getUid()) + "</u>"));
		currency.setText(Html.fromHtml("<u>"
				+ String.valueOf((int) UserInfo.instance().getCurrency())
				+ "</u>"));
	}

	protected void toNoCompile(EditText edit) {
		edit.setEnabled(false);
	}

	protected void toCompile(EditText edit) {
		edit.setEnabled(true);
	}

	protected void toCompile() {
		isCompile = true;
		up.setText(R.string.submit);
		cancel.setVisibility(View.VISIBLE);
		toCompile(signature);
		toCompile(area);
		toCompile(nick);
	}

	protected void toNoCompile() {
		isCompile = false;
		up.setText(R.string.compile);
		cancel.setVisibility(View.GONE);
		toNoCompile(signature);
		toNoCompile(area);
		toNoCompile(nick);
	}

	@Override
	public int getLayout() {
		return R.layout.act_up_userinfo;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		upPwd = findViewById(R.id.upPwd);
		signature = (EditText) findViewById(R.id.signature);
		area = (EditText) findViewById(R.id.area);
		nick = (EditText) findViewById(R.id.nick);
		phone = (EditText) findViewById(R.id.phone);
		up = (Button) findViewById(R.id.up);
		cancel = (Button) findViewById(R.id.cancel);
		upHead = findViewById(R.id.upHead);
		userIcon = (CircleNetworkImageView) findViewById(R.id.userIcon);
		currency = (TextView) findViewById(R.id.currency);
		uid = (TextView) findViewById(R.id.uid);

	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnClickListener(this);
		upHead.setOnClickListener(this);
		up.setOnClickListener(this);
		cancel.setOnClickListener(this);
		cancel.setOnTouchListener(ViewUtils.instance().onTouchListener);
		upPwd.setOnClickListener(this);
		upPwd.setOnTouchListener(ViewUtils.instance().onTouchListener);
		upHead.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		up.setOnTouchListener(ViewUtils.instance().onTouchListener);

	}

	protected boolean submit() {
		String nickStr = nick.getText().toString().trim();
		String areaStr = area.getText().toString().trim();
		String signatureStr = signature.getText().toString().trim();
		if (nickStr.trim().length() < 2) {
			ToastUtil.showMessage("昵称长度至少为2");
			return false;
		}
		if (areaStr.trim().length() < 2) {
			ToastUtil.showMessage("所在地长度至少为2");
			return false;
		}
		if (signatureStr.trim().length() < 2) {
			ToastUtil.showMessage("签名长度至少为2");
			return false;
		}

		if (nickStr.equals(UserInfo.instance().getNick())
				&& areaStr.equals(UserInfo.instance().getArea())
				&& signatureStr.equals(UserInfo.instance().getSignature())) {
			ToastUtil.showMessage("没有任何修改!");
			return false;
		}
		progressDialog.show();
		HttpUtils.modifyInfo(nickStr, signatureStr, areaStr, this, this);
		return true;
	}

	private void showPickDialog() {
		new AlertDialog.Builder(this)
				.setTitle(this.getString(R.string.toast_info_pic_title))
				.setMessage(this.getString(R.string.toast_info_pic_tip))
				.setNegativeButton(this.getString(R.string.toast_info_pic_pic),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								//相册
								Intent intent = new Intent(Intent.ACTION_PICK,
										null);
								intent.setDataAndType(
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										"image/*");
								startActivityForResult(intent, 1);

							}
						})
				.setPositiveButton(
						this.getString(R.string.toast_info_pic_photo),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
								//拍照
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												"icon.jpg")));
								startActivityForResult(intent, 2);
							}
						}).show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.upHead:
			if (ToolUtils.isNetworkAvailableTip(this))
				if (FileUtils.isSDCardMounted()) {
					showPickDialog();
				} else {
					ToastUtil.showMessage(R.string.toast_info_no_sdcard);
				}
			break;
		case R.id.cancel:
			toNoCompile();
			upUserInfo();
			break;
		case R.id.up:
			if (ToolUtils.isNetworkAvailableTip(this)) {
				if (!isCompile) {
					toCompile();
				} else {
					if (submit()) {
						toNoCompile();
					}

				}
			}
			break;
		case R.id.back:
			this.finish();
			break;
		case R.id.upPwd:
			LoadingTool.launchActivity(this, ModifyPwdAct.class);
			break;

		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progressDialog.dismiss();
	}

	@Override
	public void onResponse(JSONObject response) {
		progressDialog.dismiss();
		try {
			LogUtil.e(Tag, "modifyInfo:" + response.toString());
			int res = response.optInt("result", -1);
			if (res == 0) {
				ToastUtil.showMessage("修改成功");
				UserInfo.instance().setNick(
						response.optString("nick", UserInfo.instance()
								.getNick()));
				UserInfo.instance().setSignature(
						response.optString("signature", UserInfo.instance()
								.getSignature()));
				UserInfo.instance().setArea(
						response.optString("area", UserInfo.instance()
								.getArea()));
				UserInfo.instance().setComplete(true);
			} else {
				String msg = response.optString("msg");
				if (!android.text.TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
			}
		} catch (Exception e) {
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (data != null) {
				startPhotoZoom(data.getData());
			}
			break;
		case 2:
			try {
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/icon.jpg");
				startPhotoZoom(Uri.fromFile(temp));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 3:
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startPhotoZoom(Uri uri) {
		if (uri != null) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/jpeg");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, 3);
		}
	}

	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			iconFlag = true;
			Bitmap photo = extras.getParcelable("data");
			BitmapDrawable drawable = new BitmapDrawable(photo);
			updateIcon(drawable);
		}
	}

	private void updateIcon(final BitmapDrawable drawable) {
		Bitmap photo = drawable.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		byte[] b = stream.toByteArray();
		String icon = new String(Base64.encode(b));
		progressDialog.show();
		HttpUtils.modifyIcon(icon, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				progressDialog.dismiss();
				try {
					int res = response.optInt("result", -1);
					if (res == 0) {
						ToastUtil.showMessage("修改成功");
						UserInfo.instance().setIcon(
								response.optString("icon", UserInfo.instance()
										.getIcon()));
						userIcon.setImageUrl(urlHead
								+ UserInfo.instance().getIcon(), imageLoader);
					} else {
						String msg = response.optString("msg");
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
			}
		});
	}
}
