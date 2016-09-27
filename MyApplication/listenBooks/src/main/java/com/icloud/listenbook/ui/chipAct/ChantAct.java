package com.icloud.listenbook.ui.chipAct;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.StaticLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.VolleyUtils;
import com.icloud.listenbook.http.datas.HttpConfig;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.ChantAdapter;
import com.icloud.listenbook.ui.adapter.LessonAdapter;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.UIUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ChantAct extends BaseActivity implements OnClickListener,
		RadioGroup.OnCheckedChangeListener {
	public static final int PAGE_CHANT = 1;
	public static final int PAGE_LESSON = PAGE_CHANT + 1;
	public static final int SUMBIT_ENSURE = 1;
	public static final int SHOW_MARKS = SUMBIT_ENSURE + 1;
	public static final int REGONE_GOIT = SHOW_MARKS + 1;
	View back;
	View chantView;
	TextView title;
	RadioGroup tabs;
	RadioButton chantBtn;
	RadioButton lessonBtn;
	RecyclerView list;
	RecyclerView list_chant;
	TextView tv_end;
	static RelativeLayout rl;
	LessonAdapter lessonAdapter;
	ChantAdapter chantAdapter;
	int viewPage;
	private LinearLayoutManager layoutManager;
	private LinearLayoutManager layoutManager2;
	// 功课数据
	ArrayList<LessonItem> datas_buf;
	// 功课列表
	ArrayList<LessonItem> datas_list;
	public static ArrayList<LessonItem> datas_chant;
	private boolean isBuf = true;
	private boolean ischant;
	public static lessonHandler handler;
	private  PopupWindow popupWindow;
	private int width;
	private int height;
    LinearLayout ll_progressbar;
    
	@Override
	public void init() {
		ischant = getIntent().getBooleanExtra("ischant", true);
		if (ischant)
			viewPage = PAGE_CHANT;
		else {
			viewPage = PAGE_LESSON;
		}
		handler = new lessonHandler(this);
		layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager2 = new LinearLayoutManager(this);
		layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
		lessonAdapter = new LessonAdapter(this);
		chantAdapter = new ChantAdapter(this);
		datas_buf = new ArrayList<LessonItem>();
		datas_list = new ArrayList<LessonItem>();
		datas_chant = new ArrayList<LessonItem>();
	}

	@Override
	public int getLayout() {
		return R.layout.act_chant;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		tabs = (RadioGroup) findViewById(R.id.tabs);
		chantBtn = (RadioButton) findViewById(R.id.chantBtn);
		lessonBtn = (RadioButton) findViewById(R.id.lessonBtn);
		list = (RecyclerView) findViewById(R.id.list);
		list_chant = (RecyclerView) findViewById(R.id.list_chant);
		chantView = findViewById(R.id.chantView);
		rl = (RelativeLayout) findViewById(R.id.rl);
		list.setLayoutManager(layoutManager);
		list.setHasFixedSize(true);
		list.setAdapter(lessonAdapter);
		list_chant.setLayoutManager(layoutManager2);
		list_chant.setHasFixedSize(true);
		list_chant.setAdapter(chantAdapter);
		tv_end = (TextView) findViewById(R.id.tv_end);
		ll_progressbar=(LinearLayout) findViewById(R.id.ll_progressbar);
		ll_progressbar.setVisibility(View.VISIBLE);
		ViewTreeObserver vto = rl.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				rl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				height = rl.getHeight();
				width = rl.getWidth();
			}
		});
		
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		tabs.setOnCheckedChangeListener(this);
		tv_end.setOnClickListener(this);
		tv_end.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	public void setDatas() {
		JSONObject jb = new JSONObject();
		try {
			jb.put("uid", UserInfo.instance().getUid());
			jb.put("token", UserInfo.instance().getToken());
			jb.put("version", SharedPreferenceUtil.getChantVersion());
			LogUtil.e("TAG", jb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.chant, jb,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response)  {
						LogUtil.e("TAG", response.toString());
						JsonUtils.refreshChant(response);
						 upCacheData();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						 upCacheData();
					}
				});
		
	}
    private void upCacheData(){
    	ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				datas_chant.addAll(JsonUtils.toChantItems(30));
				datas_buf.addAll(JsonUtils.toLessonItems(null));
				datas_list.addAll(JsonUtils.toLessonMarksItems(30));

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						chantAdapter.up(datas_chant);
						lessonAdapter.up(datas_list);
						lessonAdapter.up(datas_buf);
					}
				});
			}
		});
    	ll_progressbar.setVisibility(View.GONE);
		upPage();
    }
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.chantBtn:
			viewPage = PAGE_CHANT;
			break;
		case R.id.lessonBtn:
			viewPage = PAGE_LESSON;
			break;
		}
		upPage();
	}

	private void upPage() {
		switch (viewPage) {
		case PAGE_LESSON:
			tabs.check(R.id.lessonBtn);
			list.setVisibility(View.VISIBLE);
			chantView.setVisibility(View.GONE);
			tv_end.setVisibility(View.VISIBLE);
			break;
		case PAGE_CHANT:
			tabs.check(R.id.chantBtn);
			list.setVisibility(View.GONE);
			chantView.setVisibility(View.VISIBLE);
			tv_end.setVisibility(View.GONE);
			
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;
		case R.id.tv_end:
			if (isBuf) {
				tv_end.setText("返回顿悟功课");
				lessonAdapter.up(datas_list);
			} else {
				tv_end.setText("查看往期顿悟功课");
				lessonAdapter.up(datas_buf);
			}
			isBuf = !isBuf;
			break;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static class lessonHandler extends Handler {
		WeakReference<Activity> WRact;

		public lessonHandler(Activity act) {
			WRact = new WeakReference<Activity>(act);
		}

		public void handleMessage(Message msg) {
			ChantAct act = (ChantAct) WRact.get();
			int what = msg.what;
			switch (what) {
			case SUMBIT_ENSURE:
				act.popupWindow.dismiss();
				break;
			case SHOW_MARKS:
				act.ll_progressbar.setVisibility(View.GONE);
				String markString=(String) msg.obj;
				act.getPopupWindow();
				// 这里是位置显示方式,在屏幕的左侧
				act.popupWindow.showAtLocation(act.chantBtn, Gravity.CENTER, 0,
						0);
				act.tv_marks.setText(markString);
				break;
			case REGONE_GOIT:
				String date=(String) msg.obj;
				ArrayList<LessonItem> lessonItems = JsonUtils.toLessonItems(date);
				act.lessonAdapter.up(lessonItems);
				act.tv_end.setText("查看往期顿悟功课");
				act.isBuf = !act.isBuf;
				
			default:
				break;
			}
		}
	}
	TextView tv_ensure;
	TextView tv_marks;
	protected void initPopuptMenuWindow() {	
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		View popupWindow_view = getLayoutInflater().inflate(
				R.layout.lesson_popwin, null, false);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view,
				width, LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 这个是为了点击“返回Back”也能使其消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);

		tv_marks = (TextView) popupWindow_view.findViewById(R.id.tv_marks);
		tv_ensure = (TextView) popupWindow_view.findViewById(R.id.tv_ensure);
		tv_ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(SUMBIT_ENSURE);
			}
		});
	}

	/***
	 * 获取PopupWindow实例
	 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptMenuWindow();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		datas_chant=null;
	}

}
