package com.icloud.listenbook.http.datas;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.VolleyUtils;
import com.icloud.listenbook.io.DBHelper;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.pro.HallSocketManage;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.ClientDeviceInfo;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.Baseuser;
import com.listenBook.greendao.Collect;

public class Login {
	public static final String TAG = Login.class.getName();
	public static final String TYPE_YZX = "yzx";
	/** 自动注册 */
	public static final String TYPE_FAST = "icloud";
	public static final String TYPE_REG = "reg";
	public static boolean isgetCollectList = false;

	public static void check() {
		if (!UserInfo.instance().isLogin()) {
			//打开APP时自动登录
			login();
		}
	}

	public static void loginSucceed() {
		if (!UserInfo.instance().isLogin())
			return;
		HandlerUtils.instance().post(new Runnable() {
			@Override
			public void run() {
				HallSocketManage.instance().connSocket();
				/** 获取收藏 */
				getCollectList();

			}
		});

	}

	/**
	 * 检测是否有新版本 一天一次
	 * */
	public static void checkVersion() {
		long time = System.currentTimeMillis();
		if (time - SharedPreferenceUtil.geyVerCheckTime() > DataUpManage.Version_interval) {
			HttpUtils.checkVersion(new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						LogUtil.e(TAG, "checkVersion" + response.toString());

						int res = response.optInt("result", -1);
						if (res == 0) {
							String url = response.optString("url", "");
							String msg = response.optString("msg", "");
							String version = response.optString("version", "");
							boolean isUp = response.optInt("strong", 0) == 0;
							if (!isUp)
								SharedPreferenceUtil.saveVerCheckTime(System
										.currentTimeMillis());
							if (!TextUtils.isEmpty(url)) {
								DialogManage.showUpVersion(url, msg, version,
										isUp);
							}
						}
					} catch (Exception e) {
						e.toString();
					}
				}
			}, null);
		}
	}

	/** 获取收藏 */
	protected static void getCollectList() {
		isgetCollectList = false;
		HttpUtils.collectList(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					isgetCollectList = true;
					LogUtil.e("collectList", response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						long uid = response.optLong("uid");
						JSONArray list = response.optJSONArray("list");
						ArrayList<Collect> collects = new ArrayList<Collect>();
						ArrayList<Article> articleList = new ArrayList<Article>();
						Collect cItem;
						JSONObject json;
						Article AItem;
						for (int i = 0; i < list.length(); i++) {
							json = list.optJSONObject(i);
							AItem = new Article();
							JsonUtils.toArticle(AItem, json);
							cItem = new Collect();
							cItem.setCid(AItem.getAId() + ":" + uid);
							cItem.setAid(AItem.getAId());
							cItem.setUid(uid);
							articleList.add(AItem);
							collects.add(cItem);
						}
						IoUtils.instance().saveArticle(articleList);
						IoUtils.instance().saveCollect(collects);
					}
				} catch (Exception e) {
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isgetCollectList = true;
			}

		});
	}

	public static void login() {
		/** 获取最后登陆账号 */
		Baseuser baseuser = DBHelper.getInstance(GameApp.instance())
				.getLastUser();
		/** 如果没有账号获取did 并作为游客账号登陆 */
		if (baseuser == null
				|| TextUtils.isEmpty(SharedPreferenceUtil.getDeviceId())) {
			getDeviceId(new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject json) {
					try {
						LogUtil.e(TAG, "getDeviceId" + json.toString());
						int res = json.optInt("result", -1);
						if (res == 0) {
							JSONArray cid = json.optJSONArray("deviceId");
							SharedPreferenceUtil.saveDeviceId(cid);
							String account = json.optString("account", "");
							String pwd = json.optString("pwd", "");
							login(account, pwd, TYPE_FAST, null);
						}
					} catch (Exception e) {
					}finally{
						
					}
				}
			}, null);
		} else {
			login(baseuser.getAccount(), baseuser.getPwd(), baseuser.getType(),
					null);
		}

	}

	public static void login(final String account, final String psd,
			String type, String nick) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("account", account);
			jb.put("pwd", psd);
			jb.put("type", type);
			jb.put("nick", nick);
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("system", ClientDeviceInfo.getInstance().system);
			jb.put("verName", ClientDeviceInfo.getInstance().verName);
			jb.put("verCode", ClientDeviceInfo.getInstance().verCode);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "登录login:" + jb.toString());
		updateUserMoneys(null);
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.login, jb,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject json) {
						try {
							int res = json.optInt("result", -1);
							LogUtil.e(TAG, "login:" + json.toString());
							/** 不正确就删除用户 **/
							if (res == 0) {
								UserInfo.instance().parse(json);
								Baseuser baseuser = new Baseuser();
								baseuser.setAccount(UserInfo.instance()
										.getAccount());
								baseuser.setNick(UserInfo.instance().getNick());
								baseuser.setPwd(psd);
								baseuser.setType(json.optString("type",
										TYPE_FAST));
								baseuser.setUid(UserInfo.instance().getUid());
								baseuser.setDate(new Date());
								IoUtils.instance().saveUser(baseuser);
								Login.loginSucceed();
								
								
							} else {
								IoUtils.instance().delUser(account);
							}
						} catch (Exception e) {
						}
					}
				}, null);
	}
	
	private static void updateUserMoneys(JSONObject data) {
//		double currency = data.optDouble("currency", UserInfo.instance()
//				.getCurrency());
		double currency =1000;
		if (currency >= 0) {
			UserInfo.instance().setCurrency(currency);
		}
	}


	public static void getDeviceId(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getDeviceId" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getDeviceId, jb,
				responseList, erroList);
	}

	public static void login(String account, String pwd, String type,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("account", account);
			jb.put("pwd", pwd);
			jb.put("type", type);
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("system", ClientDeviceInfo.getInstance().system);
			jb.put("verName", ClientDeviceInfo.getInstance().verName);
			jb.put("verCode", ClientDeviceInfo.getInstance().verCode);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "login:" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.login, jb, responseList,
				erroList);
	}

	public static void regist(String account, String pwd,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("account", account);
			jb.put("pwd", pwd);
			jb.put("type", TYPE_YZX);
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("system", ClientDeviceInfo.getInstance().system);
			jb.put("verName", ClientDeviceInfo.getInstance().verName);
			jb.put("verCode", ClientDeviceInfo.getInstance().verCode);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "regist" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.regist, jb, responseList,
				erroList);
	}

	public static void updateUserType(String account, String password,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("account", account);
			jb.put("pwd", password);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("system", ClientDeviceInfo.getInstance().system);
			jb.put("verName", ClientDeviceInfo.getInstance().verName);
			jb.put("verCode", ClientDeviceInfo.getInstance().verCode);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "updateUserType" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.updateUserType, jb,
				responseList, erroList);
	}

	public static void complete(String phone, String email,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("phone", phone);
			jb.put("email", email);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("pmodel", ClientDeviceInfo.getInstance().nickname);
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
			jb.put("mac", ClientDeviceInfo.getInstance().mac);
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("channel", ClientDeviceInfo.getInstance().unionid);
			jb.put("system", ClientDeviceInfo.getInstance().system);
			jb.put("verName", ClientDeviceInfo.getInstance().verName);
			jb.put("verCode", ClientDeviceInfo.getInstance().verCode);
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "bind" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.complete, jb,
				responseList, erroList);
	}

	public static void modifyPwd(String pwd, String oldPwd,
			Response.Listener<JSONObject> responseList,
			Response.ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("oldPwd", oldPwd);
			jb.put("pwd", pwd);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "modifyPwd" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.modifyPwd, jb,
				responseList, erroList);
	}
}
