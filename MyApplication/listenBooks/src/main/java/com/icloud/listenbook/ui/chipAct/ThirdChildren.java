package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import com.icloud.listenbook.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.BaseFragmentActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.dialog.AlertDlg;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.EditDlg;
import com.icloud.listenbook.entity.AdultInfo;
import com.icloud.listenbook.entity.ChildrenInfo;
import com.icloud.listenbook.ui.fragment.contentFrag;
import com.icloud.listenbook.ui.fragment.lastFrag;
import com.icloud.listenbook.unit.AdultUtils;
import com.icloud.listenbook.unit.ChildrenUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ThirdChildren extends BaseFragmentActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	String TAG = getClass().getName();
	private static final int SHOW_DATAPICK = 0;
	private static final int SHOW_DATAPICK_END = 1;
	private static final int DATE_DIALOG_ID = 2;
	private static final int DATE_DIALOG_ENDID = 3;
	private int mYear;
	private int mMonth;
	private int mDay;
	public static String startTimeString = null;
	public static String endTimeString = null;
	private contentFrag ContentFrag;
	private contentFrag ContentFrag2;
	private lastFrag LastFrag;
	private lastFrag LastFrag2;
	View back;

	Button btn_starttime;
	Button btn_endtime;
	TextView tv_search;
	TextView tv_endsearch;
	View ig_search;

	TextView tv_title1;
	TextView tv_title2;
	TextView tv_title3;
	TextView tv_title4;
	TextView tv_title5;
	TextView tv_title6;
	TextView tv_title7;
	View tv_search1;
	View tv_search2;
	View tv_search3;
	View tv_search4;
	View tv_search5;
	View tv_search6;
	View tv_search7;
	View tv_search8;
	View tv_search9;
	View tv_search10;
	View tv_search11;
	View tv_search12;
	View tv_search13;
	View tv_search14;
	View tv_search15;
	View tv_search16;
	View tv_uplast;
	View tv_last;
	View tv_current;
	TextView search_wait;
	View submit;
	private FrameLayout fl_content;
	private FragmentManager fragmentManager;
	boolean isAdult;

	// ResultItemAdapet resultItemAdapet;
	// List<ResultItemInfo> lists;
	// FullyLinearLayoutManager manager;
	// RecyclerView recyclerview;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		isAdult = getIntent().getBooleanExtra("isAdult", false);
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_thirdadult;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		btn_starttime = (Button) findViewById(R.id.btn_starttime);
		btn_endtime = (Button) findViewById(R.id.btn_endtime);
		tv_search = (TextView) findViewById(R.id.tv_search);
		tv_endsearch = (TextView) findViewById(R.id.tv_endsearch);
		ig_search = findViewById(R.id.ig_search);
		search_wait = (TextView) findViewById(R.id.search_wait);

		tv_search1 = findViewById(R.id.tv_search1);
		tv_search2 = findViewById(R.id.tv_search2);
		tv_search3 = findViewById(R.id.tv_search3);
		tv_search4 = findViewById(R.id.tv_search4);
		tv_search5 = findViewById(R.id.tv_search5);
		tv_search6 = findViewById(R.id.tv_search6);
		tv_search7 = findViewById(R.id.tv_search7);
		tv_search8 = findViewById(R.id.tv_search8);
		tv_search9 = findViewById(R.id.tv_search9);
		tv_search10 = findViewById(R.id.tv_search10);
		tv_search11 = findViewById(R.id.tv_search11);
		tv_search12 = findViewById(R.id.tv_search12);
		tv_search13 = findViewById(R.id.tv_search13);
		tv_search14 = findViewById(R.id.tv_search14);
		tv_search15 = findViewById(R.id.tv_search15);
		tv_search16 = findViewById(R.id.tv_search16);

		tv_title1 = (TextView) findViewById(R.id.tv_title1);
		tv_title2 = (TextView) findViewById(R.id.tv_title2);
		tv_title3 = (TextView) findViewById(R.id.tv_title3);
		tv_title4 = (TextView) findViewById(R.id.tv_title4);
		tv_title5 = (TextView) findViewById(R.id.tv_title5);
		tv_title6 = (TextView) findViewById(R.id.tv_title6);
		tv_title7 = (TextView) findViewById(R.id.tv_title7);

		fl_content = (FrameLayout) findViewById(R.id.fl_content);
		tv_uplast = findViewById(R.id.tv_uplast);
		tv_last = findViewById(R.id.tv_last);
		tv_current = findViewById(R.id.tv_current);
		submit = findViewById(R.id.submit);
		// recyclerview = (RecyclerView) findViewById(R.id.recyclerview);

	}

	@Override
	public void setDatas() {
		// TODO Auto-generated method stub
		super.setDatas();
		if (!isAdult) {
			tv_title1.setText("作息规律");
			tv_title2.setText("对人态度");
			tv_title3.setText("学习专注");
			tv_title4.setText("爱心善意");
			tv_title5.setText("尊师重教");
			tv_title6.setText("思考行动");
			tv_title7.setText("其  它");
		}
		// lists = new ArrayList<ResultItemInfo>();
		// ResultItemInfo info = new ResultItemInfo(true, true, "起心动念", "无",
		// "无");
		// lists.add(info);
		// manager = new FullyLinearLayoutManager(this);
		// manager.setOrientation(LinearLayoutManager.VERTICAL);
		// resultItemAdapet = new ResultItemAdapet(this, lists);
		// recyclerview.setLayoutManager(manager);
		// recyclerview.setAdapter(resultItemAdapet);
		// recyclerview.setVisibility(View.GONE);

		// 获取到fragment的管理者
		fragmentManager = getSupportFragmentManager();
		// 开启事务
		FragmentTransaction mTransaction = fragmentManager.beginTransaction();
		ContentFrag = new contentFrag(this, isAdult);
//		ContentFrag2 = new contentFrag(this, isAdult);

		LastFrag = new lastFrag(this, isAdult, true);
		LastFrag2 = new lastFrag(this, isAdult, false);

		mTransaction.replace(R.id.fl_content, ContentFrag).commit();
		setDateTime();
	}

	@Override
	public void setListeners() {
		btn_starttime.setOnClickListener(this);
		btn_endtime.setOnClickListener(this);
		ig_search.setOnClickListener(this);
		back.setOnClickListener(this);
		tv_uplast.setOnClickListener(this);
		tv_last.setOnClickListener(this);
		tv_current.setOnClickListener(this);
		submit.setOnClickListener(this);
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
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {

		case R.id.btn_starttime:
			Message msg = new Message();
			if (btn_starttime.equals((Button) v)) {
				msg.what = ThirdChildren.SHOW_DATAPICK;
			}
			this.dateandtimeHandler.sendMessage(msg);
			break;

		case R.id.btn_endtime:
			Message endmsg = new Message();
			if (btn_endtime.equals((Button) v)) {
				endmsg.what = ThirdChildren.SHOW_DATAPICK_END;
			}
			this.dateandtimeHandler.sendMessage(endmsg);
			break;

		case R.id.ig_search:

//			int[] array = AdultUtils.instance().SearchEvent(this, this,
//					startTimeString, endTimeString, search_wait, submit);
//			if (array == null) {
//
//				break;
//			}
//			setSearchResult(array);
			break;

		case R.id.back:
			finish();
			break;

		case R.id.tv_current:
			// if (recyclerview.getVisibility() == View.VISIBLE)
			// recyclerview.setVisibility(View.GONE);
			FragmentTransaction ft = fragmentManager.beginTransaction();

			tv_current.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_last.setBackgroundResource(R.drawable.tab_right_default);
			tv_uplast.setBackgroundResource(R.drawable.tab_right_default);
			ft.replace(R.id.fl_content, ContentFrag2).commit();
//			if (isAdult) {
//				ft.replace(R.id.fl_content, ContentFrag).commit();
//			}else{
//				ft.replace(R.id.fl_content, ContentFrag2).commit();
//			}
			break;
		case R.id.tv_last:
			FragmentTransaction ft2 = fragmentManager.beginTransaction();

			tv_current.setBackgroundResource(R.drawable.tab_right_default);
			tv_last.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_uplast.setBackgroundResource(R.drawable.tab_right_default);

			ft2.replace(R.id.fl_content, LastFrag).commit();
			break;
		case R.id.tv_uplast:
			FragmentTransaction ft3 = fragmentManager.beginTransaction();

			tv_current.setBackgroundResource(R.drawable.tab_right_default);
			tv_last.setBackgroundResource(R.drawable.tab_right_default);
			tv_uplast.setBackgroundResource(R.drawable.tab_left_pressed);

			ft3.replace(R.id.fl_content, LastFrag2).commit();
			break;
		case R.id.submit:

			// GameApp.instance().MeritTableAdultlists = new
			// ArrayList<MeritTableAdult>();
			// MeritTableAdult Adult = new MeritTableAdult();
			// Adult.setDate(System.currentTimeMillis());
			// Adult.setGong(3);
			// Adult.setGuo(3);
			// GameApp.instance().MeritTableAdultlists.add(Adult);
			if (isAdult) {
				GameApp.instance().MeritTableAdultlists = AdultUtils.instance()
						.getLists();
				Intent intent = new Intent(this, ResultAct.class);
				intent.putExtra("isadult", isAdult);
				startActivity(intent);
			} else {
				GameApp.instance().MeritTableChildrenlists = ChildrenUtils
						.instance().getLists();
				Intent intent = new Intent(this, ResultAct.class);
				intent.putExtra("isadult", isAdult);
				startActivity(intent);
			}
		}

	}

	public void setSearchResult(int[] array) {
		if (array == null)
			return;
		// TODO Auto-generated method stub
		((TextView) tv_search3).setText(String.valueOf(array[0]));
		((TextView) tv_search4).setText(String.valueOf(array[1]));
		((TextView) tv_search5).setText(String.valueOf(array[2]));
		((TextView) tv_search6).setText(String.valueOf(array[3]));
		((TextView) tv_search7).setText(String.valueOf(array[4]));
		((TextView) tv_search8).setText(String.valueOf(array[5]));
		((TextView) tv_search9).setText(String.valueOf(array[6]));
		((TextView) tv_search10).setText(String.valueOf(array[7]));
		((TextView) tv_search11).setText(String.valueOf(array[8]));
		((TextView) tv_search12).setText(String.valueOf(array[9]));
		((TextView) tv_search13).setText(String.valueOf(array[10]));
		((TextView) tv_search14).setText(String.valueOf(array[11]));
		((TextView) tv_search15).setText(String.valueOf(array[12]));
		((TextView) tv_search16).setText(String.valueOf(array[13]));

		((TextView) tv_search1).setText(String.valueOf(array[14]));
		((TextView) tv_search2).setText(String.valueOf(array[15]));
	}

	/**
	 * 处理日期控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;

			case SHOW_DATAPICK_END:
				showDialog(DATE_DIALOG_ENDID);
				break;

			}
		}
	};

	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		// updateDateDisplay();
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		tv_search.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
		startTimeString = (new StringBuilder().append(mYear).append(
				(mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append((mDay < 10) ? "0" + mDay : mDay)).toString();
	}

	private void updateEndDateDisplay() {
		tv_endsearch.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
		endTimeString = (new StringBuilder().append(mYear).append(
				(mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append((mDay < 10) ? "0" + mDay : mDay)).toString();
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay();
		}
	};
	private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateEndDateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case DATE_DIALOG_ENDID:
			return new DatePickerDialog(this, mEndDateSetListener, mYear,
					mMonth, mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case DATE_DIALOG_ENDID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (GameApp.instance().MeritTableAdultlists != null)
			GameApp.instance().MeritTableAdultlists.clear();
		if (GameApp.instance().MeritTableChildrenlists != null)
			GameApp.instance().MeritTableChildrenlists.clear();
		AdultUtils.instance().setLists();
		ChildrenUtils.instance().setLists();
		startTimeString = null;
		endTimeString = null;
	}
}

// final int drawable_on = R.drawable.checkbox_pressed;
// final int drawable_off = R.drawable.checkbox_normal;
// private static final int SHOW_DATAPICK = 0;
// private static final int SHOW_DATAPICK_END = 1;
// private static final int DATE_DIALOG_ID = 2;
// private static final int DATE_DIALOG_ENDID = 3;
// private static final int MAX_NUMBER = 7;
// private int mYear;
// private int mMonth;
// private int mDay;
// private static int Credit = 0;
// private static int fault = 0;
// public static String startTimeString = null;
// public static String endTimeString = null;
// AlertDlg sumbitDlg;
// View back;
// TextView eText;
// RelativeLayout btn1;
// RelativeLayout btn2;
// RelativeLayout btn3;
// RelativeLayout btn4;
// RelativeLayout btn5;
// RelativeLayout btn6;
// RelativeLayout btn7;
// RelativeLayout btn8;
// RelativeLayout btn9;
// RelativeLayout btn10;
// RelativeLayout btn11;
// RelativeLayout btn12;
// RelativeLayout btn13;
// RelativeLayout btn14;
//
// View et1;
// View et2;
// View et3;
// View et4;
// View et5;
// View et6;
// View et7;
// View et8;
// View et9;
// View et10;
// View et11;
// View et12;
// View et13;
// View et14;
// View iv1;
// View iv2;
// View iv3;
// View iv4;
// View iv5;
// View iv6;
// View iv7;
// View iv8;
// View iv9;
// View iv10;
// View iv11;
// View iv12;
// View iv13;
// View iv14;
// View tv_total1;
// View tv_total2;
//
// View submit;
// Button btn_starttime;
// Button btn_endtime;
// TextView tv_search;
// TextView tv_endsearch;
// View ig_search;
// TextView submit_wait;
// View tv_search1;
// View tv_search2;
// View tv_search3;
// View tv_search4;
// View tv_search5;
// View tv_search6;
// View tv_search7;
// View tv_search8;
// View tv_search9;
// View tv_search10;
// View tv_search11;
// View tv_search12;
// View tv_search13;
// View tv_search14;
// View tv_search15;
// View tv_search16;
// List<Object> gonglist;
// List<Object> guolist;
// TextView search_wait;
// @Override
// public void init() {
// // TODO Auto-generated method stub
// final Calendar c = Calendar.getInstance();
// mYear = c.get(Calendar.YEAR);
// mMonth = c.get(Calendar.MONTH);
// mDay = c.get(Calendar.DAY_OF_MONTH);
// gonglist = new ArrayList<Object>();
// guolist = new ArrayList<Object>();
// gonglist.add(1000);
// guolist.add(1000);
// }
//
// @Override
// public int getLayout() {
// // TODO Auto-generated method stub
// return R.layout.act_children;
// }
//
// @Override
// public void findViews() {
// back = findViewById(R.id.back);
// btn1 = (RelativeLayout) findViewById(R.id.btn1);
// btn2 = (RelativeLayout) findViewById(R.id.btn2);
// btn3 = (RelativeLayout) findViewById(R.id.btn3);
// btn4 = (RelativeLayout) findViewById(R.id.btn4);
// btn5 = (RelativeLayout) findViewById(R.id.btn5);
// btn6 = (RelativeLayout) findViewById(R.id.btn6);
// btn7 = (RelativeLayout) findViewById(R.id.btn7);
// btn8 = (RelativeLayout) findViewById(R.id.btn8);
// btn9 = (RelativeLayout) findViewById(R.id.btn9);
// btn10 = (RelativeLayout) findViewById(R.id.btn10);
// btn11 = (RelativeLayout) findViewById(R.id.btn11);
// btn12 = (RelativeLayout) findViewById(R.id.btn12);
// btn13 = (RelativeLayout) findViewById(R.id.btn13);
// btn14 = (RelativeLayout) findViewById(R.id.btn14);
//
// et1 = findViewById(R.id.et1);
// et2 = findViewById(R.id.et2);
// et3 = findViewById(R.id.et3);
// et4 = findViewById(R.id.et4);
// et5 = findViewById(R.id.et5);
// et6 = findViewById(R.id.et6);
// et7 = findViewById(R.id.et7);
// et8 = findViewById(R.id.et8);
// et9 = findViewById(R.id.et9);
// et10 = findViewById(R.id.et10);
// et11 = findViewById(R.id.et11);
// et12 = findViewById(R.id.et12);
// et13 = findViewById(R.id.et13);
// et14 = findViewById(R.id.et14);
//
// submit = findViewById(R.id.submit);
// tv_total1 = findViewById(R.id.tv_total1);
// tv_total2 = findViewById(R.id.tv_total2);
// iv1 = findViewById(R.id.im1);
// iv2 = findViewById(R.id.im2);
// iv3 = findViewById(R.id.im3);
// iv4 = findViewById(R.id.im4);
// iv5 = findViewById(R.id.im5);
// iv6 = findViewById(R.id.im6);
// iv7 = findViewById(R.id.im7);
// iv8 = findViewById(R.id.im8);
// iv9 = findViewById(R.id.im9);
// iv10 = findViewById(R.id.im10);
// iv11 = findViewById(R.id.im11);
// iv12 = findViewById(R.id.im12);
// iv13 = findViewById(R.id.im13);
// iv14 = findViewById(R.id.im14);
//
// btn_starttime = (Button) findViewById(R.id.btn_starttime);
// btn_endtime = (Button) findViewById(R.id.btn_endtime);
// tv_search = (TextView) findViewById(R.id.tv_search);
// tv_endsearch = (TextView) findViewById(R.id.tv_endsearch);
// ig_search = findViewById(R.id.ig_search);
//
// tv_search1 = findViewById(R.id.tv_search1);
// tv_search2 = findViewById(R.id.tv_search2);
// tv_search3 = findViewById(R.id.tv_search3);
// tv_search4 = findViewById(R.id.tv_search4);
// tv_search5 = findViewById(R.id.tv_search5);
// tv_search6 = findViewById(R.id.tv_search6);
// tv_search7 = findViewById(R.id.tv_search7);
// tv_search8 = findViewById(R.id.tv_search8);
// tv_search9 = findViewById(R.id.tv_search9);
// tv_search10 = findViewById(R.id.tv_search10);
// tv_search11 = findViewById(R.id.tv_search11);
// tv_search12 = findViewById(R.id.tv_search12);
// tv_search13 = findViewById(R.id.tv_search13);
// tv_search14 = findViewById(R.id.tv_search14);
// tv_search15 = findViewById(R.id.tv_search15);
// tv_search16 = findViewById(R.id.tv_search16);
// submit_wait =(TextView) findViewById(R.id.submit_wait);
// search_wait=(TextView) findViewById(R.id.search_wait);
//
// setDateTime();
// }
//
// @Override
// public void setListeners() {
// btn1.setOnClickListener(this);
// btn2.setOnClickListener(this);
// btn3.setOnClickListener(this);
// btn4.setOnClickListener(this);
// btn5.setOnClickListener(this);
// btn6.setOnClickListener(this);
// btn7.setOnClickListener(this);
// btn8.setOnClickListener(this);
// btn9.setOnClickListener(this);
// btn10.setOnClickListener(this);
// btn11.setOnClickListener(this);
// btn12.setOnClickListener(this);
// btn13.setOnClickListener(this);
// btn14.setOnClickListener(this);
//
// et1.setOnClickListener(this);
// et2.setOnClickListener(this);
// et3.setOnClickListener(this);
// et4.setOnClickListener(this);
// et5.setOnClickListener(this);
// et6.setOnClickListener(this);
// et7.setOnClickListener(this);
// et8.setOnClickListener(this);
// et9.setOnClickListener(this);
// et10.setOnClickListener(this);
// et11.setOnClickListener(this);
// et12.setOnClickListener(this);
// et13.setOnClickListener(this);
// et14.setOnClickListener(this);
//
// et1.setOnTouchListener(ViewUtils.instance().onTouchListener);
// et2.setOnTouchListener(ViewUtils.instance().onTouchListener);
// submit.setOnClickListener(this);
// btn_starttime.setOnClickListener(this);
// btn_endtime.setOnClickListener(this);
// ig_search.setOnClickListener(this);
// back.setOnClickListener(this);
// }
//
// @Override
// public void onErrorResponse(VolleyError error) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void onResponse(JSONObject response) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void onClick(View v) {
// // TODO Auto-generated method stub
// int id = v.getId();
// switch (id) {
// case R.id.btn1:
// // Bitmap d=BitmapFactory.decodeResource(this.getResources(),
// // R.drawable.btn_check_on);
//
// // ChildrenInfo.instance().setSchedules(true);
// // iv1.setBackgroundResource(drawable_on);
// // iv2.setBackgroundResource(drawable_off);
// // add(1);
// // break;
// // case R.id.btn2:
// //
// // iv2.setBackgroundResource(drawable_on);
// // iv1.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setSchedules(false);
// // dec(1);
// // break;
// // case R.id.btn3:
// //
// // iv3.setBackgroundResource(drawable_on);
// // iv4.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setAttitude(true);
// // add(2);
// // break;
// // case R.id.btn4:
// //
// // iv4.setBackgroundResource(drawable_on);
// // iv3.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setAttitude(false);
// // dec(2);
// // break;
// // case R.id.btn5:
// //
// // iv5.setBackgroundResource(drawable_on);
// // iv6.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setStudy(true);
// // add(3);
// // break;
// // case R.id.btn6:
// //
// // iv6.setBackgroundResource(drawable_on);
// // iv5.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setStudy(false);
// // dec(3);
// // break;
// // case R.id.btn7:
// //
// // iv7.setBackgroundResource(drawable_on);
// // iv8.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setLove(true);
// // add(4);
// // break;
// // case R.id.btn8:
// //
// // iv8.setBackgroundResource(drawable_on);
// // iv7.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setLove(false);
// // dec(4);
// // break;
// // case R.id.btn9:
// //
// // iv9.setBackgroundResource(drawable_on);
// // iv10.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setRespect(true);
// // add(5);
// // break;
// // case R.id.btn10:
// //
// // iv10.setBackgroundResource(drawable_on);
// // iv9.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setRespect(false);
// // dec(5);
// // break;
// // case R.id.btn11:
// //
// // iv11.setBackgroundResource(drawable_on);
// // iv12.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setAction(true);
// // add(6);
// // break;
// // case R.id.btn12:
// //
// // iv12.setBackgroundResource(drawable_on);
// // iv11.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setAction(true);
// // dec(6);
// // break;
// // case R.id.btn13:
// //
// // iv13.setBackgroundResource(drawable_on);
// // iv14.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setOther(true);
// // add(7);
// // break;
// // case R.id.btn14:
// //
// // iv14.setBackgroundResource(drawable_on);
// // iv13.setBackgroundResource(drawable_off);
// // ChildrenInfo.instance().setOther(false);
// // dec(7);
// // break;
//
// case R.id.et1:
// edit(et1, 1);
// break;
// case R.id.et2:
// edit(et2, 2);
// break;
// case R.id.et3:
// edit(et3, 3);
// break;
// case R.id.et4:
// edit(et4, 4);
// break;
// case R.id.et5:
// edit(et5, 5);
// break;
// case R.id.et6:
// edit(et6, 6);
// break;
// case R.id.et7:
// edit(et7, 7);
// break;
// case R.id.et8:
// edit(et8, 8);
// break;
// case R.id.et9:
// edit(et9, 9);
// break;
// case R.id.et10:
// edit(et10, 10);
// break;
// case R.id.et11:
// edit(et11, 11);
// break;
// case R.id.et12:
// edit(et12, 12);
// break;
// case R.id.et13:
// edit(et13, 13);
// break;
// case R.id.et14:
// edit(et14, 14);
// break;
//
// case R.id.btn_starttime:
// Message msg = new Message();
// if (btn_starttime.equals((Button) v)) {
// msg.what = ThirdChildren.SHOW_DATAPICK;
// }
// ThirdChildren.this.dateandtimeHandler.sendMessage(msg);
// break;
//
// case R.id.btn_endtime:
// Message endmsg = new Message();
// if (btn_endtime.equals((Button) v)) {
// endmsg.what = ThirdChildren.SHOW_DATAPICK_END;
// }
// ThirdChildren.this.dateandtimeHandler.sendMessage(endmsg);
// break;
// case R.id.submit:
//
// if (sumbitDlg == null) {
// sumbitDlg = DialogManage.showAlertDlg(this, "提示",
// "功过表每日只能提交一次，提交后奖励20金币，是否提交？", R.string.sumbit,
// new android.content.DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog,
// int which) {
// LogUtil.e(TAG, "提交结果了\n");
//
// ChildrenUtils.instance().submitEvent(
// ThirdChildren.this,submit_wait);
// sumbitDlg.dismiss();
// }
// }, R.string.cancel);
//
// } else
// sumbitDlg.show();
//
// break;
// case R.id.ig_search:
//
// // int[] array = ChildrenUtils.instance().SearchEvent(this, this,
// // startTimeString, endTimeString,search_wait);
// // if (array == null) {
// // break;
// // }
// // setSearchResult(array);
// // break;
//
// case R.id.back:
// finish();
// break;
// }
//
// }
//
// public void setSearchResult(int[] array) {
// if (array == null)
// return;
// // TODO Auto-generated method stub
// ((TextView) tv_search3).setText(String.valueOf(array[0]));
// ((TextView) tv_search4).setText(String.valueOf(array[1]));
// ((TextView) tv_search5).setText(String.valueOf(array[2]));
// ((TextView) tv_search6).setText(String.valueOf(array[3]));
// ((TextView) tv_search7).setText(String.valueOf(array[4]));
// ((TextView) tv_search8).setText(String.valueOf(array[5]));
// ((TextView) tv_search9).setText(String.valueOf(array[6]));
// ((TextView) tv_search10).setText(String.valueOf(array[7]));
// ((TextView) tv_search11).setText(String.valueOf(array[8]));
// ((TextView) tv_search12).setText(String.valueOf(array[9]));
// ((TextView) tv_search13).setText(String.valueOf(array[10]));
// ((TextView) tv_search14).setText(String.valueOf(array[11]));
// ((TextView) tv_search15).setText(String.valueOf(array[12]));
// ((TextView) tv_search16).setText(String.valueOf(array[13]));
//
// ((TextView) tv_search1).setText(String.valueOf(array[14]));
// ((TextView) tv_search2).setText(String.valueOf(array[15]));
// }
//
// private void add(Object id) {
// // TODO Auto-generated method stub
// if (!gonglist.contains(id)) {
// gonglist.add(id);
// Credit++;
// ChildrenInfo.instance().setCredit(Credit);
// if (guolist.contains(id)) {
// guolist.remove(id);
// fault--;
// ChildrenInfo.instance().setFault(fault);
// }
// } else {
// return;
// }
// if (Credit <= MAX_NUMBER) {
// ((TextView) tv_total1).setText(String.valueOf(Credit));
// }
// if (fault >= 0) {
//
// ((TextView) tv_total2).setText(String.valueOf(fault));
// }
// }
//
// private void dec(Object id) {
// if (!guolist.contains(id)) {
// guolist.add(id);
// fault++;
// ChildrenInfo.instance().setFault(fault);
// if (gonglist.contains(id)) {
// gonglist.remove(id);
// Credit--;
// ChildrenInfo.instance().setCredit(Credit);
// }
// } else {
// return;
// }
// if (fault <= MAX_NUMBER) {
// ((TextView) tv_total2).setText(String.valueOf(fault));
// }
// if (Credit >= 0) {
// ((TextView) tv_total1).setText(String.valueOf(Credit));
// }
// }
//
// EditDlg inputDlg;
// int eTextid;
//
// protected void edit(View v, int i) {
// eText = (TextView) v;
// eTextid = i;
// if (inputDlg == null) {
// inputDlg = DialogManage.showEditInput(this, R.string.edittitle,
// R.string.confirm,
// new android.content.DialogInterface.OnClickListener() {
// @Override
// public void onClick(DialogInterface dialog, int which) {
// if (inputDlg != null) {
// String inputStr = inputDlg.getInput();
// if (!TextUtils.isEmpty(inputStr)) {
// try {
// eText.setText(inputStr);
// eTextsave(inputStr, eTextid);
// inputDlg.dismiss();
// } catch (Exception e) {
// }
// }
// }
// }
// }, R.string.cancel);
// inputDlg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
// inputDlg.mutilShow();
// inputDlg.show();
// } else
// switch (eTextid) {
// case 1:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getSchedulesDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 2:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getSchedulesExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 3:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getAttitudeDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 4:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getAttitudeExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 5:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getStudyDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 6:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getStudyExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 7:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getLoveDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 8:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getLoveExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 9:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getRespectDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 10:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getRespectExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 11:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getActionDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 12:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getActionExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 13:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherDEs())) {
// inputDlg.setBody(ChildrenInfo.instance().getOtherDEs());
// } else {
// inputDlg.setBody("");
// }
// break;
// case 14:
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherExc())) {
// inputDlg.setBody(ChildrenInfo.instance().getOtherExc());
// } else {
// inputDlg.setBody("");
// }
// break;
// default:
// break;
// }
// inputDlg.show();
//
// }
//
// @Override
// protected void onStart() {
// super.onStart();
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesDEs())) {
// ((TextView) et1).setText(ChildrenInfo.instance().getSchedulesDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getSchedulesExc())) {
// ((TextView) et2).setText(ChildrenInfo.instance().getSchedulesExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeDEs())) {
// ((TextView) et3).setText(ChildrenInfo.instance().getAttitudeDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getAttitudeExc())) {
// ((TextView) et4).setText(ChildrenInfo.instance().getAttitudeExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyDEs())) {
// ((TextView) et5).setText(ChildrenInfo.instance().getStudyDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getStudyExc())) {
// ((TextView) et6).setText(ChildrenInfo.instance().getStudyExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveDEs())) {
// ((TextView) et7).setText(ChildrenInfo.instance().getLoveDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getLoveExc())) {
// ((TextView) et8).setText(ChildrenInfo.instance().getLoveExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectDEs())) {
// ((TextView) et9).setText(ChildrenInfo.instance().getRespectDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getRespectExc())) {
// ((TextView) et10).setText(ChildrenInfo.instance().getRespectExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionDEs())) {
// ((TextView) et11).setText(ChildrenInfo.instance().getActionDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getActionExc())) {
// ((TextView) et12).setText(ChildrenInfo.instance().getActionExc());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherDEs())) {
// ((TextView) et13).setText(ChildrenInfo.instance().getOtherDEs());
// }
// if (!TextUtils.isEmpty(ChildrenInfo.instance().getOtherExc())) {
// ((TextView) et14).setText(ChildrenInfo.instance().getOtherExc());
// }
// }
//
// protected void eTextsave(String inputStr, int Textid) {
// // TODO Auto-generated method stub
// if (!TextUtils.isEmpty(inputStr)) {
// switch (Textid) {
// case 1:
// ChildrenInfo.instance().setSchedulesDEs(inputStr);
// break;
// case 2:
// ChildrenInfo.instance().setSchedulesExc(inputStr);
// break;
// case 3:
// ChildrenInfo.instance().setAttitudeDEs(inputStr);
// break;
// case 4:
// ChildrenInfo.instance().setAttitudeExc(inputStr);
// break;
// case 5:
// ChildrenInfo.instance().setStudyDEs(inputStr);
// break;
// case 6:
// ChildrenInfo.instance().setStudyDEs(inputStr);
// break;
// case 7:
// ChildrenInfo.instance().setLoveDEs(inputStr);
// break;
// case 8:
// ChildrenInfo.instance().setLoveExc(inputStr);
// break;
// case 9:
// ChildrenInfo.instance().setRespectDEs(inputStr);
// break;
// case 10:
// ChildrenInfo.instance().setRespectExc(inputStr);
// break;
// case 11:
// ChildrenInfo.instance().setActionDEs(inputStr);
// break;
// case 12:
// ChildrenInfo.instance().setActionDEs(inputStr);
// break;
//
// case 13:
// AdultInfo.instance().setOtherDEs(inputStr);
// break;
// case 14:
// AdultInfo.instance().setOtherDEs(inputStr);
// break;
// default:
// break;
// }
// }
// }
//
// /**
// * 处理日期控件的Handler
// */
// Handler dateandtimeHandler = new Handler() {
// public void handleMessage(Message msg) {
// switch (msg.what) {
// case ThirdChildren.SHOW_DATAPICK:
// showDialog(DATE_DIALOG_ID);
// break;
//
// case ThirdChildren.SHOW_DATAPICK_END:
// showDialog(DATE_DIALOG_ENDID);
// break;
//
// }
// }
// };
//
// /**
// * 设置日期
// */
// private void setDateTime() {
// final Calendar c = Calendar.getInstance();
// mYear = c.get(Calendar.YEAR);
// mMonth = c.get(Calendar.MONTH);
// mDay = c.get(Calendar.DAY_OF_MONTH);
// // updateDateDisplay();
// }
//
// /**
// * 更新日期显示
// */
// private void updateDateDisplay() {
// tv_search.setText(new StringBuilder().append(mYear).append("-")
// .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
// .append("-").append((mDay < 10) ? "0" + mDay : mDay));
// startTimeString = (new StringBuilder().append(mYear).append(
// (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
// .append((mDay < 10) ? "0" + mDay : mDay)).toString();
// }
//
// private void updateEndDateDisplay() {
// tv_endsearch.setText(new StringBuilder().append(mYear).append("-")
// .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
// .append("-").append((mDay < 10) ? "0" + mDay : mDay));
// endTimeString = (new StringBuilder().append(mYear).append(
// (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
// .append((mDay < 10) ? "0" + mDay : mDay)).toString();
// }
//
// /**
// * 日期控件的事件
// */
// private DatePickerDialog.OnDateSetListener mDateSetListener = new
// DatePickerDialog.OnDateSetListener() {
// public void onDateSet(DatePicker view, int year, int monthOfYear,
// int dayOfMonth) {
//
// mYear = year;
// mMonth = monthOfYear;
// mDay = dayOfMonth;
// updateDateDisplay();
// }
// };
// private DatePickerDialog.OnDateSetListener mEndDateSetListener = new
// DatePickerDialog.OnDateSetListener() {
// public void onDateSet(DatePicker view, int year, int monthOfYear,
// int dayOfMonth) {
//
// mYear = year;
// mMonth = monthOfYear;
// mDay = dayOfMonth;
// updateEndDateDisplay();
// }
// };
//
// @Override
// protected Dialog onCreateDialog(int id) {
// switch (id) {
// case DATE_DIALOG_ID:
// return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
// mDay);
// case DATE_DIALOG_ENDID:
// return new DatePickerDialog(this, mEndDateSetListener, mYear,
// mMonth, mDay);
// }
// return null;
// }
//
// @Override
// protected void onPrepareDialog(int id, Dialog dialog) {
// switch (id) {
// case DATE_DIALOG_ID:
// ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
// break;
// case DATE_DIALOG_ENDID:
// ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
// break;
// }
// }
//
// @Override
// protected void onDestroy() {
// // TODO Auto-generated method stub
// super.onDestroy();
// if (gonglist != null) {
// gonglist.clear();
// }
// if (guolist != null) {
// guolist.clear();
// }
// Credit = 0;
// fault = 0;
// }
//
// @Override
// protected void onStop() {
// // TODO Auto-generated method stub
// super.onStop();
//
// }
// }
