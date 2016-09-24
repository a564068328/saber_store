package com.icloud.listenbook.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.datas.HttpConfig;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.chipFrage.HomePageFrage;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.MethodUtils;
import com.icloud.wrzjh.base.net.HttpSender;
import com.icloud.wrzjh.base.utils.ClientDeviceInfo;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;
import com.listenBook.greendao.Recommend;

public class HttpUtils {
	public static final String TAG = Login.class.getName();

	/** 异常信息 */
	public static void reportException(String exception) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("version", ClientDeviceInfo.getInstance().version);
			jb.put("type", ClientDeviceInfo.getInstance().system + ";"
					+ ClientDeviceInfo.getInstance().nickname);
			jb.put("exception", exception);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("deviceId", SharedPreferenceUtil.getDeviceId());
			jb.put("imei", ClientDeviceInfo.getInstance().imei);
			jb.put("imsi", ClientDeviceInfo.getInstance().imsi);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpSender.post(ServerIps.getLoginAddr() + (HttpConfig.EXCEPTION),
				null, jb.toString());

	}

	public static void login() {
		Login.login();

	}

	public static void readArticle(long cId, int page,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("cId", cId);
			jb.put("page", page);
			jb.put("verCode",
					DataUpManage.getVerCode("readArticle" + jb.toString()));
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "readArticle" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.categoryArticle, jb,
				responseList, erroList);

	}

	public static void readNewArticle(long cId, int page,String verCode,		
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("cId", cId);
			jb.put("page", page);
			jb.put("verCode", verCode);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "readArticle" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.categoryArticle, jb,
				responseList, erroList);

	}

	/** 读取推荐 */
	public static void readRecommend() {
		HttpUtils.getRecommend(0, 0, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int res = response.optInt("result", -1);
					LogUtil.e("readRecommend", response.toString());
					if (res == 0) {
						ArrayList<Recommend> saveitems = JsonUtils
								.toRecommendArray(response
										.optJSONObject("list"),response.optString("verCode", "0"));
//						JSONObject jb = new JSONObject();
//						try {
//							jb.put("media", 0);
//							jb.put("more", 0);
//							DataUpManage.saveVerCode(
//									"getRecommend" + jb.toString(),
//									response.optString("verCode", "0"));
//						} catch (Exception e) {
//
//						}
						IoUtils.instance().clearRecommendNotId(
								(long) Configuration.TYPE_GREAT);
						IoUtils.instance().saveRecommend(saveitems);
						HomePageFrage.sendUpUi();
					}
				} catch (Exception e) {
				}
			}
		}, null);
	}

	/* 获取新增内容 */
//	public static void getNewContent() {
//		// 判断要不要更新内容
//		HttpUtils.readNewArticle(IoUtils.timeId[0], 0,
//				new Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject response) {
//						LogUtil.e("readNewArticle", response.toString());
//						int res = response.optInt("result", -1);
//						if (res == 0) {
////							List<Long> allCid = MethodUtils.getAllCid();
////							new MethodUtils(allCid).getAllNewArticle();
//						}
//					}
//				}, null);
//	}

	public static void getArticleInfo(long aId,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("aId", aId);
		} catch (JSONException e) {
		}
		// LogUtil.e(TAG, "getArticleInfo" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.articleInfo, jb,
				responseList, erroList);
	}

	public static void getPrivate(String url,
			Response.Listener<String> responseList,
			Response.ErrorListener erroList) {
		get(ServerIps.getLoginAddr() + url, responseList, erroList);
	}

	public static void get(String url, Response.Listener<String> responseList,
			Response.ErrorListener erroList) {
		VolleyUtils.instance().get(url, responseList, erroList);
	}

	public static void getArticleChapterInfo(long aId, int page,
			Listener<JSONObject> responseList, ErrorListener erroList) {

		JSONObject jb = new JSONObject();
		try {
			jb.put("aId", aId);
			jb.put("page", page);
			// jb.put("verCode",
			// DataUpManage.getVerCode("getArticleChapterInfo"
			// + jb.toString()));
			jb.put("verCode", "0");
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "articleChapterInfo" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.articleChapterInfo, jb,
				responseList, erroList);
	}

	public static void getArticleFeedback(long aId, int page,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("aId", aId);
			jb.put("page", page);

		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getArticleFeedback" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.articleFeedback, jb,
				responseList, erroList);
	}

	public static void getHome(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("verCode", SharedPreferenceUtil.getHomeverCode());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getHome" + jb.toString());
		VolleyUtils.instance().post(ServerIps.getLoginAddr() + HttpConfig.find,
				jb, responseList, erroList);
	}

	public static void getRecommend(long media, int more,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("media", media);
			jb.put("more", more);
			jb.put("verCode",
					DataUpManage.getVerCode("getRecommend" + jb.toString()));
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getRecommend" + jb.toString());
		VolleyUtils.instance().post(ServerIps.getLoginAddr() + HttpConfig.push,
				jb, responseList, erroList);
	}

	/****
	 * 
	 Type 可选 String downNum：下载数 clickNum：点击数 buyNum：购买数 praiseNum：好评数
	 * badNum：差评数
	 * 
	 * */
	public static void getRanks(int rankid, String type,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("rankid", rankid);
			jb.put("type", type);
			jb.put("verCode",
					DataUpManage.getVerCode("getRanks" + jb.toString()));
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getRanks" + jb.toString());
		VolleyUtils.instance().post(ServerIps.getLoginAddr() + HttpConfig.rank,
				jb, responseList, erroList);
	}

	public static void getPPT(Listener<JSONObject> responseList,
			ErrorListener erroList, int id) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("id", id);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getPPT" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getPPT, jb, responseList,
				erroList);
	}

	public static void getHomeAds() {
		final int type = 0;
		final int length = 4;
		getAds(type, length, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LogUtil.e(TAG, "getAds" + response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						JSONArray list = response.optJSONArray("list");
						ArrayList<Ads> asds = new ArrayList<Ads>();
						Ads item;
						JSONObject obj;
						for (int i = 0; i < list.length(); i++) {
							obj = list.optJSONObject(i);
							item = JsonUtils.toAds(type, obj);
							asds.add(item);
						}
						if (asds.size() > 0) {
							IoUtils.instance().clearAds();
							IoUtils.instance().saveAds(asds);
						}
						HomePageFrage.sendUpUi();
					}
				} catch (Exception e) {
				}
			}
		}, null);

	}

	public static void getAds(int type, int length,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("type", type);
			jb.put("length", length);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getAds" + jb.toString());
		VolleyUtils.instance().post(ServerIps.getLoginAddr() + HttpConfig.ads,
				jb, responseList, erroList);
	}

	public static void search(String keywords,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("keywords", keywords);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "search" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.search, jb, responseList,
				erroList);
	}

	/** 发表评论 **/
	public static void addFeedback(long aId, String msg,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("aId", aId);
			jb.put("msg", msg);
			/** 星 可选 */
			jb.put("starts", 5);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "addFeedback" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.addFeedBack, jb,
				responseList, erroList);
	}

	/***
	 * 反馈 fid 评论id 默认为0
	 * */
	public static void addAppFeedback(long fid, String msg,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("fid", fid);
			jb.put("msg", msg);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "AddAppFeedback" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.addAppFeedback, jb,
				responseList, erroList);
	}

	/***
	 * 获取反馈 fid 评论id 默认为0
	 * */
	public static void appFeedbackList(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "appFeedbackList" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.appFeedbackList, jb,
				responseList, erroList);
	}
    /**
     * @param cpId 章节ID
    **/
	public static void deductCurrency(long aId, long cpid,
			Listener<JSONObject> responseList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("cpId", cpid);
			jb.put("aId", aId);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "deductCurrency" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.deductCurrency, jb,
				responseList, null);
	}
	public static void isPay(long aId, long cpid,
			Listener<JSONObject> responseList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("cpId", cpid);
			jb.put("aId", aId);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "isPay" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.isPay, jb,
				responseList, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
					}
				});
	}
	public static void getUserInfo(long fuid,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("fuid", fuid);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getUserInfo" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getUserInfo, jb,
				responseList, erroList);
	}

	public static void friendAdd(long fuid, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("fuid", fuid);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "friendAdd" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.friendAdd, jb,
				responseList, erroList);
	}

	public static void friendDel(long fuid, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("fuid", fuid);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "friendDel" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.friendDel, jb,
				responseList, erroList);
	}

	public static void friendsList(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "friendsList" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.friendsList, jb,
				responseList, erroList);
	}

	public static void modifyInfo(String nick, String signature, String area,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("nick", nick);
			jb.put("signature", signature);
			jb.put("area", area);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "modifyInfo" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.modifyInfo, jb,
				responseList, erroList);
	}

	/***
	 * type 1：账号绑定手机 2：忘记密码 3：重置绑定手机
	 */
	public static void getSms(String phone, int type,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("phone", phone);
			jb.put("type", type);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getSms" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getSms, jb, responseList,
				erroList);
	}

	public static void setPhone(String phone, String msgCode,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("phone", phone);
			jb.put("msgCode", msgCode);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "setPhone" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.setPhone, jb,
				responseList, erroList);
	}

	public static void modifyPhone(String phone, String oldPhone,
			String msgCode, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("phone", phone);
			jb.put("oldPhone", oldPhone);
			jb.put("msgCode", msgCode);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "modifyPhone" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.modifyPhone, jb,
				responseList, erroList);
	}

	public static void forgetPwd(String account, String pwd, String phone,
			String msgCode, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("account", account);
			jb.put("pwd", pwd);
			jb.put("phone", phone);
			jb.put("msgCode", msgCode);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "forgetPwd" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.forgetPwd, jb,
				responseList, erroList);
	}

	public static void collectAdd(long aid, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("aId", aid);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "collectAdd" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.collectAdd, jb,
				responseList, erroList);
	}

	public static void collectDel(long aid, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("aId", aid);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "collectDel" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.collectDel, jb,
				responseList, erroList);
	}

	public static void collectList(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "collectList" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.collectList, jb,
				responseList, erroList);
	}

	public static void checkVersion(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("pv", ClientDeviceInfo.getInstance().pv);
			jb.put("version", ClientDeviceInfo.getInstance().verCode);
			jb.put("unionid", ClientDeviceInfo.getInstance().unionid);
			jb.put("pkg", ClientDeviceInfo.getInstance().pkg);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "checkVersion" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.checkVersion, jb,
				responseList, erroList);
	}

	public static void modifyIcon(String icon,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("icon", icon);
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "modifyIcon" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.modifyIcon, jb,
				responseList, erroList);
	}

	public static void getUserIcon(long fuid,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("fuid", fuid);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "getUserIcon" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getUserIcon, jb,
				responseList, erroList);
	}

	public static void Thanks(String code, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("code", code);
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
		} catch (JSONException e) {
		}
		LogUtil.e(TAG, "thanks" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.thanks, jb, responseList,
				erroList);
	}

	public static void getMembersList(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();

		LogUtil.e(TAG, "getMembersList" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getMembersList, jb,
				responseList, erroList);
	}

	public static void getGreatMaster(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();

		LogUtil.e(TAG, "getGreatMaster" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getGreatMaster, jb,
				responseList, erroList);
	}

	public static void getLectureList(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();

		LogUtil.e(TAG, "getLectureList" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getLectureList, jb,
				responseList, erroList);
	}

	public static void getNotice(Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();

		LogUtil.e(TAG, "getNotice" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.getNotice, jb,
				responseList, erroList);
	}

	// 提交功过表成人版
	public static void sumbitAdult(long uid, MeritTableAdult info,
			Listener<JSONObject> responseList, ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", uid);
			jb.put("token", UserInfo.instance().getToken());
			jb.put("level", 1);
			JSONArray jsonArray = new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("id", 1);
			obj.put("level", 1);
			obj.put("signValue", info.getIdea());
			obj.put("remark", info.getIdeades());
			obj.put("desc", info.getIdeaexc());
			jsonArray.put(0, obj);
			obj = new JSONObject();
			obj.put("id", 2);
			obj.put("level", 1);
			obj.put("signValue", info.getAttitude());
			obj.put("remark", info.getAttitudedes());
			obj.put("desc", info.getAttitudeexc());
			jsonArray.put(1, obj);
			obj = new JSONObject();
			obj.put("id", 3);
			obj.put("level", 1);
			obj.put("signValue", info.getAction());
			obj.put("remark", info.getActiondes());
			obj.put("desc", info.getActionexc());
			jsonArray.put(2, obj);
			obj = new JSONObject();
			obj.put("id", 4);
			obj.put("level", 1);
			obj.put("signValue", info.getTreat());
			obj.put("remark", info.getTreatdes());
			obj.put("desc", info.getTreatexc());
			jsonArray.put(3, obj);
			obj = new JSONObject();
			obj.put("id", 5);
			obj.put("level", 1);
			obj.put("signValue", info.getWork());
			obj.put("remark", info.getWorkdes());
			obj.put("desc", info.getWorkexc());
			jsonArray.put(4, obj);
			obj = new JSONObject();
			obj.put("id", 6);
			obj.put("level", 1);
			obj.put("signValue", info.getBelief());
			obj.put("remark", info.getBeliefdes());
			obj.put("desc", info.getBeliefexc());
			jsonArray.put(5, obj);
			obj = new JSONObject();
			obj.put("id", 7);
			obj.put("level", 1);
			obj.put("signValue", info.getOther());
			obj.put("remark", info.getOtherdes());
			obj.put("desc", info.getOtherexc());
			jsonArray.put(6, obj);
			jb.put("list", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.e(TAG, "提交功过表成人版" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.sumbitAdult, jb,
				responseList, erroList);
	}

	// 搜索功过表
	public static void searchTable(long uid, String statrTime, String endTime,
			boolean isAdult, Listener<JSONObject> responseList,
			ErrorListener erroList) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", uid);
			jb.put("token", UserInfo.instance().getToken());
			jb.put("startdate", statrTime);
			jb.put("enddate", endTime);
			if (isAdult)
				jb.put("level", 1);
			else {
				jb.put("level", 0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.e(TAG, "搜索功过表" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.searchAdult, jb,
				responseList, erroList);
	}

	public static void sumbitChildren(long uid, MeritTableChildren info,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", uid);
			jb.put("token", UserInfo.instance().getToken());
			jb.put("level", 0);
			JSONArray jsonArray = new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("id", 8);
			obj.put("level", 0);
			obj.put("signValue", info.getSchedules());
			obj.put("remark", info.getSchedulesdes());
			obj.put("desc", info.getSchedulesexc());
			jsonArray.put(0, obj);
			obj = new JSONObject();

			obj.put("id", 9);
			obj.put("level", 0);
			obj.put("signValue", info.getAttitude());
			obj.put("remark", info.getAttitudedes());
			obj.put("desc", info.getAttitudeexc());
			jsonArray.put(1, obj);

			obj = new JSONObject();
			obj.put("id", 10);
			obj.put("level", 0);
			obj.put("signValue", info.getStudy());
			obj.put("remark", info.getStudydes());
			obj.put("desc", info.getStudyexc());
			jsonArray.put(2, obj);

			obj = new JSONObject();
			obj.put("id", 11);
			obj.put("level", 0);
			obj.put("signValue", info.getLove());
			obj.put("remark", info.getLovedes());
			obj.put("desc", info.getLoveexc());
			jsonArray.put(3, obj);

			obj = new JSONObject();
			obj.put("id", 12);
			obj.put("level", 0);
			obj.put("signValue", info.getRespect());
			obj.put("remark", info.getRespectdes());
			obj.put("desc", info.getRespectexc());
			jsonArray.put(4, obj);

			obj = new JSONObject();
			obj.put("id", 13);
			obj.put("level", 0);
			obj.put("signValue", info.getAction());
			obj.put("remark", info.getActiondes());
			obj.put("desc", info.getActionexc());
			jsonArray.put(5, obj);

			obj = new JSONObject();
			obj.put("id", 14);
			obj.put("level", 0);
			obj.put("signValue", info.getOther());
			obj.put("remark", info.getOtherdes());
			obj.put("desc", info.getOtherexc());
			jsonArray.put(6, obj);
			jb.put("list", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.e(TAG, "提交功过表孩子版" + jb.toString());
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.sumbitAdult, jb,
				listener, errorListener);

	}

	// return true为有网
	public static boolean isNetworkAvailable(Context act) {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.getState() == NetworkInfo.State.CONNECTED)
			return true;
		return false;
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();

		/*
		 * 一、判断网络是否是wifi，在判断之前一定要进行的非空判断，如果没有任何网络 连接
		 */
		if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI)
			return true;
		return false;
	}

	public static void getMedia() {
		if (DataUpManage.isUp(GameApp.getContext(), "getMediumCache")) {
			HttpUtils.getHome(new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject json) {
					// TODO Auto-generated method stub
					try {
						int res = json.optInt("result", -1);
						LogUtil.e(TAG, json.toString());
						if (res == 0) {
							IoUtils.instance().saveMedia(json);
							long verCode = json.optLong("verCode", 0);
							SharedPreferenceUtil.saveHomeverCode(verCode);
						}
					} catch (Exception e) {
					}
				}

			}, null);
		}
	}

	public static void getChant() {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("version", SharedPreferenceUtil.getChantVersion());
			LogUtil.e(TAG, jb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.chant, jb,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response)  {
						LogUtil.e(TAG, response.toString());
						JsonUtils.refreshChant(response);
					}
				}, null);
	}
}
