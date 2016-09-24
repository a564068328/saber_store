package com.icloud.listenbook.unit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.AdultInfo;
import com.icloud.listenbook.entity.SearchData;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.ThirdAdult;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.listenBook.greendao.MeritTableAdult;
import com.yunva.live.sdk.interfaces.logic.model.GetAwardsNotify;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class AdultUtils {
	private final int NO = 0;
	private final int GONG = NO + 1;
	private final int GUO = GONG + 1;
	private final int ALL = GUO + 1;
	private final static String TAG = "AdultUtils";
	private Context AUcontext;
	private static final int MAX_NUMBER = 7;
	public static int[] array;
	MeritTableAdult adult;
	Date startDate;
	Date endDate;
	List<MeritTableAdult> lists;

	public List<MeritTableAdult> getLists() {
		return lists;
	}

	public void setLists() {
		if (lists != null)
			lists.clear();
	}

	private AdultUtils() {
	}

	protected static AdultUtils instance;

	public static AdultUtils instance() {
		if (instance == null)
			instance = new AdultUtils();
		return instance;
	}

	public void submitEvent(Context context, final TextView submitwait) {
		instance.AUcontext = context;
		LogUtil.e(TAG, "" + AdultInfo.instance().getCredit()
				+ AdultInfo.instance().getFault());

		long date = System.currentTimeMillis();
		// SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
		// Date curDate=new Date(date);
		// String str=formatter.format(curDate);
		// long newdateLong=Long.valueOf(str);

		long uid = UserInfo.instance().getUid();
		LogUtil.e(TAG, "" + uid);
		if (uid <= 0) {
			ToastUtil.showMessage("账号错误请重新登陆！");
			Intent intent = new Intent(AUcontext, LoginAct.class);
			AUcontext.startActivity(intent);
			return;
		} else {

			if (AdultInfo.instance().getCredit()
					+ AdultInfo.instance().getFault() == 0) {
				ToastUtil.showMessage("每日应有所得,再想想吧");
				return;
			}
			adult = new MeritTableAdult();
			adult.setUid(uid);
			// adult.setDate(date);
			LogUtil.e(TAG, "新加入功过表！\n");
			// adult.setAction(AdultInfo.instance().isAction());
			// adult.setAttitude(AdultInfo.instance().isAttitude());
			// adult.setBelief(AdultInfo.instance().isBelief());
			// adult.setIdea(AdultInfo.instance().isIdea());
			// adult.setOther(AdultInfo.instance().isOther());
			// adult.setTreat(AdultInfo.instance().isTreat());
			// adult.setWork(AdultInfo.instance().isWork());
			if (AdultInfo.instance().isIdeaGong()
					&& AdultInfo.instance().isIdeaGuo()) {
				adult.setIdea(ALL);
			} else if (!AdultInfo.instance().isIdeaGong()
					&& AdultInfo.instance().isIdeaGuo()) {
				adult.setIdea(GUO);
			} else if (AdultInfo.instance().isIdeaGong()
					&& !AdultInfo.instance().isIdeaGuo()) {
				adult.setIdea(GONG);
			} else {
				adult.setIdea(NO);
			}

			if (AdultInfo.instance().isAttitudeGong()
					&& AdultInfo.instance().isAttitudeGuo()) {
				adult.setAttitude(ALL);
			} else if (!AdultInfo.instance().isAttitudeGong()
					&& AdultInfo.instance().isAttitudeGuo()) {
				adult.setAttitude(GUO);
			} else if (AdultInfo.instance().isAttitudeGong()
					&& !AdultInfo.instance().isAttitudeGuo()) {
				adult.setAttitude(GONG);
			} else {
				adult.setAttitude(NO);
			}

			if (AdultInfo.instance().isActionGong()
					&& AdultInfo.instance().isActionGuo()) {
				adult.setAction(ALL);
			} else if (!AdultInfo.instance().isActionGong()
					&& AdultInfo.instance().isActionGuo()) {
				adult.setAction(GUO);
			} else if (AdultInfo.instance().isActionGong()
					&& !AdultInfo.instance().isActionGuo()) {
				adult.setAction(GONG);
			} else {
				adult.setAction(NO);
			}

			if (AdultInfo.instance().isTreatGong()
					&& AdultInfo.instance().isTreatGuo()) {
				adult.setTreat(ALL);
			} else if (!AdultInfo.instance().isTreatGong()
					&& AdultInfo.instance().isTreatGuo()) {
				adult.setTreat(GUO);
			} else if (AdultInfo.instance().isTreatGong()
					&& !AdultInfo.instance().isTreatGuo()) {
				adult.setTreat(GONG);
			} else {
				adult.setTreat(NO);
			}

			if (AdultInfo.instance().isWorkGong()
					&& AdultInfo.instance().isWorkGuo()) {
				adult.setWork(ALL);
			} else if (!AdultInfo.instance().isWorkGong()
					&& AdultInfo.instance().isWorkGuo()) {
				adult.setWork(GUO);
			} else if (AdultInfo.instance().isWorkGong()
					&& !AdultInfo.instance().isWorkGuo()) {
				adult.setWork(GONG);
			} else {
				adult.setWork(NO);
			}

			if (AdultInfo.instance().isBeliefGong()
					&& AdultInfo.instance().isBeliefGuo()) {
				adult.setBelief(ALL);
			} else if (!AdultInfo.instance().isBeliefGong()
					&& AdultInfo.instance().isBeliefGuo()) {
				adult.setBelief(GUO);
			} else if (AdultInfo.instance().isBeliefGong()
					&& !AdultInfo.instance().isBeliefGuo()) {
				adult.setBelief(GONG);
			} else {
				adult.setBelief(NO);
			}

			if (AdultInfo.instance().isOtherGong()
					&& AdultInfo.instance().isOtherGuo()) {
				adult.setOther(ALL);
			} else if (!AdultInfo.instance().isOtherGong()
					&& AdultInfo.instance().isOtherGuo()) {
				adult.setOther(GUO);
			} else if (AdultInfo.instance().isOtherGong()
					&& !AdultInfo.instance().isOtherGuo()) {
				adult.setOther(GONG);
			} else {
				adult.setOther(NO);
			}

			adult.setGong(AdultInfo.instance().getCredit());
			adult.setGuo(AdultInfo.instance().getFault());

			if (TextUtils.isEmpty(AdultInfo.instance().getIdeaDEs()))
				adult.setIdeades("");
			else
				adult.setIdeades(AdultInfo.instance().getIdeaDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getIdeaExc()))
				adult.setIdeaexc("");
			else
				adult.setIdeaexc(AdultInfo.instance().getIdeaExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getAttitudeDEs()))
				adult.setAttitudedes("");
			else
				adult.setAttitudedes(AdultInfo.instance().getAttitudeDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getAttitudeExc()))
				adult.setAttitudeexc("");
			else
				adult.setAttitudeexc(AdultInfo.instance().getAttitudeExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getActionDEs()))
				adult.setActiondes("");
			else
				adult.setActiondes(AdultInfo.instance().getActionDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getActionExc()))
				adult.setActionexc("");
			else
				adult.setActionexc(AdultInfo.instance().getActionExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getTreatDEs()))
				adult.setTreatdes("");
			else
				adult.setTreatdes(AdultInfo.instance().getTreatDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getTreatExc()))
				adult.setTreatexc("");
			else
				adult.setTreatexc(AdultInfo.instance().getTreatExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getWorkDEs()))
				adult.setWorkdes("");
			else
				adult.setWorkdes(AdultInfo.instance().getWorkDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getWorkExc()))
				adult.setWorkexc("");
			else
				adult.setWorkexc(AdultInfo.instance().getWorkExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getBeliefDEs()))
				adult.setBeliefdes("");
			else
				adult.setBeliefdes(AdultInfo.instance().getBeliefDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getBeliefExc()))
				adult.setBeliefexc("");
			else
				adult.setBeliefexc(AdultInfo.instance().getBeliefExc());

			if (TextUtils.isEmpty(AdultInfo.instance().getOtherDEs()))
				adult.setOtherdes("");
			else
				adult.setOtherdes(AdultInfo.instance().getOtherDEs());
			if (TextUtils.isEmpty(AdultInfo.instance().getOtherExc()))
				adult.setOtherexc("");
			else
				adult.setOtherexc(AdultInfo.instance().getOtherExc());

			adult.setGong(AdultInfo.instance().getCredit());
			adult.setGuo(AdultInfo.instance().getFault());
			submitwait.setVisibility(View.VISIBLE);

			HttpUtils.sumbitAdult(uid, adult, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						int res = response.optInt("result", -1);

						LogUtil.e(TAG, "搜索得到" + response.toString());
						if (res == 0) {
							double Currency = response.optDouble("currency",
									UserInfo.instance().getCurrency());
							UserInfo.instance().setCurrency(Currency);
//							IoUtils.instance().saveMeritTableAdult(adult);
							ToastUtil.showMessage("提交成功！已获得20金币，请注意查收");
						} else {
							ToastUtil.showMessage(response.optString("msg"));
						}

					} catch (Exception e) {
					}
					if (submitwait.getVisibility() == View.VISIBLE) {
						submitwait.setVisibility(View.GONE);
					}
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					LogUtil.e(TAG, "错误" + error.toString());
				}
			});

			return;

		}

	}

	public int[] SearchEvent(Context context, final ThirdAdult thirdAdult,
			String statrTime, String endTime, final TextView searchwait,
			final View submit) {
		instance.AUcontext = context;
		int[] arraybuf = null;
		long uid = UserInfo.instance().getUid();
		array = new int[MAX_NUMBER * 2 + 2];
		LogUtil.e(TAG, "" + uid);
		if (uid <= 0) {
			ToastUtil.showMessage("账号错误请重新登陆！");
			Intent intent = new Intent(AUcontext, LoginAct.class);
			AUcontext.startActivity(intent);
			return null;
		} else {
			if (TextUtils.isEmpty(statrTime) || TextUtils.isEmpty(endTime)) {
				ToastUtil.showMessage("请输入起始日期和结束日期");
				return null;
			} else {
				searchwait.setVisibility(View.VISIBLE);
				if (Long.valueOf(statrTime) > Long.valueOf(endTime)) {
					String timeBuf = statrTime;
					statrTime = endTime;
					endTime = timeBuf;
				}
				StringBuilder st = new StringBuilder(statrTime);
				st.insert(4, "-");
				st.insert(7, "-");
				statrTime = st.toString();
				st = new StringBuilder(endTime);
				st.insert(4, "-");
				st.insert(7, "-");
				endTime = st.toString();
				LogUtil.e(TAG, "" + statrTime + endTime);
				if (HttpUtils.isNetworkAvailable(context)) {
					HttpUtils.searchTable(uid, statrTime, endTime, true,
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									try {
										int res = response.optInt("result", -1);

										if (res == 0) {
											lists = new ArrayList<MeritTableAdult>();
											searchwait.setVisibility(View.GONE);
											submit.setVisibility(View.VISIBLE);
											LogUtil.e(TAG, "res=" + res);
											LogUtil.e(TAG, "response="
													+ response.toString());
											JSONObject list = response
													.optJSONObject("list");
//											LogUtil.e(TAG,
//													"list=" + list.toString());

											Iterator it = list.keys();
											while (it.hasNext()) {
												MeritTableAdult Adult = new MeritTableAdult();
												int[] buffarray=new int[MAX_NUMBER * 2 + 2];
												String key = it.next()
														.toString();
												StringBuilder sb=new StringBuilder(key);
												if(key.length()==3)
												{						
													sb.insert(0, "记录日期0");
												}else{
													sb.insert(0, "记录日期");
												}
												sb.insert(6, "月");
												sb.append("日");
												Adult.setDate(sb.toString());
												JSONArray item = list
														.optJSONArray(key);
												for (int i = 0; i < item
														.length(); i++) {
													JSONObject chipJsonObj = item
															.optJSONObject(i);
													int id = chipJsonObj
															.optInt("id");
													int value = chipJsonObj
															.optInt("signValue");
													String remark=chipJsonObj
															.optString("remark");
													String desc=chipJsonObj
															.optString("desc");
													if (id == 1) {
														Adult.setIdea(value);
														Adult.setIdeades(remark);
														Adult.setIdeaexc(desc);
													} else if (id == 2) {
														Adult.setAttitude(value);
														Adult.setAttitudedes(remark);
														Adult.setAttitudeexc(desc);
													} else if (id == 3) {
														Adult.setAction(value);
														Adult.setActiondes(remark);
														Adult.setActionexc(desc);
													} else if (id == 4) {
														Adult.setTreat(value);
														Adult.setTreatdes(remark);
														Adult.setTreatexc(desc);
													} else if (id == 5) {
														Adult.setWork(value);
														Adult.setWorkdes(remark);
														Adult.setWorkexc(desc);
													} else if (id == 6) {
														Adult.setBelief(value);
														Adult.setBeliefdes(remark);
														Adult.setBeliefexc(desc);
													} else if (id == 7) {
														Adult.setOther(value);
														Adult.setOtherdes(remark);
														Adult.setOtherexc(desc);
													}
													if (value == 3) {
														buffarray[id*2-2] += 1;
														buffarray[id*2-1] += 1;
														//LogUtil.e(TAG, "buffarray"+buffarray[id*2-1]);
													} else if (value == 2) {
														buffarray[id*2-1] += 1;
													} else if (value == 1) {
														buffarray[id*2-2] += 1;
													}
													Adult.setGong(buffarray[0] + buffarray[2]
															+ buffarray[4] + buffarray[6]
																	+ buffarray[8] + buffarray[10]
																	+ buffarray[12]);
													Adult.setGuo(buffarray[1] + buffarray[3]
													+ buffarray[5] + buffarray[7]
													+ buffarray[9] + buffarray[11]
													+ buffarray[13]);
												}
												lists.add(Adult);
												for(int i=0;i<MAX_NUMBER*2;i++){
													array[i]+=buffarray[i];
												}
												// LogUtil.e(TAG,
												// "optJSONObject=" +
												// optJSONObject.toString());
												// String
												// date=optJSONObject.toString();
												// String strm[] =
												// date.split("\"");
												// date=strm[1];
												// LogUtil.e(TAG, "date=" +
												// date);
												// JSONArray
												// key=list.optJSONArray("504");
												//
												// LogUtil.e(TAG, "key=" +
												// key.toString());
												// JSONArray
												// date=key.optJSONArray();
												// LogUtil.e(TAG, "date=" +
												// date.toString());
												// LogUtil.e(
												// TAG,
												// "date="+ date.toString());
												// int i = chipJsonObj
												// .optInt("idea");
												// Adult.setId(UserInfo.instance()
												// .getUid());
												// Adult.setDate(chipJsonObj
												// .optInt("date"));
												// Adult.setIdea(i);
												// Adult.setIdeades(chipJsonObj
												// .optString("ideades"));
												// Adult.setIdeaexc(chipJsonObj
												// .optString("ideaexc"));
												// if (i == 3) {
												// array[0] += 1;
												// array[1] += 1;
												// } else if (i == 2) {
												// array[1] += 1;
												// } else if (i == 1) {
												// array[0] += 1;
												// }
												//
												// i = chipJsonObj
												// .optInt("attitude");
												// Adult.setAttitude(i);
												// Adult.setAttitudedes(chipJsonObj
												// .optString("attitudedes"));
												// Adult.setAttitudeexc(chipJsonObj
												// .optString("attitudeexc"));
												// if (i == 3) {
												// array[2] += 1;
												// array[3] += 1;
												// } else if (i == 2) {
												// array[3] += 1;
												// } else if (i == 1) {
												// array[2] += 1;
												// }
												//
												// i = chipJsonObj
												// .optInt("action");
												// Adult.setAction(i);
												// Adult.setActiondes(chipJsonObj
												// .optString("actiondes"));
												// Adult.setActionexc(chipJsonObj
												// .optString("actionexc"));
												// if (i == 3) {
												// array[4] += 1;
												// array[5] += 1;
												// } else if (i == 2) {
												// array[5] += 1;
												// } else if (i == 1) {
												// array[4] += 1;
												// }
												//
												// i =
												// chipJsonObj.optInt("treat");
												// Adult.setTreat(i);
												// Adult.setTreatdes(chipJsonObj
												// .optString("treatdes"));
												// Adult.setTreatexc(chipJsonObj
												// .optString("treatexc"));
												// if (i == 3) {
												// array[6] += 1;
												// array[7] += 1;
												// } else if (i == 2) {
												// array[7] += 1;
												// } else if (i == 1) {
												// array[6] += 1;
												// }
												//
												// i =
												// chipJsonObj.optInt("work");
												// Adult.setWork(i);
												// Adult.setWorkdes(chipJsonObj
												// .optString("workdes"));
												// Adult.setWorkexc(chipJsonObj
												// .optString("workexc"));
												// if (i == 3) {
												// array[8] += 1;
												// array[9] += 1;
												// } else if (i == 2) {
												// array[9] += 1;
												// } else if (i == 1) {
												// array[8] += 1;
												// }
												//
												// i = chipJsonObj
												// .optInt("belief");
												// Adult.setBelief(i);
												// Adult.setBeliefdes(chipJsonObj
												// .optString("beliefdes"));
												// Adult.setBeliefexc(chipJsonObj
												// .optString("beliefexc"));
												// if (i == 3) {
												// array[10] += 1;
												// array[11] += 1;
												// } else if (i == 2) {
												// array[11] += 1;
												// } else if (i == 1) {
												// array[10] += 1;
												// }
												//
												// i =
												// chipJsonObj.optInt("other");
												// Adult.setOther(i);
												// Adult.setOtherdes(chipJsonObj
												// .optString("otherdes"));
												// Adult.setOtherexc(chipJsonObj
												// .optString("otherexc"));
												// if (i == 3) {
												// array[12] += 1;
												// array[13] += 1;
												// } else if (i == 2) {
												// array[13] += 1;
												// } else if (i == 1) {
												// array[12] += 1;
												// }
												//
												// Adult.setGong(chipJsonObj
												// .optInt("gong"));
												// Adult.setGuo(chipJsonObj
												// .optInt("guo"));

											}
											array[14] = array[0] + array[2]
													+ array[4] + array[6]
													+ array[8] + array[10]
													+ array[12];
											array[15] = array[1] + array[3]
													+ array[5] + array[7]
													+ array[9] + array[11]
													+ array[13];
											
											thirdAdult.setSearchResult(array);
//											LogUtil.e(TAG, "解析网络功过格结果"
//													+ array[14] + array[15]);
											if (searchwait.getVisibility() == View.VISIBLE) {
												ToastUtil.showMessage("查询成功");
												searchwait
														.setVisibility(View.GONE);
											}
										} else {
											if (searchwait.getVisibility() == View.VISIBLE) {
												searchwait
														.setVisibility(View.GONE);
											}
											ToastUtil.showMessage(response
													.optString("msg"));
										}

									} catch (Exception e) {
										if (searchwait.getVisibility() == View.VISIBLE) {
											searchwait.setVisibility(View.GONE);
										}
										e.printStackTrace();
										//LogUtil.e(TAG, "res=" + "json解析错误");
									}
								}
							}, null);
				} else {
					ToastUtil.showMessage("网络不给力，请再给我一次机会");
					// arraybuf = Parse(uid, statrTime, endTime, searchwait);
					// thirdAdult.setSearchResult(array);
				}
			}
		}
		return null;
	}

//	private int[] Parse(long uid, String statrTime, String endTime,
//			TextView searchwait) {
//		// TODO Auto-generated method stub
//		List<MeritTableAdult> lists;
////		lists = IoUtils.instance().getMeritTableAdultToDate(uid, statrTime,
////				endTime);
//		if (searchwait.getVisibility() == View.VISIBLE) {
//			searchwait.setVisibility(View.GONE);
//		}
//		for (MeritTableAdult list : lists) {
//			// if (list.getIdea()) {
//			// array[0] += 1;
//			// } else {
//			// array[1] += 1;
//			// }
//			// if (list.getAttitude()) {
//			// array[2] += 1;
//			// } else {
//			// array[3] += 1;
//			// }
//			// if (list.getAction()) {
//			// array[4] += 1;
//			// } else {
//			// array[5] += 1;
//			// }
//			// if (list.getTreat()) {
//			// array[6] += 1;
//			// } else {
//			// array[7] += 1;
//			// }
//			// if (list.getWork()) {
//			// array[8] += 1;
//			// } else {
//			// array[9] += 1;
//			// }
//			// if (list.getBelief()) {
//			// array[10] += 1;
//			// } else {
//			// array[11] += 1;
//			// }
//			//
//			// if (list.getOther()) {
//			// array[12] += 1;
//			// } else {
//			// array[13] += 1;
//			// }
//		}
//		lists.clear();
//		array[14] = array[0] + array[2] + array[4] + array[6] + array[8]
//				+ array[10] + array[12];
//		array[15] = array[1] + array[3] + array[5] + array[7] + array[9]
//				+ array[11] + array[13];
//		return array;
//	}
}
