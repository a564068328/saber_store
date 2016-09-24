package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.BaseFragmentActivity;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.base.view.RefreshListView;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.pro.HallSocketManage;
import com.icloud.listenbook.socket.pro.objectbuffer;
import com.icloud.listenbook.ui.adapter.ChatMsgAdapter;
import com.icloud.listenbook.ui.adapter.ChatMsgLeftView;
import com.icloud.listenbook.ui.adapter.FriendMsgAdapter;
import com.icloud.listenbook.ui.custom.DragLayout;
import com.icloud.listenbook.ui.custom.DragLayout.OnDragStatusChangeListener;
import com.icloud.listenbook.ui.custom.DragLayout.Status;
import com.icloud.listenbook.ui.custom.MyLinearLayout;
import com.icloud.listenbook.ui.popup.PopuItem;
import com.icloud.listenbook.ui.popup.PopuJar;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.listenbook.unit.UserIconManage;
import com.icloud.listenbook.unit.ChatMsgManage.MessageListener;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.UserIconManage.IconListener;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.OtherUser;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

/**
 * 
 * 聊天室ACT
 * 
 */
public class ChatMsgAct extends BaseFragmentActivity implements
		OnClickListener, MessageListener, IconListener,
		EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {
	public static final long TIME_INTERVAL = 5 * 1000;
	// 更新的时间跨度
	public static final long UP_TIME_SPAN = 1 * 800;
	public static final int TO_BOTTOM = 1024;
	public static final int UPDATE_UNCHAT = TO_BOTTOM + 1;
	public static final int getNewest = UPDATE_UNCHAT + 1;
	public static final int getMore = getNewest + 1;
	
	
	MyLinearLayout mLinearLayout;
	private DragLayout mDragLayout;
	static ChatMsgAct chatMsgAct;
	ArrayList<ChatMsg> dataList;
	View back, emojiconSwitch, emojicons;
	PullToRefreshListView listView;
	ListView list;
	RelativeLayout rl_list;
	TextView send;
	EditText edit;
	TextView tipNum;
	// View del;
	ChatMsgAdapter adapter;
	private long lastSendLaba;
	int TipNum;
	long upTime;
	RelativeLayout rl_tip_news;
	LinearLayout ll_tip_news;
	TextView tip_news;// 有离线消息
	boolean isCompelet;
	Status mStatus = Status.Close;
	LinearLayout left_ll;
	ChatMsgLeftView chatMsgLeftView;
	ImageLoader imageLoader;
	/**
	 * 开始的位置
	 */
	private int mStartIndex = 0;
	/**
	 * 显示的个数
	 */
	private int showCount = 0;
	/**
	 * 每页展示最大数据
	 */
	private int maxCount;

	/**
	 * 一共有多少页面
	 */
	private int totalPage;
	/*
	 * 当前页
	 */
	private int currentPage;
	/*
	 * 总共的条目数
	 */
	private int totalNumber;
	
	protected Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (ChatMsgAct.this.isFinishing())
				return;
			// 大于更新的时间跨度才刷新UI
			if ((System.currentTimeMillis() - upTime) > UP_TIME_SPAN) {
				upTime = System.currentTimeMillis();
				// adapter.notifyDataSetChanged();
			} else {
				UIHandler.sendMessageDelayed(UIHandler.obtainMessage(),
						UP_TIME_SPAN);
				// UIHandler.sendMessageDelayed(msg,UP_TIME_SPAN);
			}
			if (msg.what == TO_BOTTOM && list != null && adapter != null) {
				if (currentPage != totalPage || mStatus != Status.Close)
					return;
				totalNumber = (int) IoUtils.instance().getChatMsgCount();
				showCount = totalNumber - maxCount * (totalPage - 1);
				int position = showCount - 1;
				dataList.clear();
				dataList.addAll((ArrayList<ChatMsg>) IoUtils.instance()
						.getMsgLimit(mStartIndex, showCount));
				currentPage = totalPage;
				adapter.data.clear();
				adapter.data.addAll(dataList);
				adapter.notifyDataSetChanged();
				list.setSelection(position);
			} else if (msg.what == UPDATE_UNCHAT && list != null
					&& adapter != null) {
				totalNumber = (int) IoUtils.instance().getChatMsgCount();
				mStartIndex = maxCount * (totalPage - 1);
				// LogUtil.e(TAG, "showCount2" + showCount);
				int position = totalNumber
						- ChatMsgManage.instance().getTotalSize();
				showCount = position + maxCount;
				currentPage = totalPage;
				dataList.clear();
				dataList.addAll((ArrayList<ChatMsg>) IoUtils.instance()
						.getMsgLimit(mStartIndex, showCount));
				adapter.data.clear();
				adapter.data.addAll(dataList);
				adapter.notifyDataSetChanged();
				list.setSelection(position);
				ll_tip_news.setVisibility(View.GONE);
				rl_tip_news.setVisibility(View.GONE);
			} else if (msg.what == getNewest && list != null && adapter != null) {
				if ((--currentPage) <= 0) {
					currentPage++;
					listView.onRefreshComplete();
					return;
				}
				mStartIndex = (currentPage - 1) * maxCount;
				showCount = maxCount;
				dataList.clear();
				dataList.addAll((ArrayList<ChatMsg>) IoUtils.instance()
						.getMsgLimit(mStartIndex, showCount));
				adapter.data.clear();
				adapter.data.addAll(dataList);
				adapter.notifyDataSetChanged();
				// list.setSelection(showCount - 1);
				listView.onRefreshComplete();
				// 这里必须放在listView.onRefreshComplete之后执行
				list.setSelection(ListView.FOCUS_DOWN);
			} else if (msg.what == getMore && list != null && adapter != null) {
				if ((++currentPage) > totalPage) {
					currentPage--;
					listView.onRefreshComplete();
					return;
				}
				mStartIndex = (currentPage - 1) * maxCount;
				totalNumber = (int) IoUtils.instance().getChatMsgCount();
				showCount = totalNumber - maxCount * (totalPage - 1);
				dataList.clear();
				dataList.addAll((ArrayList<ChatMsg>) IoUtils.instance()
						.getMsgLimit(mStartIndex, showCount));
				adapter.data.clear();
				adapter.data.addAll(dataList);
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				list.setSelection(0);
			}
		};
	};

	protected void onResume() {
		super.onResume();
		UserIconManage.instance().addImageListener(this);
		IntentFilter msgFilter = new IntentFilter();
		msgFilter.addAction(ChatMsgManage.UNCHAT_MSG_ADD_ACTION);
		// msgFilter.addAction(ChatMsgManage.ONCHAT_MSG_ADD_ACTION);
		registerReceiver(mReceiver, msgFilter);
		int getUnlineChatSize = ChatMsgManage.instance().getUnlineChatSize();
		if (getUnlineChatSize > 0) {
			ChatMsgManage.instance().setUnlineChatSize(0);
			if (getUnlineChatSize > 99)
				getUnlineChatSize = 99;
			tip_news.setText("您有" + getUnlineChatSize + "条消息未查看，点击查看");
			rl_tip_news.setVisibility(View.VISIBLE);
		}
		// mStatus=Status.Close;
		// mDragLayout.setStatusResonpe(mStatus);
		// LogUtil.e(TAG, "onResume+mStatus:" + mStatus);
	}

	protected void onPause() {
		super.onPause();
		UserIconManage.instance().removeImageListener(this);
		uReceiver();

		// mStatus=Status.Close;
		// mDragLayout.setStatusResonpe(mStatus);
		// LogUtil.e(TAG, "onPause+mStatus:" + mStatus);
	}

	protected void onDestroy() {
		super.onDestroy();
		ChatMsgManage.instance().addMessageListener(null);
		mDragLayout.setDragStatusListener(null);
		if (null != mDragLayout.getreceiver()) {
			unregisterReceiver(mDragLayout.getreceiver());
		}
	}

	@Override
	public void add() {
		UIHandler.obtainMessage(TO_BOTTOM).sendToTarget();
	}

	@Override
	public void init() {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		adapter = new ChatMsgAdapter(this, imageLoader);
		chatMsgAct = this;
		ChatMsgManage.instance().addMessageListener(this);
		maxCount = ChatMsgManage.CHAT_COUNT_LIMIT;
		dataList = new ArrayList<ChatMsg>();
		totalNumber = (int) IoUtils.instance().getChatMsgCount();
		totalPage = totalNumber / maxCount;
		currentPage = totalPage;
		mStartIndex = (totalPage - 1) * maxCount;
		showCount = maxCount;
		if (totalNumber % maxCount != 0) {
			showCount += totalNumber % maxCount;
		}
		
	}


	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// LogUtil.e(TAG, "更新离线消息");
			UIHandler.obtainMessage(UPDATE_UNCHAT).sendToTarget();
		}

	};

	protected void uReceiver() {
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
	}

	@Override
	public int getLayout() {
		return R.layout.act_chat_msg;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		emojiconSwitch = findViewById(R.id.emojicon);
		emojicons = findViewById(R.id.emojicons);
		listView = (PullToRefreshListView) findViewById(R.id.my_fresh_listview);
		edit = (EditText) findViewById(R.id.edit);
		send = (TextView) findViewById(R.id.send);
		tipNum = (TextView) findViewById(R.id.tip_num);
		// del = findViewById(R.id.del);
		left_ll = (LinearLayout) findViewById(R.id.left_ll);
		mLinearLayout = (MyLinearLayout) findViewById(R.id.mll);
		mDragLayout = (DragLayout) findViewById(R.id.dsl);
		// 设置引用
		mLinearLayout.setDraglayout(mDragLayout);
		mDragLayout.setDragStatusListener(mDragListener);
		// 设置左边栏各控件
		chatMsgLeftView = new ChatMsgLeftView(this, left_ll, imageLoader);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
				.commit();
		emojiconSwitch.setOnClickListener(this);

		tip_news = (TextView) findViewById(R.id.tip_news);
		rl_tip_news = (RelativeLayout) findViewById(R.id.rl_tip_news);
		ll_tip_news = (LinearLayout) findViewById(R.id.ll_tip_news);
		list = listView.getRefreshableView();
		list.setAdapter(adapter);
		list.setSelection(ListView.FOCUS_DOWN);
		
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		send.setOnClickListener(this);
		send.setOnTouchListener(ViewUtils.instance().onTouchListener);
		isUpOrDown();
		listView.setMode(Mode.BOTH);// 两端刷新
		
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				UIHandler.obtainMessage(getNewest).sendToTarget();

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				UIHandler.obtainMessage(getMore).sendToTarget();
			}
		});
		edit.addTextChangedListener(tw);
		rl_tip_news.setOnClickListener(this);
		dataList = (ArrayList<ChatMsg>) IoUtils.instance().getMsgLimit(
				mStartIndex, showCount);
		adapter.data.clear();
		adapter.data.addAll(dataList);
		adapter.notifyDataSetChanged();
		
//		setPopupMeun();
	}

	

	TextWatcher tw = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			send.setEnabled(edit.getText().length() >= 1 ? true : false);

		}
	};

	// 发送消息
	protected void send() {
		String msg = edit.getText().toString();
		// LogUtil.e(TAG,"发送消息："+ msg);
		if (TextUtils.isEmpty(msg)) {
			// ToastUtil.showMessage("发送消息不能为空");
			return;
		}
		long delta = System.currentTimeMillis() - lastSendLaba;
		if (delta >= TIME_INTERVAL) {
			if (HallSocketManage.instance().sendChat(
					UserInfo.instance().getNick(), msg,
					UserInfo.instance().getIcon())) {
				lastSendLaba = System.currentTimeMillis();
				edit.setText("");
				// UIHandler.obtainMessage(UPDATE_UNCHAT).sendToTarget();

			} else {
				ToastUtil.showMessage("未连接服务器发送失败");
			}
		} else {
			ToastUtil.showMessage("发送间隔不得小于5秒");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.del:
			ChatMsgManage.instance().clearchatMsg();
			if (adapter != null) {
				adapter.data.clear();
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.back:
			mDragLayout.open();
			break;
		case R.id.send:
			if (ToolUtils.isNetworkAvailableTip(this))
				send();
			break;
		case R.id.emojicon:
			boolean is = emojicons.getVisibility() == View.VISIBLE;
			emojicons.setVisibility(!is ? View.VISIBLE : View.GONE);

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// 接受软键盘输入的编辑文本或其它视图
			// imm.showSoftInput(edit,
			// InputMethodManager.SHOW_FORCED);
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);

			break;
		case R.id.rl_tip_news:
			objectbuffer.instance().getHallSocket().getUnlineMsg();
			ll_tip_news.setVisibility(View.VISIBLE);
			tip_news.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onEmojiconBackspaceClicked(View emojicon) {
		EmojiconsFragment.backspace(edit);

	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(edit, emojicon);

	}

	public static ChatMsgAct getChatMsgAct() {
		return chatMsgAct;
	}

	public void isUpOrDown() {
		// 设置刷新文本说明(展开刷新栏前)
		listView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载...");
		listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		listView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");

		listView.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"正在刷新...");
		listView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		listView.getLoadingLayoutProxy(true, false).setReleaseLabel("释放开始刷新");
	}

	private OnDragStatusChangeListener mDragListener = new OnDragStatusChangeListener() {

		@Override
		public void onOpen() {
			mStatus = Status.Open;
		}

		@Override
		public void onDraging(float percent) {
			mStatus = Status.Draging;
			ViewHelper.setAlpha(back, 1.0f - percent);
		}

		@Override
		public void onClose() {
			mStatus = Status.Close;
		}
	};

	public void onBackPressed() {

		if (mDragLayout.getStatus() != Status.Close) {
			mDragLayout.close();
			return;
		}
		super.onBackPressed();
	}

	public void clearChatmag() {
		if (adapter != null) {
			// adapter.data.clear();
			// adapter.notifyDataSetChanged();
		}
	}
}
