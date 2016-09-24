package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.BaseFragmentActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.dialog.AlertDlg;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.EditDlg;
import com.icloud.listenbook.dialog.InputDlg;
import com.icloud.listenbook.entity.AdultInfo;
import com.icloud.listenbook.entity.ResultItemInfo;
import com.icloud.listenbook.recyclerplus.FullyLinearLayoutManager;
import com.icloud.listenbook.ui.adapter.ResultItemAdapet;
import com.icloud.listenbook.ui.fragment.contentFrag;
import com.icloud.listenbook.ui.fragment.lastFrag;
import com.icloud.listenbook.unit.AdultUtils;
import com.icloud.listenbook.unit.ChildrenUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.MeritTableAdult;

public class ThirdAdult extends BaseFragmentActivity implements
		OnClickListener, Listener<JSONObject>, ErrorListener {
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
    View standard;
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
		standard=findViewById(R.id.standard);
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
		// LastFrag = new lastFrag(this);
		LastFrag = new lastFrag(this, isAdult, true);
		LastFrag2 = new lastFrag(this, isAdult, false);

		mTransaction.replace(R.id.fl_content, ContentFrag).commit();
		setDateTime();
	}

	@Override
	public void setListeners() {
		standard.setOnClickListener(this);
		standard.setOnTouchListener(ViewUtils.instance().onTouchListener);
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
		Intent intent;
		int id = v.getId();
		switch (id) {

		case R.id.btn_starttime:
			Message msg = new Message();
			if (btn_starttime.equals((Button) v)) {
				msg.what = ThirdAdult.SHOW_DATAPICK;
			}
			ThirdAdult.this.dateandtimeHandler.sendMessage(msg);
			break;

		case R.id.btn_endtime:
			Message endmsg = new Message();
			if (btn_endtime.equals((Button) v)) {
				endmsg.what = ThirdAdult.SHOW_DATAPICK_END;
			}
			ThirdAdult.this.dateandtimeHandler.sendMessage(endmsg);
			break;

		case R.id.ig_search:
			int[] array = null;
            if(isAdult){
            	array = AdultUtils.instance().SearchEvent(this, this,
    					startTimeString, endTimeString, search_wait, submit);
            }
            else{
            	array = ChildrenUtils.instance().SearchEvent(this, this,
    					startTimeString, endTimeString, search_wait, submit);
            }
			if (array == null) {

				break;
			}
			setSearchResult(array);
			break;

		case R.id.back:
			finish();
			break;

		case R.id.standard:
			intent = new Intent(this, StandardAct.class);
			intent.putExtra("isadult", isAdult);
			LoadingTool.launchActivity(this, StandardAct.class,intent);
			break;
			
		case R.id.tv_current:
			// if (recyclerview.getVisibility() == View.VISIBLE)
			// recyclerview.setVisibility(View.GONE);
			FragmentTransaction ft = fragmentManager.beginTransaction();

			tv_current.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_last.setBackgroundResource(R.drawable.tab_right_default);
			tv_uplast.setBackgroundResource(R.drawable.tab_right_default);
			ft.replace(R.id.fl_content, ContentFrag).commit();
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

			if (isAdult) {
				GameApp.instance().MeritTableAdultlists = AdultUtils.instance()
						.getLists();
				intent = new Intent(this, ResultAct.class);
				intent.putExtra("isadult", isAdult);
				//startActivity(intent);
				LoadingTool.launchActivity(this, ResultAct.class,intent);
			} else {
				GameApp.instance().MeritTableChildrenlists = ChildrenUtils
						.instance().getLists();
				intent = new Intent(this, ResultAct.class);
				intent.putExtra("isadult", isAdult);
				//startActivity(intent);
				LoadingTool.launchActivity(this, ResultAct.class,intent);
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
			case ThirdAdult.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;

			case ThirdAdult.SHOW_DATAPICK_END:
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
