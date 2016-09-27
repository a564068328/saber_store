package com.icloud.listenbook.ui.fragment;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.dialog.AlertDlg;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.EditDlg;
import com.icloud.listenbook.entity.AdultInfo;
import com.icloud.listenbook.entity.ChildrenInfo;
import com.icloud.listenbook.ui.chipAct.ThirdAdult;
import com.icloud.listenbook.unit.AdultUtils;
import com.icloud.listenbook.unit.ChildrenUtils;
import com.icloud.wrzjh.base.utils.LogUtil;

@SuppressLint("ValidFragment")
public class contentFrag extends BaseFragement implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	String TAG = getClass().getName();
	final int drawable_on = R.drawable.checkbox_pressed;
	final int drawable_off = R.drawable.checkbox_normal;
	final int drawable_guo = R.drawable.checkbox_dafult;
	private int Credit = 0;
	private int fault = 0;
	private static final int MAX_NUMBER = 7;
	RelativeLayout btn1;
	RelativeLayout btn2;
	RelativeLayout btn3;
	RelativeLayout btn4;
	RelativeLayout btn5;
	RelativeLayout btn6;
	RelativeLayout btn7;
	RelativeLayout btn8;
	RelativeLayout btn9;
	RelativeLayout btn10;
	RelativeLayout btn11;
	RelativeLayout btn12;
	RelativeLayout btn13;
	RelativeLayout btn14;
	TextView eText;
	View et1;
	View et2;
	View et3;
	View et4;
	View et5;
	View et6;
	View et7;
	View et8;
	View et9;
	View et10;
	View et11;
	View et12;
	View et13;
	View et14;
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
	TextView tv_title1;
	TextView tv_title2;
	TextView tv_title3;
	TextView tv_title4;
	TextView tv_title5;
	TextView tv_title6;
	TextView tv_title7;
	AlertDlg sumbitDlg;
	View submit;
	TextView submit_wait;
	EditDlg inputDlg;
	int eTextid;
	Activity act;
	boolean isAdult;
	RecyclerView recyclerview;

	public contentFrag(Activity act, boolean isAdult) {
		this.act = act;
		this.isAdult = isAdult;
		// this.recyclerview=recyclerview;
	}

	@Override
	public int getLayout() {
		// if(isAdult)
		return R.layout.fra_content;
		// else

	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		btn1 = (RelativeLayout) findViewById(R.id.btn1);
		btn2 = (RelativeLayout) findViewById(R.id.btn2);
		btn3 = (RelativeLayout) findViewById(R.id.btn3);
		btn4 = (RelativeLayout) findViewById(R.id.btn4);
		btn5 = (RelativeLayout) findViewById(R.id.btn5);
		btn6 = (RelativeLayout) findViewById(R.id.btn6);
		btn7 = (RelativeLayout) findViewById(R.id.btn7);
		btn8 = (RelativeLayout) findViewById(R.id.btn8);
		btn9 = (RelativeLayout) findViewById(R.id.btn9);
		btn10 = (RelativeLayout) findViewById(R.id.btn10);
		btn11 = (RelativeLayout) findViewById(R.id.btn11);
		btn12 = (RelativeLayout) findViewById(R.id.btn12);
		btn13 = (RelativeLayout) findViewById(R.id.btn13);
		btn14 = (RelativeLayout) findViewById(R.id.btn14);

		et1 = findViewById(R.id.et1);
		et2 = findViewById(R.id.et2);
		et3 = findViewById(R.id.et3);
		et4 = findViewById(R.id.et4);
		et5 = findViewById(R.id.et5);
		et6 = findViewById(R.id.et6);
		et7 = findViewById(R.id.et7);
		et8 = findViewById(R.id.et8);
		et9 = findViewById(R.id.et9);
		et10 = findViewById(R.id.et10);
		et11 = findViewById(R.id.et11);
		et12 = findViewById(R.id.et12);
		et13 = findViewById(R.id.et13);
		et14 = findViewById(R.id.et14);
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
		submit = findViewById(R.id.submit);
		submit_wait = (TextView) findViewById(R.id.submit_wait);
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btn10.setOnClickListener(this);
		btn11.setOnClickListener(this);
		btn12.setOnClickListener(this);
		btn13.setOnClickListener(this);
		btn14.setOnClickListener(this);

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
		submit.setOnClickListener(this);
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
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(JSONObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn1:
			// Bitmap d=BitmapFactory.decodeResource(this.getResources(),
			// R.drawable.btn_check_on);
			if (isAdult) {
				if (AdultInfo.instance().isIdeaGong()) {
					AdultInfo.instance().setIdeaGong(false);
					iv1.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setIdeaGong(true);
					iv1.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isSchedulesGong()) {
					ChildrenInfo.instance().setSchedulesGong(false);
					iv1.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setSchedulesGong(true);
					iv1.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn2:
			if (isAdult) {
				if (AdultInfo.instance().isIdeaGuo()) {
					AdultInfo.instance().setIdeaGuo(false);
					iv2.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setIdeaGuo(true);
					iv2.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isSchedulesGuo()) {
					ChildrenInfo.instance().setSchedulesGuo(false);
					iv2.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setSchedulesGuo(true);
					iv2.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn3:
			if (isAdult) {
				if (AdultInfo.instance().isAttitudeGong()) {
					AdultInfo.instance().setAttitudeGong(false);
					iv3.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setAttitudeGong(true);
					iv3.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isAttitudeGong()) {
					ChildrenInfo.instance().setAttitudeGong(false);
					iv3.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setAttitudeGong(true);
					iv3.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn4:
			if (isAdult) {
				if (AdultInfo.instance().isAttitudeGuo()) {
					AdultInfo.instance().setAttitudeGuo(false);
					iv4.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setAttitudeGuo(true);
					iv4.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isAttitudeGuo()) {
					ChildrenInfo.instance().setAttitudeGuo(false);
					iv4.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setAttitudeGuo(true);
					iv4.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn5:
			if (isAdult) {
				if (AdultInfo.instance().isActionGong()) {
					AdultInfo.instance().setActionGong(false);
					iv5.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setActionGong(true);
					iv5.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isStudyGong()) {
					ChildrenInfo.instance().setStudyGong(false);
					iv5.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setStudyGong(true);
					iv5.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn6:
			if (isAdult) {
				if (AdultInfo.instance().isActionGuo()) {
					AdultInfo.instance().setActionGuo(false);
					iv6.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setActionGuo(true);
					iv6.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isStudyGuo()) {
					ChildrenInfo.instance().setStudyGuo(false);
					iv6.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setStudyGuo(true);
					iv6.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn7:
			if (isAdult) {
				if (AdultInfo.instance().isTreatGong()) {
					AdultInfo.instance().setTreatGong(false);
					iv7.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setTreatGong(true);
					iv7.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isLoveGong()) {
					ChildrenInfo.instance().setLoveGong(false);
					iv7.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setLoveGong(true);
					iv7.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn8:
			if (isAdult) {
				if (AdultInfo.instance().isTreatGuo()) {
					AdultInfo.instance().setTreatGuo(false);
					iv8.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setTreatGuo(true);
					iv8.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isLoveGuo()) {
					ChildrenInfo.instance().setLoveGuo(false);
					iv8.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setLoveGuo(true);
					iv8.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn9:
			if (isAdult) {
				if (AdultInfo.instance().isWorkGong()) {
					AdultInfo.instance().setWorkGong(false);
					iv9.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setWorkGong(true);
					iv9.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isRespectGong()) {
					ChildrenInfo.instance().setRespectGong(false);
					iv9.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setRespectGong(true);
					iv9.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn10:
			if (isAdult) {
				if (AdultInfo.instance().isWorkGuo()) {
					AdultInfo.instance().setWorkGuo(false);
					iv10.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setWorkGuo(true);
					iv10.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isRespectGuo()) {
					ChildrenInfo.instance().setRespectGuo(false);
					iv10.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setRespectGuo(true);
					iv10.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn11:
			if (isAdult) {
				if (AdultInfo.instance().isBeliefGong()) {
					AdultInfo.instance().setBeliefGong(false);
					iv11.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setBeliefGong(true);
					iv11.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isActionGong()) {
					ChildrenInfo.instance().setActionGong(false);
					iv11.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setActionGong(true);
					iv11.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}
			break;
		case R.id.btn12:
			if (isAdult) {
				if (AdultInfo.instance().isBeliefGuo()) {
					AdultInfo.instance().setBeliefGuo(false);
					iv12.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setBeliefGuo(true);
					iv12.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isActionGuo()) {
					ChildrenInfo.instance().setActionGuo(false);
					iv12.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setActionGuo(true);
					iv12.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;
		case R.id.btn13:
			if (isAdult) {
				if (AdultInfo.instance().isOtherGong()) {
					AdultInfo.instance().setOtherGong(false);
					iv13.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					AdultInfo.instance().setOtherGong(true);
					iv13.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			} else {
				if (ChildrenInfo.instance().isOtherGong()) {
					ChildrenInfo.instance().setOtherGong(false);
					iv13.setBackgroundResource(drawable_off);
					Credit--;
					Credit();
				} else {
					ChildrenInfo.instance().setOtherGong(true);
					iv13.setBackgroundResource(drawable_on);
					Credit++;
					Credit();
				}
			}

			break;
		case R.id.btn14:
			if (isAdult) {
				if (AdultInfo.instance().isOtherGuo()) {
					AdultInfo.instance().setOtherGuo(false);
					iv14.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					AdultInfo.instance().setOtherGuo(true);
					iv14.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			} else {
				if (ChildrenInfo.instance().isOtherGuo()) {
					ChildrenInfo.instance().setOtherGuo(false);
					iv14.setBackgroundResource(drawable_off);
					fault--;
					fault();
				} else {
					ChildrenInfo.instance().setOtherGuo(true);
					iv14.setBackgroundResource(drawable_guo);
					fault++;
					fault();
				}
			}
			break;

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
		case R.id.submit:

			if (sumbitDlg == null) {
				sumbitDlg = DialogManage.showAlertDlg((Context) act, "提示",
						"功过表(成人版)每日只能提交一次，提交后奖励20金币，是否提交？",
						R.string.sumbit,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								LogUtil.e(TAG, "提交结果了\n");
								if (isAdult) {
									AdultUtils.instance().submitEvent(
											(Context) act, submit_wait);
								}else{
									//sumbitDlg.setMsg("功过表(孩子版)每日只能提交一次，是否提交？");
									ChildrenUtils.instance().submitEvent(
											(Context) act, submit_wait);
								}
								sumbitDlg.dismiss();
							}
						}, R.string.cancel);
				if (isAdult){
					sumbitDlg.setMsg("功过表(成人版)每日只能提交一次，提交后奖励20金币，是否提交？");
				}else{
					sumbitDlg.setMsg("功过表(孩子版)每日只能提交一次，是否提交？");
				}
			} else
				if (isAdult){
					sumbitDlg.setMsg("功过表(成人版)每日只能提交一次，提交后奖励20金币，是否提交？");
				}else{
					sumbitDlg.setMsg("功过表(孩子版)每日只能提交一次，是否提交？");
				}
				sumbitDlg.show();

			break;
		}
	}

	protected void edit(View v, int i) {
		eText = (TextView) v;
		eTextid = i;
		if (inputDlg == null) {
			inputDlg = DialogManage.showEditInput((Context) act,
					R.string.edittitle, R.string.confirm,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (inputDlg != null) {
								String inputStr = inputDlg.getInput();
								if (!TextUtils.isEmpty(inputStr)) {
									try {
										eText.setText(inputStr);
										eTextsave(inputStr, eTextid);
										inputDlg.dismiss();
									} catch (Exception e) {
									}
								}
							}
						}
					}, R.string.cancel);
			inputDlg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			inputDlg.mutilShow();
			inputDlg.show();
		} else if (isAdult) {
			switch (eTextid) {
			case 1:
				if (!TextUtils.isEmpty(AdultInfo.instance().getIdeaDEs())) {
					inputDlg.setBody(AdultInfo.instance().getIdeaDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 2:
				if (!TextUtils.isEmpty(AdultInfo.instance().getIdeaExc())) {
					inputDlg.setBody(AdultInfo.instance().getIdeaExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 3:
				if (!TextUtils.isEmpty(AdultInfo.instance().getAttitudeDEs())) {
					inputDlg.setBody(AdultInfo.instance().getAttitudeDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 4:
				if (!TextUtils.isEmpty(AdultInfo.instance().getAttitudeExc())) {
					inputDlg.setBody(AdultInfo.instance().getAttitudeExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 5:
				if (!TextUtils.isEmpty(AdultInfo.instance().getActionDEs())) {
					inputDlg.setBody(AdultInfo.instance().getActionDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 6:
				if (!TextUtils.isEmpty(AdultInfo.instance().getActionExc())) {
					inputDlg.setBody(AdultInfo.instance().getActionExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 7:
				if (!TextUtils.isEmpty(AdultInfo.instance().getTreatDEs())) {
					inputDlg.setBody(AdultInfo.instance().getTreatDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 8:
				if (!TextUtils.isEmpty(AdultInfo.instance().getTreatExc())) {
					inputDlg.setBody(AdultInfo.instance().getTreatExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 9:
				if (!TextUtils.isEmpty(AdultInfo.instance().getWorkDEs())) {
					inputDlg.setBody(AdultInfo.instance().getWorkDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 10:
				if (!TextUtils.isEmpty(AdultInfo.instance().getWorkExc())) {
					inputDlg.setBody(AdultInfo.instance().getWorkExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 11:
				if (!TextUtils.isEmpty(AdultInfo.instance().getBeliefDEs())) {
					inputDlg.setBody(AdultInfo.instance().getBeliefDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 12:
				if (!TextUtils.isEmpty(AdultInfo.instance().getBeliefExc())) {
					inputDlg.setBody(AdultInfo.instance().getBeliefExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 13:
				if (!TextUtils.isEmpty(AdultInfo.instance().getOtherDEs())) {
					inputDlg.setBody(AdultInfo.instance().getOtherDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 14:
				if (!TextUtils.isEmpty(AdultInfo.instance().getOtherExc())) {
					inputDlg.setBody(AdultInfo.instance().getOtherExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			default:
				break;
			}
		} else {
			switch (eTextid) {
			case 1:
				if (!TextUtils.isEmpty(ChildrenInfo.instance()
						.getSchedulesDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getSchedulesDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 2:
				if (!TextUtils.isEmpty(ChildrenInfo.instance()
						.getSchedulesExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getSchedulesExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 3:
				if (!TextUtils
						.isEmpty(ChildrenInfo.instance().getAttitudeDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getAttitudeDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 4:
				if (!TextUtils
						.isEmpty(ChildrenInfo.instance().getAttitudeExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getAttitudeExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 5:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getStudyDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 6:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getStudyExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 7:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getLoveDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 8:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getLoveExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 9:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getRespectDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 10:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getRespectExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 11:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getActionDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 12:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getActionExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 13:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherDEs())) {
					inputDlg.setBody(ChildrenInfo.instance().getOtherDEs());
				} else {
					inputDlg.setBody("");
				}
				break;
			case 14:
				if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherExc())) {
					inputDlg.setBody(ChildrenInfo.instance().getOtherExc());
				} else {
					inputDlg.setBody("");
				}
				break;
			default:
				break;
			}
		}
		inputDlg.show();

	}

	@Override
	public void onResume() {
		super.onStart();
		Credit = 0;
		fault = 0;
		((TextView) tv_total1).setText(String.valueOf(Credit));
		((TextView) tv_total2).setText(String.valueOf(fault));
		iv1.setBackgroundResource(drawable_off);
		iv2.setBackgroundResource(drawable_off);
		iv3.setBackgroundResource(drawable_off);
		iv4.setBackgroundResource(drawable_off);
		iv5.setBackgroundResource(drawable_off);
		iv6.setBackgroundResource(drawable_off);
		iv7.setBackgroundResource(drawable_off);
		iv8.setBackgroundResource(drawable_off);
		iv9.setBackgroundResource(drawable_off);
		iv10.setBackgroundResource(drawable_off);
		iv11.setBackgroundResource(drawable_off);
		iv12.setBackgroundResource(drawable_off);
		iv13.setBackgroundResource(drawable_off);
		iv14.setBackgroundResource(drawable_off);
		if (isAdult) {
			AdultInfo.instance().Clear();
			
			if (!TextUtils.isEmpty(AdultInfo.instance().getIdeaDEs())) {
				((TextView) et1).setText(AdultInfo.instance().getIdeaDEs());
			} else {
				((TextView) et1).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getIdeaExc())) {
				((TextView) et2).setText(AdultInfo.instance().getIdeaExc());
			} else {
				((TextView) et2).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getAttitudeDEs())) {
				((TextView) et3).setText(AdultInfo.instance().getAttitudeDEs());
			} else {
				((TextView) et3).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getAttitudeExc())) {
				((TextView) et4).setText(AdultInfo.instance().getAttitudeExc());
			} else {
				((TextView) et4).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getActionDEs())) {
				((TextView) et5).setText(AdultInfo.instance().getActionDEs());
			} else {
				((TextView) et5).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getActionExc())) {
				((TextView) et6).setText(AdultInfo.instance().getActionExc());
			} else {
				((TextView) et6).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getTreatDEs())) {
				((TextView) et7).setText(AdultInfo.instance().getTreatDEs());
			} else {
				((TextView) et7).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getTreatExc())) {
				((TextView) et8).setText(AdultInfo.instance().getTreatExc());
			} else {
				((TextView) et8).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getWorkDEs())) {
				((TextView) et9).setText(AdultInfo.instance().getWorkDEs());
			} else {
				((TextView) et9).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getWorkExc())) {
				((TextView) et10).setText(AdultInfo.instance().getWorkExc());
			} else {
				((TextView) et10).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getBeliefDEs())) {
				((TextView) et11).setText(AdultInfo.instance().getBeliefDEs());
			} else {
				((TextView) et11).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getBeliefExc())) {
				((TextView) et12).setText(AdultInfo.instance().getBeliefExc());
			} else {
				((TextView) et12).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getOtherDEs())) {
				((TextView) et13).setText(AdultInfo.instance().getOtherDEs());
			} else {
				((TextView) et13).setText("");
			}
			if (!TextUtils.isEmpty(AdultInfo.instance().getOtherExc())) {
				((TextView) et14).setText(AdultInfo.instance().getOtherExc());
			} else {
				((TextView) et14).setText("");
			}
		} else {
			ChildrenInfo.instance().clear();
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesDEs())) {
				((TextView) et1).setText(ChildrenInfo.instance()
						.getSchedulesDEs());
			} else {
				((TextView) et1).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesExc())) {
				((TextView) et2).setText(ChildrenInfo.instance()
						.getSchedulesExc());
			} else {
				((TextView) et2).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeDEs())) {
				((TextView) et3).setText(ChildrenInfo.instance()
						.getAttitudeDEs());
			} else {
				((TextView) et3).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeExc())) {
				((TextView) et4).setText(ChildrenInfo.instance()
						.getAttitudeExc());
			} else {
				((TextView) et4).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyDEs())) {
				((TextView) et5).setText(ChildrenInfo.instance().getStudyDEs());
			} else {
				((TextView) et5).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyExc())) {
				((TextView) et6).setText(ChildrenInfo.instance().getStudyExc());
			} else {
				((TextView) et6).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveDEs())) {
				((TextView) et7).setText(ChildrenInfo.instance().getLoveDEs());
			} else {
				((TextView) et7).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveExc())) {
				((TextView) et8).setText(ChildrenInfo.instance().getLoveExc());
			} else {
				((TextView) et8).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectDEs())) {
				((TextView) et9).setText(ChildrenInfo.instance()
						.getRespectDEs());
			} else {
				((TextView) et9).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectExc())) {
				((TextView) et10).setText(ChildrenInfo.instance()
						.getRespectExc());
			} else {
				((TextView) et10).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionDEs())) {
				((TextView) et11).setText(ChildrenInfo.instance()
						.getActionDEs());
			} else {
				((TextView) et11).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionExc())) {
				((TextView) et12).setText(ChildrenInfo.instance()
						.getActionExc());
			} else {
				((TextView) et2).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherDEs())) {
				((TextView) et13)
						.setText(ChildrenInfo.instance().getOtherDEs());
			} else {
				((TextView) et13).setText("");
			}
			if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherExc())) {
				((TextView) et14)
						.setText(ChildrenInfo.instance().getOtherExc());
			} else {
				((TextView) et14).setText("");
			}
		}
	}

	protected void eTextsave(String inputStr, int Textid) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(inputStr)) {
			if (isAdult) {
				switch (Textid) {
				case 1:
					AdultInfo.instance().setIdeaDEs(inputStr);
					break;
				case 2:
					AdultInfo.instance().setIdeaExc(inputStr);
					break;
				case 3:
					AdultInfo.instance().setAttitudeDEs(inputStr);
					break;
				case 4:
					AdultInfo.instance().setAttitudeExc(inputStr);
					break;
				case 5:
					AdultInfo.instance().setActionDEs(inputStr);
					break;
				case 6:
					AdultInfo.instance().setActionExc(inputStr);
					break;
				case 7:
					AdultInfo.instance().setTreatDEs(inputStr);
					break;
				case 8:
					AdultInfo.instance().setTreatExc(inputStr);
					break;
				case 9:
					AdultInfo.instance().setWorkDEs(inputStr);
					break;
				case 10:
					AdultInfo.instance().setWorkExc(inputStr);
					break;
				case 11:
					AdultInfo.instance().setBeliefDEs(inputStr);
					break;
				case 12:
					AdultInfo.instance().setBeliefExc(inputStr);
					break;

				case 13:
					AdultInfo.instance().setOtherDEs(inputStr);
					break;
				case 14:
					AdultInfo.instance().setOtherExc(inputStr);
					break;
				default:
					break;
				}
			} else {
				switch (Textid) {
				case 1:
					ChildrenInfo.instance().setSchedulesDEs(inputStr);
					break;
				case 2:
					ChildrenInfo.instance().setSchedulesExc(inputStr);
					break;
				case 3:
					ChildrenInfo.instance().setAttitudeDEs(inputStr);
					break;
				case 4:
					ChildrenInfo.instance().setAttitudeExc(inputStr);
					break;
				case 5:
					ChildrenInfo.instance().setStudyDEs(inputStr);
					break;
				case 6:
					ChildrenInfo.instance().setStudyExc(inputStr);
					break;
				case 7:
					ChildrenInfo.instance().setLoveDEs(inputStr);
					break;
				case 8:
					ChildrenInfo.instance().setLoveExc(inputStr);
					break;
				case 9:
					ChildrenInfo.instance().setRespectDEs(inputStr);
					break;
				case 10:
					ChildrenInfo.instance().setRespectExc(inputStr);
					break;
				case 11:
					ChildrenInfo.instance().setActionDEs(inputStr);
					break;
				case 12:
					ChildrenInfo.instance().setActionExc(inputStr);
					break;

				case 13:
					ChildrenInfo.instance().setOtherDEs(inputStr);
					break;
				case 14:
					ChildrenInfo.instance().setOtherExc(inputStr);
					break;
				default:
					break;
				}
			}
		}
	}

	private void Credit() {
		// ((TextView) tv_total1).setText(String.valueOf(Credit));

		if (Credit <= MAX_NUMBER && Credit >= 0) {
			((TextView) tv_total1).setText(String.valueOf(Credit));
			if (isAdult) {
				AdultInfo.instance().setCredit(Credit);
			} else {
				ChildrenInfo.instance().setCredit(Credit);
			}
		}

	}

	private void fault() {
		// ((TextView) tv_total2).setText(String.valueOf(fault));
		if (fault <= MAX_NUMBER && fault >= 0) {
			((TextView) tv_total2).setText(String.valueOf(fault));
			if (isAdult) {
				AdultInfo.instance().setFault(fault);
			} else {
				ChildrenInfo.instance().setFault(fault);
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Credit = 0;
		fault = 0;
	}
}
