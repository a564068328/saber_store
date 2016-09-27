package com.icloud.listenbook.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.ShowTextDlg;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

@SuppressLint("ValidFragment")
public class lastFrag extends BaseFragement implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	private final int NO = 0;
	private final int GONG = NO + 1;
	private final int GUO = GONG + 1;
	private final int ALL = GUO + 1;
	String TAG = getClass().getName();
	MeritTableAdult adult;
	final int drawable_on = R.drawable.checkbox_pressed;
	final int drawable_off = R.drawable.checkbox_normal;
	final int drawable_guo = R.drawable.checkbox_dafult;
	private static int Credit = 0;
	private static int fault = 0;
	private static final int MAX_NUMBER = 7;
	int eTextid;
	ShowTextDlg showTextDlg;
	TextView eText;
	TextView et1;
	TextView et2;
	TextView et3;
	TextView et4;
	TextView et5;
	TextView et6;
	TextView et7;
	TextView et8;
	TextView et9;
	TextView et10;
	TextView et11;
	TextView et12;
	TextView et13;
	TextView et14;
	View iv1;
	View iv2;
	View iv3;
	View iv4;
	View iv5;
	View iv6;
	View iv7;
	View iv8;
	View iv9;
	View iv10;
	View iv11;
	View iv12;
	View iv13;
	View iv14;
	View tv_total1;
	View tv_total2;
	Activity act;
	TextView tv_title1;
	TextView tv_title2;
	TextView tv_title3;
	TextView tv_title4;
	TextView tv_title5;
	TextView tv_title6;
	TextView tv_title7;
	LinearLayout ll_progress;
	boolean isAdult;
	boolean isYesterday;
	MeritTableChildren children;
	String time;

	public lastFrag(Activity act, boolean isAdult, boolean isYesterday) {
		this.act = act;
		this.isAdult = isAdult;
		this.isYesterday = isYesterday;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		adult = new MeritTableAdult();
		children = new MeritTableChildren();
	}

	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.fra_history;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		et1 = (TextView) findViewById(R.id.et1);
		et2 = (TextView) findViewById(R.id.et2);
		et3 = (TextView) findViewById(R.id.et3);
		et4 = (TextView) findViewById(R.id.et4);
		et5 = (TextView) findViewById(R.id.et5);
		et6 = (TextView) findViewById(R.id.et6);
		et7 = (TextView) findViewById(R.id.et7);
		et8 = (TextView) findViewById(R.id.et8);
		et9 = (TextView) findViewById(R.id.et9);
		et10 = (TextView) findViewById(R.id.et10);
		et11 = (TextView) findViewById(R.id.et11);
		et12 = (TextView) findViewById(R.id.et12);
		et13 = (TextView) findViewById(R.id.et13);
		et14 = (TextView) findViewById(R.id.et14);
		tv_total1 = findViewById(R.id.tv_total1);
		tv_total2 = findViewById(R.id.tv_total2);
		iv1 = findViewById(R.id.im1);
		iv2 = findViewById(R.id.im2);
		iv3 = findViewById(R.id.im3);
		iv4 = findViewById(R.id.im4);
		iv5 = findViewById(R.id.im5);
		iv6 = findViewById(R.id.im6);
		iv7 = findViewById(R.id.im7);
		iv8 = findViewById(R.id.im8);
		iv9 = findViewById(R.id.im9);
		iv10 = findViewById(R.id.im10);
		iv11 = findViewById(R.id.im11);
		iv12 = findViewById(R.id.im12);
		iv13 = findViewById(R.id.im13);
		iv14 = findViewById(R.id.im14);
		tv_title1 = (TextView) findViewById(R.id.tv_title1);
		tv_title2 = (TextView) findViewById(R.id.tv_title2);
		tv_title3 = (TextView) findViewById(R.id.tv_title3);
		tv_title4 = (TextView) findViewById(R.id.tv_title4);
		tv_title5 = (TextView) findViewById(R.id.tv_title5);
		tv_title6 = (TextView) findViewById(R.id.tv_title6);
		tv_title7 = (TextView) findViewById(R.id.tv_title7);
		ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
		ll_progress.setVisibility(View.VISIBLE);
	}

	@Override
	public void setData() {
		// TODO Auto-generated method stub
		super.setData();
		if (!isAdult) {
			tv_title1.setText("作息规律");
			tv_title2.setText("对人态度");
			tv_title3.setText("学习专注");
			tv_title4.setText("爱心善意");
			tv_title5.setText("尊师重教");
			tv_title6.setText("思考行动");
			tv_title7.setText("其  它");
		}
		if (isYesterday) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			time = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());

		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -2);
			time = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
		}
		HttpUtils.searchTable(UserInfo.instance().getUid(), time, time,
				isAdult, this, this);
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		et1.setOnClickListener(this);
		et2.setOnClickListener(this);
		et3.setOnClickListener(this);
		et4.setOnClickListener(this);
		et5.setOnClickListener(this);
		et6.setOnClickListener(this);
		et7.setOnClickListener(this);
		et8.setOnClickListener(this);
		et9.setOnClickListener(this);
		et10.setOnClickListener(this);
		et11.setOnClickListener(this);
		et12.setOnClickListener(this);
		et13.setOnClickListener(this);
		et14.setOnClickListener(this);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);

			if (res == 0) {
				ll_progress.setVisibility(View.GONE);
//				LogUtil.e(TAG, "res=" + res);
//				LogUtil.e(TAG, "response=" + response.toString());
				JSONObject list = response.optJSONObject("list");
				if(list==null){
					ToastUtil.showMessage("该日无记录哦");
					return;
				}
				LogUtil.e(TAG, "list(lastfrag)=" + list.toString());
//				LogUtil.e(TAG, "到这里没错");

				Iterator it = list.keys();
				while (it.hasNext()) {
					if (isAdult) {
						int[] buffarray = new int[MAX_NUMBER * 2 + 2];
						String key = it.next().toString();
						adult.setDate(key);
						JSONArray item = list.optJSONArray(key);
						for (int i = 0; i < item.length(); i++) {
							JSONObject chipJsonObj = item.optJSONObject(i);
							int id = chipJsonObj.optInt("id");
							int value = chipJsonObj.optInt("signValue");
							String remark = chipJsonObj.optString("remark");
							String desc = chipJsonObj.optString("desc");
							if (id == 1) {
								adult.setIdea(value);
								adult.setIdeades(remark);
								adult.setIdeaexc(desc);
							} else if (id == 2) {
								adult.setAttitude(value);
								adult.setAttitudedes(remark);
								adult.setAttitudeexc(desc);
							} else if (id == 3) {
								adult.setAction(value);
								adult.setActiondes(remark);
								adult.setActionexc(desc);
							} else if (id == 4) {
								adult.setTreat(value);
								adult.setTreatdes(remark);
								adult.setTreatexc(desc);
							} else if (id == 5) {
								adult.setWork(value);
								adult.setWorkdes(remark);
								adult.setWorkexc(desc);
							} else if (id == 6) {
								adult.setBelief(value);
								adult.setBeliefdes(remark);
								adult.setBeliefexc(desc);
							} else if (id == 7) {
								adult.setOther(value);
								adult.setOtherdes(remark);
								adult.setOtherexc(desc);
							}
							if (value == 3) {
								buffarray[id * 2 - 2] += 1;
								buffarray[id * 2 - 1] += 1;
								// LogUtil.e(TAG,
								// "buffarray"+buffarray[id*2-1]);
							} else if (value == 2) {
								buffarray[id * 2 - 1] += 1;
							} else if (value == 1) {
								buffarray[id * 2 - 2] += 1;
							}
							adult.setGong(buffarray[0] + buffarray[2]
									+ buffarray[4] + buffarray[6]
									+ buffarray[8] + buffarray[10]
									+ buffarray[12]);
							adult.setGuo(buffarray[1] + buffarray[3]
									+ buffarray[5] + buffarray[7]
									+ buffarray[9] + buffarray[11]
									+ buffarray[13]);
							ParseAdult();
						}
					} else {
						int[] buffarray = new int[MAX_NUMBER * 2 + 2];
						String key = it.next().toString();
						children.setDate(key);
						JSONArray item = list.optJSONArray(key);
						for (int i = 0; i < item.length(); i++) {
							JSONObject chipJsonObj = item.optJSONObject(i);
							int id = chipJsonObj.optInt("id");
							int value = chipJsonObj.optInt("signValue");
							String remark = chipJsonObj.optString("remark");
							String desc = chipJsonObj.optString("desc");
							if (id == 8) {
								children.setSchedules(value);
								children.setSchedulesdes(remark);
								children.setSchedulesexc(desc);
							} else if (id == 9) {
								children.setAttitude(value);
								children.setAttitudedes(remark);
								children.setAttitudeexc(desc);
							} else if (id == 10) {
								children.setStudy(value);
								children.setStudydes(remark);
								children.setStudyexc(desc);
							} else if (id == 11) {
								children.setLove(value);
								children.setLovedes(remark);
								children.setLoveexc(desc);
							} else if (id == 12) {
								children.setRespect(value);
								children.setRespectdes(remark);
								children.setRespectexc(desc);
							} else if (id == 13) {
								children.setAction(value);
								children.setActiondes(remark);
								children.setActionexc(desc);
							} else if (id == 14) {
								children.setOther(value);
								children.setOtherdes(remark);
								children.setOtherexc(desc);
							}
							if (value == 3) {
								buffarray[(id-7)*2 - 2] += 1;
								buffarray[(id-7)*2- 1] += 1;
							} else if (value == 2) {
								buffarray[(id-7)*2 - 1] += 1;
							} else if (value == 1) {
								buffarray[(id-7)*2 - 2] += 1;
							}
							children.setGong(buffarray[0] + buffarray[2]
									+ buffarray[4] + buffarray[6]
									+ buffarray[8] + buffarray[10]
									+ buffarray[12]);
							children.setGuo(buffarray[1] + buffarray[3]
									+ buffarray[5] + buffarray[7]
									+ buffarray[9] + buffarray[11]
									+ buffarray[13]);
							ParseChildren();
						}
					}
				}
			} else {
				ll_progress.setVisibility(View.GONE);
				ToastUtil.showMessage(response.optString("msg"));
			}

		} catch (Exception e) {
			// if (searchwait.getVisibility() == View.VISIBLE) {
			// searchwait.setVisibility(View.GONE);
			// }
			ll_progress.setVisibility(View.GONE);
			e.printStackTrace();
			// LogUtil.e(TAG, "res=" + "json解析错误");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.et1:
			edit(et1, 1);
			break;
		case R.id.et2:
			edit(et2, 2);
			break;
		case R.id.et3:
			edit(et3, 3);
			break;
		case R.id.et4:
			edit(et4, 4);
			break;
		case R.id.et5:
			edit(et5, 5);
			break;
		case R.id.et6:
			edit(et6, 6);
			break;
		case R.id.et7:
			edit(et7, 7);
			break;
		case R.id.et8:
			edit(et8, 8);
			break;
		case R.id.et9:
			edit(et9, 9);
			break;
		case R.id.et10:
			edit(et10, 10);
			break;
		case R.id.et11:
			edit(et11, 11);
			break;
		case R.id.et12:
			edit(et12, 12);
			break;
		case R.id.et13:
			edit(et13, 13);
			break;
		case R.id.et14:
			edit(et14, 14);
			break;
		}
	}

	protected void edit(View v, int i) {
		eText = (TextView) v;
		eTextid = i;
		if (showTextDlg == null) {
			showTextDlg = DialogManage.showTextDlg((Context) act, "查看内容",
					R.string.confirm,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (showTextDlg != null) {
								String inputStr = showTextDlg.getInput();
								if (!TextUtils.isEmpty(inputStr)) {
									try {
										// eText.setText(inputStr);
										// eTextsave(inputStr, eTextid);
										showTextDlg.dismiss();
									} catch (Exception e) {
									}
								}
							}
						}
					});
			// inputDlg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			// inputDlg.mutilShow();
			showTextDlg.show();
		} else

		if (isAdult) {
			switch (eTextid) {
			case 1:
				if (!TextUtils.isEmpty(adult.getIdeades())) {
					showTextDlg.setBody(adult.getIdeades());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 2:
				if (!TextUtils.isEmpty(adult.getIdeaexc())) {
					showTextDlg.setBody(adult.getIdeaexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 3:
				if (!TextUtils.isEmpty(adult.getAttitudedes())) {
					showTextDlg.setBody(adult.getAttitudedes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 4:
				if (!TextUtils.isEmpty(adult.getAttitudeexc())) {
					showTextDlg.setBody(adult.getAttitudeexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 5:
				if (!TextUtils.isEmpty(adult.getActiondes())) {
					showTextDlg.setBody(adult.getActiondes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 6:
				if (!TextUtils.isEmpty(adult.getActionexc())) {
					showTextDlg.setBody(adult.getActionexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 7:
				if (!TextUtils.isEmpty(adult.getTreatdes())) {
					showTextDlg.setBody(adult.getTreatdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 8:
				if (!TextUtils.isEmpty(adult.getTreatexc())) {
					showTextDlg.setBody(adult.getTreatexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 9:
				if (!TextUtils.isEmpty(adult.getWorkdes())) {
					showTextDlg.setBody(adult.getWorkdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 10:
				if (!TextUtils.isEmpty(adult.getWorkexc())) {
					showTextDlg.setBody(adult.getWorkexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 11:
				if (!TextUtils.isEmpty(adult.getBeliefdes())) {
					showTextDlg.setBody(adult.getBeliefdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 12:
				if (!TextUtils.isEmpty(adult.getBeliefexc())) {
					showTextDlg.setBody(adult.getBeliefexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 13:
				if (!TextUtils.isEmpty(adult.getOtherdes())) {
					showTextDlg.setBody(adult.getOtherdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 14:
				if (!TextUtils.isEmpty(adult.getOtherexc())) {
					showTextDlg.setBody(adult.getOtherexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			default:
				break;
			}
		} else {
			switch (eTextid) {
			case 1:
				if (!TextUtils.isEmpty(children.getSchedulesdes())) {
					showTextDlg.setBody(children.getSchedulesdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 2:
				if (!TextUtils.isEmpty(children.getSchedulesexc())) {
					showTextDlg.setBody(children.getSchedulesexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 3:
				if (!TextUtils.isEmpty(children.getAttitudedes())) {
					showTextDlg.setBody(children.getAttitudedes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 4:
				if (!TextUtils.isEmpty(children.getAttitudeexc())) {
					showTextDlg.setBody(children.getAttitudeexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 5:
				if (!TextUtils.isEmpty(children.getStudydes())) {
					showTextDlg.setBody(children.getStudydes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 6:
				if (!TextUtils.isEmpty(children.getStudyexc())) {
					showTextDlg.setBody(children.getStudyexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 7:
				if (!TextUtils.isEmpty(children.getLovedes())) {
					showTextDlg.setBody(children.getLovedes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 8:
				if (!TextUtils.isEmpty(children.getLoveexc())) {
					showTextDlg.setBody(children.getLoveexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 9:
				if (!TextUtils.isEmpty(children.getRespectdes())) {
					showTextDlg.setBody(children.getRespectdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 10:
				if (!TextUtils.isEmpty(children.getRespectexc())) {
					showTextDlg.setBody(children.getRespectexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 11:
				if (!TextUtils.isEmpty(children.getActiondes())) {
					showTextDlg.setBody(children.getActiondes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 12:
				if (!TextUtils.isEmpty(children.getActionexc())) {
					showTextDlg.setBody(children.getActionexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 13:
				if (!TextUtils.isEmpty(children.getOtherdes())) {
					showTextDlg.setBody(children.getOtherdes());
				} else {
					showTextDlg.setBody("");
				}
				break;
			case 14:
				if (!TextUtils.isEmpty(children.getOtherexc())) {
					showTextDlg.setBody(children.getOtherexc());
				} else {
					showTextDlg.setBody("");
				}
				break;
			default:
				break;
			}
		}
		showTextDlg.show();
	}

	protected void ParseAdult() {
		// lists = new ArrayList<ResultItemInfo>();
		// ResultItemInfo infodemo=new ResultItemInfo(true,true,"起心动念","无","无");
		// lists.add(infodemo);
		// return lists;
		if (adult == null)
			return;

		int i = adult.getIdea();
		if (i == ALL || i == GONG)
			iv1.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv2.setBackgroundResource(drawable_guo);
		tv_title1.setText("起心动念");
		et1.setText(adult.getIdeades());
		et2.setText(adult.getIdeaexc());

		i = adult.getAttitude();
		if (i == ALL || i == GONG)
			iv3.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv4.setBackgroundResource(drawable_guo);
		tv_title2.setText("言语态度");
		et3.setText(adult.getAttitudedes());
		et4.setText(adult.getAttitudeexc());

		i = adult.getAction();
		if (i == ALL || i == GONG)
			iv5.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv6.setBackgroundResource(drawable_guo);
		tv_title3.setText("行为处事");
		et5.setText(adult.getActiondes());
		et6.setText(adult.getActionexc());

		i = adult.getTreat();
		if (i == ALL || i == GONG)
			iv7.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv8.setBackgroundResource(drawable_guo);
		tv_title4.setText("待人接物");
		et7.setText(adult.getTreatdes());
		et8.setText(adult.getTreatexc());

		i = adult.getWork();
		if (i == ALL || i == GONG)
			iv9.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv10.setBackgroundResource(drawable_guo);
		tv_title5.setText("工作事业");
		et9.setText(adult.getWorkdes());
		et10.setText(adult.getWorkexc());

		i = adult.getBelief();
		if (i == ALL || i == GONG)
			iv11.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv12.setBackgroundResource(drawable_guo);
		tv_title6.setText("信仰修为");
		et11.setText(adult.getBeliefdes());
		et12.setText(adult.getBeliefexc());

		i = adult.getOther();
		if (i == ALL || i == GONG)
			iv13.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv14.setBackgroundResource(drawable_guo);
		tv_title6.setText("其  它");
		et13.setText(adult.getOtherdes());
		et14.setText(adult.getOtherexc());
		((TextView)tv_total1).setText(String.valueOf(adult.getGong()));
		((TextView)tv_total2).setText(String.valueOf(adult.getGuo()));
	}

	protected void ParseChildren() {
		// TODO Auto-generated method stub
		if (children == null)
			return;

		int i = children.getSchedules();
		if (i == ALL || i == GONG)
			iv1.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv2.setBackgroundResource(drawable_guo);
		tv_title1.setText("作息规律");
		et1.setText(children.getSchedulesdes());
		et2.setText(children.getSchedulesexc());

		i = children.getAttitude();
		if (i == ALL || i == GONG)
			iv3.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv4.setBackgroundResource(drawable_guo);
		tv_title2.setText("对人态度");
		et3.setText(children.getAttitudedes());
		et4.setText(children.getAttitudeexc());

		i = children.getStudy();
		if (i == ALL || i == GONG)
			iv5.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv6.setBackgroundResource(drawable_guo);
		tv_title3.setText("学习专注");
		et5.setText(children.getStudydes());
		et6.setText(children.getStudyexc());

		i = children.getLove();
		if (i == ALL || i == GONG)
			iv7.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv8.setBackgroundResource(drawable_guo);
		tv_title4.setText("爱心善意");
		et7.setText(children.getLovedes());
		et8.setText(children.getLoveexc());

		i = children.getRespect();
		if (i == ALL || i == GONG)
			iv9.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv10.setBackgroundResource(drawable_guo);
		tv_title5.setText("尊师重教");
		et9.setText(children.getRespectdes());
		et10.setText(children.getRespectexc());

		i = children.getAction();
		if (i == ALL || i == GONG)
			iv11.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv12.setBackgroundResource(drawable_guo);
		tv_title6.setText("思考行动");
		et11.setText(children.getActiondes());
		et12.setText(children.getActionexc());

		i = children.getOther();
		if (i == ALL || i == GONG)
			iv13.setBackgroundResource(drawable_on);
		if (i == ALL || i == GUO)
			iv14.setBackgroundResource(drawable_guo);
		tv_title7.setText("其  它");
		et13.setText(children.getOtherdes());
		et14.setText(children.getOtherexc());
		((TextView)tv_total1).setText(String.valueOf(children.getGong()));
		((TextView)tv_total2).setText(String.valueOf(children.getGuo()));
	}
}
