package com.icloud.listenbook.unit;

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

import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.AdultInfo;
import com.icloud.listenbook.entity.ChildrenInfo;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.ThirdAdult;
import com.icloud.listenbook.ui.chipAct.ThirdChildren;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;
import com.yunva.live.sdk.interfaces.logic.model.GetAwardsNotify;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ChildrenUtils {
	private final int NO = 0;
	private final int GONG = NO + 1;
	private final int GUO = GONG + 1;
	private final int ALL = GUO + 1;
	private final static String TAG = "ChildrenUtils";
	private Context AUcontext;
	private static final int MAX_NUMBER = 7;
	public static int[] array;
	MeritTableChildren Children;
	Date startDate;
	Date endDate;
	List<MeritTableChildren> lists;

	public List<MeritTableChildren> getLists() {
		return lists;
	}

	public void setLists() {
		if (lists != null)
			lists.clear();
	}

	private ChildrenUtils() {
	}

	protected static ChildrenUtils instance;

	public static ChildrenUtils instance() {
		if (instance == null)
			instance = new ChildrenUtils();
		return instance;
	}

	public void submitEvent(Context context, final TextView submitwait) {
		instance.AUcontext = context;
		LogUtil.e(TAG, "" + ChildrenInfo.instance().getCredit()
				+ ChildrenInfo.instance().getFault());

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

			Children = new MeritTableChildren();
			Children.setUid(uid);
//			Children.setDate(date);
			LogUtil.e(TAG, "" + date);
			// if (!IoUtils.instance().isSaveChildren(Children)) {
			LogUtil.e(TAG, "新加入功过表！\n");
			// Children.setSchedules(ChildrenInfo.instance().isSchedules());
			// Children.setAttitude(ChildrenInfo.instance().isAttitude());
			// Children.setStudy(ChildrenInfo.instance().isStudy());
			// Children.setLove(ChildrenInfo.instance().isLove());
			// Children.setOther(ChildrenInfo.instance().isOther());
			// Children.setRespect(ChildrenInfo.instance().isRespect());
			// Children.setAction(ChildrenInfo.instance().isAction());
			submitwait.setVisibility(View.VISIBLE);

			if (ChildrenInfo.instance().isSchedulesGong()
					&& ChildrenInfo.instance().isSchedulesGuo()) {
				Children.setSchedules(ALL);
			} else if (!ChildrenInfo.instance().isSchedulesGong()
					&& ChildrenInfo.instance().isSchedulesGuo()) {
				Children.setSchedules(GUO);
			} else if (ChildrenInfo.instance().isSchedulesGong()
					&& !ChildrenInfo.instance().isSchedulesGuo()) {
				Children.setSchedules(GONG);
			} else {
				Children.setSchedules(NO);
			}

			if (TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesDEs()))
				Children.setSchedulesdes("");
			else
				Children.setSchedulesdes(ChildrenInfo.instance()
						.getSchedulesDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesExc()))
				Children.setSchedulesexc("");
			else
				Children.setSchedulesexc(ChildrenInfo.instance()
						.getSchedulesExc());

			if (ChildrenInfo.instance().isAttitudeGong()
					&& ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(ALL);
			} else if (!ChildrenInfo.instance().isAttitudeGong()
					&& ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(GUO);
			} else if (ChildrenInfo.instance().isAttitudeGong()
					&& !ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(GONG);
			} else {
				Children.setAttitude(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeDEs()))
				Children.setAttitudedes("");
			else
				Children.setAttitudedes(ChildrenInfo.instance()
						.getAttitudeDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeExc()))
				Children.setAttitudeexc("");
			else
				Children.setAttitudeexc(ChildrenInfo.instance()
						.getAttitudeExc());

			if (ChildrenInfo.instance().isAttitudeGong()
					&& ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(ALL);
			} else if (!ChildrenInfo.instance().isAttitudeGong()
					&& ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(GUO);
			} else if (ChildrenInfo.instance().isAttitudeGong()
					&& !ChildrenInfo.instance().isAttitudeGuo()) {
				Children.setAttitude(GONG);
			} else {
				Children.setAttitude(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeDEs()))
				Children.setAttitudedes("");
			else
				Children.setAttitudedes(ChildrenInfo.instance()
						.getAttitudeDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeExc()))
				Children.setAttitudeexc("");
			else
				Children.setAttitudeexc(ChildrenInfo.instance()
						.getAttitudeExc());

			if (ChildrenInfo.instance().isStudyGong()
					&& ChildrenInfo.instance().isStudyGuo()) {
				Children.setStudy(ALL);
			} else if (!ChildrenInfo.instance().isStudyGong()
					&& ChildrenInfo.instance().isStudyGuo()) {
				Children.setStudy(GUO);
			} else if (ChildrenInfo.instance().isStudyGong()
					&& !ChildrenInfo.instance().isStudyGuo()) {
				Children.setStudy(GONG);
			} else {
				Children.setStudy(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getStudyDEs()))
				Children.setStudydes("");
			else
				Children.setStudydes(ChildrenInfo.instance().getStudyDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getStudyExc()))
				Children.setStudyexc("");
			else
				Children.setStudyexc(ChildrenInfo.instance().getStudyExc());

			if (ChildrenInfo.instance().isLoveGong()
					&& ChildrenInfo.instance().isLoveGuo()) {
				Children.setLove(ALL);
			} else if (!ChildrenInfo.instance().isLoveGong()
					&& ChildrenInfo.instance().isLoveGuo()) {
				Children.setLove(GUO);
			} else if (ChildrenInfo.instance().isLoveGong()
					&& !ChildrenInfo.instance().isLoveGuo()) {
				Children.setLove(GONG);
			} else {
				Children.setLove(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getLoveDEs()))
				Children.setLovedes("");
			else
				Children.setLovedes(ChildrenInfo.instance().getLoveDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getLoveExc()))
				Children.setLoveexc("");
			else
				Children.setLoveexc(ChildrenInfo.instance().getLoveExc());

			if (ChildrenInfo.instance().isRespectGong()
					&& ChildrenInfo.instance().isRespectGuo()) {
				Children.setRespect(ALL);
			} else if (!ChildrenInfo.instance().isRespectGong()
					&& ChildrenInfo.instance().isRespectGuo()) {
				Children.setRespect(GUO);
			} else if (ChildrenInfo.instance().isRespectGong()
					&& !ChildrenInfo.instance().isRespectGuo()) {
				Children.setRespect(GONG);
			} else {
				Children.setRespect(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getRespectDEs()))
				Children.setRespectdes("");
			else
				Children.setRespectdes(ChildrenInfo.instance().getRespectDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getRespectExc()))
				Children.setRespectexc("");
			else
				Children.setRespectexc(ChildrenInfo.instance().getRespectExc());

			if (ChildrenInfo.instance().isActionGong()
					&& ChildrenInfo.instance().isActionGuo()) {
				Children.setAction(ALL);
			} else if (!ChildrenInfo.instance().isActionGong()
					&& ChildrenInfo.instance().isActionGuo()) {
				Children.setAction(GUO);
			} else if (ChildrenInfo.instance().isActionGong()
					&& !ChildrenInfo.instance().isActionGuo()) {
				Children.setAction(GONG);
			} else {
				Children.setAction(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getActionDEs()))
				Children.setActiondes("");
			else
				Children.setActiondes(ChildrenInfo.instance().getActionDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getActionExc()))
				Children.setActionexc("");
			else
				Children.setActionexc(ChildrenInfo.instance().getActionExc());

			if (ChildrenInfo.instance().isOtherGong()
					&& ChildrenInfo.instance().isOtherGuo()) {
				Children.setOther(ALL);
			} else if (!ChildrenInfo.instance().isOtherGong()
					&& ChildrenInfo.instance().isOtherGuo()) {
				Children.setOther(GUO);
			} else if (ChildrenInfo.instance().isOtherGong()
					&& !ChildrenInfo.instance().isOtherGuo()) {
				Children.setOther(GONG);
			} else {
				Children.setOther(NO);
			}
			if (TextUtils.isEmpty(ChildrenInfo.instance().getOtherDEs()))
				Children.setOtherdes("");
			else
				Children.setOtherdes(ChildrenInfo.instance().getOtherDEs());
			if (TextUtils.isEmpty(ChildrenInfo.instance().getOtherExc()))
				Children.setOtherexc("");
			else
				Children.setOtherexc(ChildrenInfo.instance().getOtherExc());
			Children.setGong(ChildrenInfo.instance().getCredit());
			Children.setGuo(ChildrenInfo.instance().getFault());

			HttpUtils.sumbitChildren(uid, Children, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						int res = response.optInt("result", -1);

						LogUtil.e(TAG, "搜索得到" + response.toString());
						if (res == 0) {
//							double Currency = response.optDouble("currency",
//									UserInfo.instance().getCurrency());
//							UserInfo.instance().setCurrency(Currency);
//							IoUtils.instance().saveMeritTableChildren(Children);
							ToastUtil.showMessage("提交成功！");
							if (submitwait.getVisibility() == View.VISIBLE) {
								submitwait.setVisibility(View.GONE);
							}
						} else {
							ToastUtil.showMessage(response.optString("msg"));
							if (submitwait.getVisibility() == View.VISIBLE) {
								submitwait.setVisibility(View.GONE);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
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
					if (submitwait.getVisibility() == View.VISIBLE) {
						submitwait.setVisibility(View.GONE);
					}
				}
			});

			return;
		}
	}

	public int[] SearchEvent(Context context, final ThirdAdult thirdAdult,
			String statrTime, String endTime, final TextView searchwait,final View submit) {
		instance.AUcontext = context;
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
				// LogUtil.e(TAG, "请输入起始日期和结束日期" + statrTime + endTime);
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
				// startDate=new Date(statrTime);
				// endDate=new Date(endTime);
				if (HttpUtils.isNetworkAvailable(context)) {
					HttpUtils.searchTable(uid, statrTime, endTime, false,
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									try {
										int res = response.optInt("result", -1);

										if (res == 0) {
											lists = new ArrayList<MeritTableChildren>();
											searchwait.setVisibility(View.GONE);
											submit.setVisibility(View.VISIBLE);
//											LogUtil.e(TAG, "res=" + res);
//											LogUtil.e(TAG, "response="
//													+ response.toString());
											JSONObject list = response
													.optJSONObject("list");
//											LogUtil.e(TAG,
//													"list=" + list.toString());

											Iterator it = list.keys();
											while (it.hasNext()) {
												MeritTableChildren Children = new MeritTableChildren();
												int[] buffarray=new int[MAX_NUMBER * 2 + 2];
												String key = it.next()
														.toString();
												
												JSONArray item = list
														.optJSONArray(key);
												StringBuilder sb=new StringBuilder(key);
												if(key.length()==3)
												{						
													sb.insert(0, "记录日期0");
												}else{
													sb.insert(0, "记录日期");
												}
												sb.insert(6, "月");
												sb.append("日");
												Children.setDate(sb.toString());
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
													
													if (id == 8) {
														Children.setSchedules(value);
														Children.setSchedulesdes(remark);
														Children.setSchedulesexc(desc);
													} else if (id == 9) {
														Children.setAttitude(value);
														Children.setAttitudedes(remark);
														Children.setAttitudeexc(desc);
													} else if (id == 10) {
														Children.setStudy(value);
														Children.setStudydes(remark);
														Children.setStudyexc(desc);
													} else if (id == 11) {
														Children.setLove(value);
														Children.setLovedes(remark);
														Children.setLoveexc(desc);
													} else if (id == 12) {
														Children.setRespect(value);
														Children.setRespectdes(remark);
														Children.setRespectexc(desc);
													} else if (id == 13) {
														Children.setAction(value);
														Children.setActiondes(remark);
														Children.setActionexc(desc);
													} else if (id == 14) {
														Children.setOther(value);
														Children.setOtherdes(remark);
														Children.setOtherexc(desc);
													}
													if (value == 3) {
														buffarray[(id-7)*2-2] += 1;
														buffarray[(id-7)*2-1] += 1;
													} else if (value == 2) {
														buffarray[(id-7)*2-1] += 1;
													} else if (value == 1) {
														buffarray[(id-7)*2-2] += 1;
													}
													Children.setGong(buffarray[0] + buffarray[2]
															+ buffarray[4] + buffarray[6]
																	+ buffarray[8] + buffarray[10]
																	+ buffarray[12]);
													Children.setGuo(buffarray[1] + buffarray[3]
													+ buffarray[5] + buffarray[7]
													+ buffarray[9] + buffarray[11]
													+ buffarray[13]);
												}
												lists.add(Children);
												for(int i=0;i<MAX_NUMBER*2;i++){
													array[i]+=buffarray[i];
												}
												

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
										//ToastUtil.showMessage("解析json错误");
										if (searchwait.getVisibility() == View.VISIBLE) {
											searchwait.setVisibility(View.GONE);
										}
									}
								}
							}, null);
				} else {
					ToastUtil.showMessage("网络不给力，请再给我一次机会");
					// arraybuf = Parse(uid, statrTime, endTime);
					// thirdChildren.setSearchResult(array);
					// if (searchwait.getVisibility() == View.VISIBLE) {
					// searchwait.setVisibility(View.GONE);
					// }
				}
			}
		}
		// LogUtil.e(TAG, "解析网络功过格结果" + array[14] + array[15]);
		return null;
	}

//	private int[] Parse(long uid, String statrTime, String endTime) {
//		// TODO Auto-generated method stub
//		List<MeritTableChildren> lists;
//		lists = IoUtils.instance().getMeritTableChildrenToDate(uid, statrTime,
//				endTime);
//		for (MeritTableChildren list : lists) {
//			// if (list.getSchedules()) {
//			// array[0] += 1;
//			// } else {
//			// array[1] += 1;
//			// }
//			// if (list.getAttitude()) {
//			// array[2] += 1;
//			// } else {
//			// array[3] += 1;
//			// }
//			// if (list.getStudy()) {
//			// array[4] += 1;
//			// } else {
//			// array[5] += 1;
//			// }
//			// if (list.getLove()) {
//			// array[6] += 1;
//			// } else {
//			// array[7] += 1;
//			// }
//			// if (list.getRespect()) {
//			// array[8] += 1;
//			// } else {
//			// array[9] += 1;
//			// }
//			// if (list.getAction()) {
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
