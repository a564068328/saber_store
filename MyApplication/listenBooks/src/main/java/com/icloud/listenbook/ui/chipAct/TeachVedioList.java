package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.view.ScrollTextView;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.sdk.yunwa.LvieListenerImpl;
import com.icloud.listenbook.ui.adapter.TeachLectureListAdapter;
import com.icloud.listenbook.ui.adapter.TeachVedioAdapter;
import com.icloud.listenbook.ui.adapter.entity.LectureInfo;
import com.icloud.listenbook.ui.popup.BasePopupWindow;
import com.icloud.listenbook.ui.popup.myBasePopupWindow;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.UIUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.yunva.live.sdk.LiveListener;
import com.yunva.live.sdk.YayaLiveApi;
import com.yunva.live.sdk.interfaces.logic.model.CurrencyInfo;
import com.yunva.live.sdk.interfaces.logic.model.RoomInfo;
import com.yunva.live.sdk.lib.logic.WhatType;

public class TeachVedioList extends BaseActivity implements OnClickListener,
		OnItemClickListener, Listener<JSONObject>, ErrorListener {
	String TAG = getClass().getName();
	public static final int UP_ROOM_VIEW = 20150825;
	public static final int SHOW_ABSTRUT = 20160428;
	public static final int SHOW_NOTIFY = SHOW_ABSTRUT + 1;
	public static final int NOTIFY_UP_promat = SHOW_NOTIFY + 1;
	public static final int ROOMprocesses = NOTIFY_UP_promat + 1;
	public static final int QUICKLYWATCH = ROOMprocesses + 1;
	public static final int NOTPLAY = QUICKLYWATCH + 1;
	View progress;
	TextView progressTip;
	private YayaLiveApi yunvaLive = null;
	private LiveListener listener;
	View back;
	ListView list;
	TeachVedioAdapter adapter;
	TeachLectureListAdapter lectureListAdapter;
	ArrayList<LectureInfo> data;
	ArrayList<Timer> timerlist;
	TextView tip;
	boolean isLoadRoom;
	TextView yyTag;
	View iv_list;
	TextView tv;
	TextView tv_bygone_chant;
	TextView tv_chant;
	TextView tv_bygone_lesson;
	TextView tv_lesson;
	ArrayList<TextView> tvlist;
	private PopupWindow popupWindow;
	Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (TeachVedioList.this.isFinishing())
				return;
			switch (msg.what) {
			case WhatType.DISCONNECTED_NOTIFY:
			case WhatType.SDK_INIT_COMPLETE:
				/*** sdk 初始化完成 自动登陆 */
				if (yunvaLive != null) {

					try {
						// JSONObject jb = new JSONObject();
						// jb.put("uid", UserInfo.instance().getUid());
						// jb.put("token", UserInfo.instance().getToken());
						String jb = "token=" + UserInfo.instance().getToken()
								+ "&uid=" + UserInfo.instance().getUid();
						yunvaLive.binding(jb.toString());
						yyTag.setText(R.string.yy_load_01);
						yyTag.setVisibility(View.VISIBLE);
					} catch (Exception e) {
					}

				}
				break;
			case WhatType.BINDING_SUCCEE:// 登录账号成功
				/** 获取播放列表 */
				if (yunvaLive != null) {
					yunvaLive.getPlayListReq();
				}
				/** 设置支付 */
				{
					yunvaLive.setConsumptionType("2");
					yunvaLive.setRechargeType(0);
					List<CurrencyInfo> list = new ArrayList<CurrencyInfo>();
					list.add(new CurrencyInfo("47", "元"));
					yunvaLive.setCurrencyInfoList(list);
					yunvaLive.setUserHeadUrl(ServerIps.getLoginAddr()
							+ UserInfo.instance().getIcon());
				}
				yyTag.setText(R.string.yy_load_02);
				yyTag.setVisibility(View.VISIBLE);
				break;
			case WhatType.BINDING_FAIL:
				yyTag.setText(R.string.yy_load_erro);
				yyTag.setVisibility(View.VISIBLE);
				ToastUtil.showMessage("获取失败,请稍后再试!");
				UIHandler.sendEmptyMessageDelayed(WhatType.SDK_INIT_COMPLETE,
						1000);
				break;
			case UP_ROOM_VIEW:
				yyTag.setText(R.string.yy_load_03);
				upRoomInfo((ArrayList<RoomInfo>) msg.obj);
				yyTag.setVisibility(View.GONE);
				break;

			case SHOW_ABSTRUT:
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;

				}
				Intent intent = new Intent(TeachVedioList.this,
						TeachAbstractAct.class);
				startActivity(intent);
				break;
			case SHOW_NOTIFY:
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				TeachVedioList.this.startActivity(new Intent(
						TeachVedioList.this, TeachNotifyAct.class));
				break;

			case NOTIFY_UP_promat:

				tv = (TextView) msg.obj;
				// final int postion = (Integer) tv.getTag();
				Timer timer = new Timer();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						if (!isLoadRoom) {
							// ToastUtil.showMessage("房间加载中...");
							UIHandler.sendEmptyMessage(ROOMprocesses);
							return;
						}
						if (adapter != null) {
							RoomInfo roomInfo;
							int position = (Integer) tv.getTag();
							if (data.size() > position) {
								if (!data.get(position).isstart) {
									return;
								}
							}
							int roomId = lectureListAdapter.getItem(position).roomid;
							for (int i = 0; i < adapter.getCount(); i++) {
								roomInfo = adapter.getItem(i);
								if (roomInfo.getRoomId() == roomId) {
									Message message = Message.obtain();
									message.what = QUICKLYWATCH;
									message.obj = roomInfo;
									UIHandler.sendMessage(message);
									return;
								}
							}
							UIHandler.sendEmptyMessage(NOTPLAY);
						}
					}
				};
				timer.schedule(task, 1000, 5000); // 1s后启动任务，每5s执行一次
				timerlist.add(timer);
				break;
			case ROOMprocesses:
				tv.setBackgroundResource(R.drawable.white);
				tv.setBackgroundColor(TeachVedioList.this.getResources()
						.getColor(R.color.white));
				tv.setText("房间加载中");
				tv.setTextColor(TeachVedioList.this.getResources().getColor(
						R.color.black));
				tv.setClickable(false);
				break;
			case QUICKLYWATCH:
				tv.setClickable(true);
				tv.setText("打开房间");
				tv.setTextColor(TeachVedioList.this.getResources().getColor(
						R.color.orange));
				tv.setBackgroundResource(R.drawable.textview_orange);
				final RoomInfo roomInfo = (RoomInfo) msg.obj;
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (TeachVedioList.this.list.getAdapter() == adapter)
							// yunvaLive.gotoLiveRoomActivity(TeachVedioList.this,
							// adapter.getItem(postion));
							yunvaLive.gotoLiveRoomActivity(TeachVedioList.this,
									roomInfo);
						if (TeachVedioList.this.list.getAdapter() == lectureListAdapter)
							yunvaLive.gotoLiveRoomActivity(TeachVedioList.this,
									roomInfo);
					}
				});
				break;
			case NOTPLAY:
				tv.setBackgroundResource(R.drawable.white);
				tv.setBackgroundColor(TeachVedioList.this.getResources()
						.getColor(R.color.white));
				tv.setClickable(false);
				tv.setText("尚未开播");
				tv.setTextColor(TeachVedioList.this.getResources().getColor(
						R.color.black));
				break;
			}
		};
	};

	protected void upRoomInfo(ArrayList<RoomInfo> roomInfo) {
		isLoadRoom = true;
		if (TeachVedioList.this.list.getAdapter() == adapter) {
			if (roomInfo.size() == 0) {
				tip.setVisibility(View.VISIBLE);
				tip.setText(R.string.no_room);
			}
			progress.setVisibility(View.INVISIBLE);
			progressTip.setVisibility(View.INVISIBLE);
		}
		adapter.upType(roomInfo);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (yunvaLive != null) {
			yunvaLive.onDestroy();
		}
		if (timerlist != null && timerlist.size() > 0) {
			for (Timer time : timerlist) {
				time.cancel();
			}
			timerlist.clear();
		}
		
	}

	@Override
	public void getDatas() {
		super.getDatas();
		HttpUtils.getLectureList(this, this);
		HttpUtils.getNotice(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int res = response.optInt("result", -1);
					String msg = response.optString("msg", "");
					if (res == 0 && !TextUtils.isEmpty(msg)) {
						SharedPreferenceUtil.setTEACH_NOTIFY(msg);
						if (!SharedPreferenceUtil.getTeachNotify()) {
							TeachVedioList.this.startActivity(new Intent(
									TeachVedioList.this, TeachNotifyAct.class));
						}
					}

				} catch (Exception e) {
				}
			}
		}, null);
	}

	@Override
	public void init() {
		isLoadRoom = false;
		listener = new LvieListenerImpl(UIHandler);
		adapter = new TeachVedioAdapter(this);
		data = new ArrayList<LectureInfo>();
		timerlist = new ArrayList<Timer>();
		lectureListAdapter = new TeachLectureListAdapter(this, UIHandler);
		Display display = getWindowManager().getDefaultDisplay();
		yunvaLive = YayaLiveApi.getInstance(this, LvieListenerImpl.APPID,
				display.getWidth(), display.getHeight(), listener, true, false);
		tvlist = new ArrayList<TextView>();
	}

	@Override
	public int getLayout() {
		return R.layout.act_teach_vedio;
	}

	@Override
	public void findViews() {
		progress = findViewById(R.id.progress);
		back = findViewById(R.id.back);
		progressTip = (TextView) findViewById(R.id.progressTip);
		list = (ListView) findViewById(R.id.list);
		tip = (TextView) findViewById(R.id.tip);
		// pptTxt = (ScrollTextView) findViewById(R.id.pptTxt);
		yyTag = (TextView) findViewById(R.id.yyTag);
		// pptTxt.setVisibility(View.GONE);
		// tv_bygone_chant = (TextView) findViewById(R.id.tv_bygone_chant);
		tv_chant = (TextView) findViewById(R.id.tv_chant);
		// tv_bygone_lesson = (TextView) findViewById(R.id.tv_bygone_lesson);
		tv_lesson = (TextView) findViewById(R.id.tv_lesson);

		iv_list = findViewById(R.id.iv_list);
		list.setAdapter(lectureListAdapter);

		
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(GameApp.instance().onTouchListener);
		// pptTxt.setOnClickListener(this);
		// pptTxt.setOnTouchListener(GameApp.instance().onTouchListener);

		// list.setOnItemClickListener(this);
		iv_list.setOnClickListener(this);
		
		tv_chant.setOnClickListener(this);
		tv_chant.setOnTouchListener(GameApp.instance().onTouchListener);
		tv_lesson.setOnClickListener(this);
		tv_lesson.setOnTouchListener(GameApp.instance().onTouchListener);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();
		switch (id) {
		case R.id.back:
			onBack();
			break;
		case R.id.iv_list:
			getPopupWindow();
			// 这里是位置显示方式,在屏幕的左侧
			popupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.TOP, 0,
					UIUtils.dip2px(this, 75f));
			// popupWindow=new TeachPopupWindow(TeachVedioList.this);
			break;
		
		case R.id.tv_chant:
			intent=new Intent(this, ChantAct.class);
			intent.putExtra("ischant", true);
			LoadingTool.launchActivity(this, ChantAct.class,intent);
			break;
		
		case R.id.tv_lesson:
			intent=new Intent(this, ChantAct.class);
			intent.putExtra("ischant", false);
			LoadingTool.launchActivity(this, ChantAct.class,intent);
			break;
		}

	}

	protected void onBack() {
		if (list.getAdapter() == adapter) {
			progress.setVisibility(View.INVISIBLE);
			progressTip.setVisibility(View.INVISIBLE);
			list.setAdapter(lectureListAdapter);
		} else {
			this.finish();
		}
	}

	private void enterRoom(int roomId) {
		if (adapter != null) {
			RoomInfo roomInfo;
			for (int i = 0; i < adapter.getCount(); i++) {
				roomInfo = adapter.getItem(i);
				if (roomInfo.getRoomId() == roomId) {
					yunvaLive.gotoLiveRoomActivity(this, roomInfo);
					return;
				}
			}
		}
		if (!isLoadRoom)
			ToastUtil.showMessage("房间加载中...");
		else
			ToastUtil.showMessage("暂未开放");
	}

	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
	// long arg3) {
	// if (TeachVedioList.this.list.getAdapter() == adapter)
	// yunvaLive.gotoLiveRoomActivity(this, adapter.getItem(postion));
	// else if (TeachVedioList.this.list.getAdapter() == lectureListAdapter) {
	// if (!lectureListAdapter.getItem(postion).isstart) {
	// ToastUtil.showMessage("尚未开放!");
	// return;
	// }
	// enterRoom(lectureListAdapter.getItem(postion).roomid);
	// /*
	// * if (!isLoadRoom) { progress.setVisibility(View.VISIBLE);
	// * progressTip.setVisibility(View.VISIBLE); }
	// * TeachVedioList.this.list.setAdapter(adapter);
	// */
	// }
	// }

	@Override
	public void onErrorResponse(VolleyError error) {
		progress.setVisibility(View.INVISIBLE);
		progressTip.setVisibility(View.INVISIBLE);
		if (data.size() == 0)
			tip.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResponse(JSONObject response) {
		progress.setVisibility(View.INVISIBLE);
		progressTip.setVisibility(View.INVISIBLE);
		try {
			int res = response.optInt("result", -1);
			if (res == 0) {
				JSONArray list = response.optJSONArray("list");
				LogUtil.e(TAG, list.toString());
				data.clear();
				JSONObject jsonObj;
				LectureInfo item;
				for (int i = 0; i < list.length(); i++) {
					jsonObj = list.optJSONObject(i);
					item = JsonUtils.toLectureInfo(jsonObj);
					// LogUtil.e("TAG", "item.uri"+item.uri);
					data.add(item);
				}

				if (data.size() == 0) {
					tip.setVisibility(View.VISIBLE);
				} else {
					// 排序
					Collections.sort(data);
					lectureListAdapter.upData(data);
				}

			}

		} catch (Exception e) {
		}
	}

	View tv_abstruct;
	View tv_notify;

	/**
	 * 创建PopupWindow
	 */
	protected void initPopuptMenuWindow() {
		// TODO Auto-generated method stub
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		View popupWindow_view = getLayoutInflater().inflate(
				R.layout.popup_teach_menu, null, false);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 这个是为了点击“返回Back”也能使其消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);

		tv_abstruct = popupWindow_view.findViewById(R.id.tv_abstruct);
		tv_notify = popupWindow_view.findViewById(R.id.tv_notify);
		tv_abstruct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHandler.sendEmptyMessage(SHOW_ABSTRUT);
			}
		});
		tv_notify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHandler.sendEmptyMessage(SHOW_NOTIFY);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}
