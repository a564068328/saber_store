package com.icloud.listenbook.ui.chipFrage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.CircleNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.ui.chipAct.FeedbackAct;
import com.icloud.listenbook.ui.chipAct.FriendsListAct;
import com.icloud.listenbook.ui.chipAct.GreatMaster;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.MembersList;
import com.icloud.listenbook.ui.chipAct.SettingAct;
import com.icloud.listenbook.ui.chipAct.ShareAct;
import com.icloud.listenbook.ui.chipAct.SystemMsgAct;
import com.icloud.listenbook.ui.chipAct.UpUserInfoAct;
import com.icloud.listenbook.ui.chipAct.WapActivity;
import com.icloud.listenbook.ui.chipFrage.HomePageFrage.upReceiver;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
/*
 * “我的”界面
 */
public class UserInfoFrage extends BaseFragement implements OnClickListener {
	CircleNetworkImageView userIcon;
	TextView userName;
	TextView signature;//签名
	TextView collectConut;//收藏数
	TextView friendsConut;//好友数
	View friendsTip;
	TextView collectTip;
	TextView loginTxt;
	TextView currencyTxt; //金币数
	View goInfo;
	View messageL;
	View vipL;
	View readingL;
	View ideaL;//意见反馈
	View settingL;//设置
	View msg_l; //聊天室条目
	View msgIcoTip;//聊天室消息tip
	View fmsgTip;
	View buyL;//商城
	View recommendedL;//推荐
	View membersL;//会员风采
	View greatMasterL;//名士大家
	View systemMsgL;//消息通告
	View systemMsgTip;
	ImageLoader imageLoader;
	String urlHead;

	@Override
	public int getLayout() {
		return R.layout.frage_userinfo_page;
	}

	@Override
	public void setListeners() {
		loginTxt.setOnClickListener(this);
		loginTxt.setOnTouchListener(ViewUtils.instance().onTouchListener);
		systemMsgL.setOnClickListener(this);
		systemMsgL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		msg_l.setOnClickListener(this);
		msg_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		userIcon.setOnClickListener(this);
		userIcon.setOnTouchListener(ViewUtils.instance().onTouchListener);
		goInfo.setOnClickListener(this);
		goInfo.setOnTouchListener(ViewUtils.instance().onTouchListener);
		messageL.setOnClickListener(this);
		messageL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		readingL.setOnClickListener(this);
		readingL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		ideaL.setOnClickListener(this);
		ideaL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		vipL.setOnClickListener(this);
		vipL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		settingL.setOnClickListener(this);
		settingL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		collectConut.setOnClickListener(this);
		collectConut.setOnTouchListener(ViewUtils.instance().onTouchListener);
		friendsConut.setOnClickListener(this);
		friendsConut.setOnTouchListener(ViewUtils.instance().onTouchListener);
		collectTip.setOnClickListener(this);
		collectTip.setOnTouchListener(ViewUtils.instance().onTouchListener);
		friendsTip.setOnClickListener(this);
		friendsTip.setOnTouchListener(ViewUtils.instance().onTouchListener);
		recommendedL.setOnClickListener(this);
		recommendedL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		buyL.setOnClickListener(this);
		buyL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		membersL.setOnClickListener(this);
		membersL.setOnTouchListener(ViewUtils.instance().onTouchListener);
		greatMasterL.setOnClickListener(this);
		greatMasterL.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void init() {
		super.init();
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public void findViews() {
		buyL = findViewById(R.id.buy_l);
		msg_l = findViewById(R.id.msg_l);
		recommendedL = findViewById(R.id.recommended_l);
		userIcon = (CircleNetworkImageView) findViewById(R.id.userIcon);
		currencyTxt = (TextView) findViewById(R.id.currency);
		loginTxt = (TextView) findViewById(R.id.loginTxt);
		userName = (TextView) findViewById(R.id.userName);
		signature = (TextView) findViewById(R.id.signature);
		collectConut = (TextView) findViewById(R.id.collectConut);
		friendsConut = (TextView) findViewById(R.id.friendsConut);
		friendsTip = findViewById(R.id.friendsTip);
		collectTip = (TextView) findViewById(R.id.collectTip);
		goInfo = findViewById(R.id.goInfo);
		messageL = findViewById(R.id.message_l);
		vipL = findViewById(R.id.vip_l);
		readingL = findViewById(R.id.reading_l);
		ideaL = findViewById(R.id.idea_l);
		settingL = findViewById(R.id.setting_l);
		msgIcoTip = findViewById(R.id.msgIcoTip);
		fmsgTip = findViewById(R.id.fmsgTip);
		membersL = findViewById(R.id.members_l);
		greatMasterL = findViewById(R.id.greatMaster_l);
		systemMsgL = findViewById(R.id.systemMsg_l);
		systemMsgTip = findViewById(R.id.systemMsgTip);

	}

	@Override
	public void setData() {
		super.setData();
		userIcon.setDefaultImageResId(R.drawable.user_defa_icon);
		userIcon.setErrorImageResId(R.drawable.user_defa_icon);
	}

	@Override
	public void onResume() {
		super.onResume();
		rReceiver();
		checkLogin();
	}

	@Override
	public void onPause() {
		super.onPause();
		uReceiver();
	}

	protected void checkLogin() {

		friendsConut.setText(String.valueOf(UserInfo.instance()
				.getFriendsCount()));
		userName.setVisibility(View.INVISIBLE);
		signature.setVisibility(View.INVISIBLE);
		loginTxt.setVisibility(View.INVISIBLE);
		String size = String.valueOf(0);
		if (UserInfo.instance().isLogin()) {
			size = String.valueOf(IoUtils.instance().getCollectSize(
					UserInfo.instance().getUid()));
			collectConut.setText(size);
			userName.setText(UserInfo.instance().getNick());
			String signatureTxt = UserInfo.instance().getSignature();
			if (TextUtils.isEmpty(signatureTxt))
				signatureTxt = "签名:";
			signature.setText(signatureTxt);
			userIcon.setImageUrl(urlHead + UserInfo.instance().getIcon(),
					imageLoader);
			userName.setVisibility(View.VISIBLE);
			signature.setVisibility(View.VISIBLE);
			msgIcoTip
					.setVisibility(ChatMsgManage.instance().getChatTip() ? View.VISIBLE
							: View.GONE);
			//好友发送了消息将显示红点
			fmsgTip.setVisibility((ChatMsgManage.instance().getFriendMsgTip()
					.size() > 0) ? View.VISIBLE : View.GONE);
			currencyTxt.setText(String.format(act.getString(R.string.gold),
					UserInfo.instance().getCurrency()));
			systemMsgTip.setVisibility((ChatMsgManage.instance()
					.getSystemMsgList().size() > 0) ? View.VISIBLE : View.GONE);

		} else {
			userIcon.setImageUrl("", imageLoader);
			loginTxt.setVisibility(View.VISIBLE);
			msgIcoTip.setVisibility(View.GONE);
			fmsgTip.setVisibility(View.GONE);
			systemMsgTip.setVisibility(View.GONE);
			currencyTxt.setText("");
			signature.setText("签名:");
		}
		collectConut.setText(size);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collectConut:
		case R.id.collectTip: {
			gotoCollect();
			break;
		}
		case R.id.systemMsg_l:
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, SystemMsgAct.class);
				return;
			}
		case R.id.buy_l:
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, WapActivity.class);
				return;
			}
		case R.id.goInfo: {
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, UpUserInfoAct.class);
				return;
			}
		}
		case R.id.msg_l: {
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, ChatMsgAct.class);
				return;
			}
		}
		case R.id.idea_l: {
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, FeedbackAct.class);
				return;
			}

		}
		case R.id.friendsTip:
		case R.id.friendsConut:
			if (UserInfo.instance().isLogin()) {
				LoadingTool.launchActivity(act, FriendsListAct.class);
				return;
			}

		case R.id.loginTxt:
			/** 跳转到登陆页面 */
			LoadingTool.launchActivity(act, LoginAct.class);
			break;

		case R.id.setting_l:
			LoadingTool.launchActivity(act, SettingAct.class);
			break;
		case R.id.recommended_l:
			LoadingTool.launchActivity(act, ShareAct.class);
			break;
		case R.id.members_l:
			LoadingTool.launchActivity(act, MembersList.class);
			break;
		case R.id.greatMaster_l:
			LoadingTool.launchActivity(act, GreatMaster.class);
			break;
		}

	}

	private void gotoCollect() {
		Activity act = this.getActivity();
		if (act instanceof TablesActivity) {
			((TablesActivity) act).upPage(1);
		}
	}

	protected void rReceiver() {
		//注册聊天室信息，好友信息，聊天室信息广播接受者
		IntentFilter filter = new IntentFilter();
		filter.addAction(ChatMsgManage.CHAT_MSG_ADD_ACTION);
		filter.addAction(ChatMsgManage.FRIEND_MSG_ADD_ACTION);
		filter.addAction(ChatMsgManage.SYSTEM_MSG_ACTION);
		act.registerReceiver(mReceiver, filter);
	}

	protected void uReceiver() {
		act.unregisterReceiver(mReceiver);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.e("UserInfoFrage", "更新topView和今日推荐");
			checkLogin();
		}

	};
}
