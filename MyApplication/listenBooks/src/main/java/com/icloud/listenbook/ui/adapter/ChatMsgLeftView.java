package com.icloud.listenbook.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.CircleNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.icloud.listenbook.R;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.WarningDlg;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.ui.chipAct.ChatNotifySetAct;
import com.icloud.listenbook.ui.chipAct.ChatPeopleInfoAct;
import com.icloud.listenbook.ui.chipAct.FeedbackAct;
import com.icloud.listenbook.ui.chipAct.UpUserInfoAct;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class ChatMsgLeftView implements OnClickListener {

	String TAG = getClass().getName();
	View view;
	ChatMsgAct act;
	TextView userName;
	TextView signature;// 签名
	CircleNetworkImageView userIcon;
	TextView time_of_online;
	View online_l;
	View gongguo_l;
	View feedback_l;
	View msgremind_l;
	View textsize_l;
	View textbubble_l;
	View exit_l;
	View del_l;
	ImageLoader imageLoader;
	String urlHead;
	private WarningDlg warningDlg;

	public ChatMsgLeftView(Activity act, View view, ImageLoader imageLoader2) {
		this.act = (ChatMsgAct) act;
		this.view = view;
		// RequestQueue mQueue = Volley.newRequestQueue(act);
		// LruImageCache lruImageCache = LruImageCache.instance(act
		// .getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = imageLoader2;
		init();
		setData();
	}

	private void setData() {
		userIcon.setDefaultImageResId(R.drawable.user_defa_icon);
		userIcon.setErrorImageResId(R.drawable.user_defa_icon);
		userIcon.setImageUrl(urlHead + UserInfo.instance().getIcon(),
				imageLoader);
		if (!TextUtils.isEmpty(UserInfo.instance().getSignature())) {
			signature.setText("签名:" + UserInfo.instance().getSignature());
		} else {
			signature.setText("签名:" + "暂无");
		}
		userName.setText(UserInfo.instance().getNick());
	}

	private void init() {
		userName = (TextView) view.findViewById(R.id.userName);
		signature = (TextView) view.findViewById(R.id.signature);
		userIcon = (CircleNetworkImageView) view.findViewById(R.id.userIcon);
		time_of_online = (TextView) view.findViewById(R.id.time_of_online);
		online_l = view.findViewById(R.id.online_l);
		gongguo_l = view.findViewById(R.id.gongguo_l);
		feedback_l = view.findViewById(R.id.feedback_l);
		msgremind_l = view.findViewById(R.id.msgremind_l);
		textsize_l = view.findViewById(R.id.textsize_l);
		textbubble_l = view.findViewById(R.id.textbubble_l);
		exit_l = view.findViewById(R.id.exit_l);
		del_l = view.findViewById(R.id.del_l);
		userIcon.setOnClickListener(this);
		online_l.setOnClickListener(this);
		online_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		gongguo_l.setOnClickListener(this);
		gongguo_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		feedback_l.setOnClickListener(this);
		feedback_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		msgremind_l.setOnClickListener(this);
		msgremind_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		textsize_l.setOnClickListener(this);
		textsize_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		textbubble_l.setOnClickListener(this);
		textbubble_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		exit_l.setOnClickListener(this);
		exit_l.setOnTouchListener(ViewUtils.instance().onTouchListener);
		del_l.setOnClickListener(this);
		del_l.setOnTouchListener(ViewUtils.instance().onTouchListener);

	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		Class<? extends Activity> activity;
		switch (i) {
		case R.id.userIcon:
			activity = UpUserInfoAct.class;
			LoadingTool.launchActivity(act, activity, new Intent());
			break;
		case R.id.online_l:
			activity = ChatPeopleInfoAct.class;
			LoadingTool.launchActivity(act, activity, new Intent());
			break;
		case R.id.gongguo_l:
//			activity = Msecond.class;
//			LoadingTool.launchActivity(act, activity, new Intent());
			break;
		case R.id.feedback_l:
			activity = FeedbackAct.class;
			LoadingTool.launchActivity(act, activity, new Intent());
			break;
		case R.id.msgremind_l:
			activity = ChatNotifySetAct.class;
			LoadingTool.launchActivity(act, activity, new Intent());
			break;
		case R.id.textsize_l:

			break;
		case R.id.textbubble_l:

			break;
		case R.id.del_l:
			if (!SharedPreferenceUtil.getDeleteChatmsgWarnning()) {
				if (warningDlg == null) {
					warningDlg = DialogManage
							.showWarningDlg(
									act,
									R.string.warning,
									R.string.del_chatmsg_warning,
									R.string.no_tip,
									R.string.cancel,
									R.string.continue_tip,
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (dialog instanceof WarningDlg
													&& ((WarningDlg) dialog)
															.isCheck()) {
												SharedPreferenceUtil
														.saveDeleteChatmsgWarnning(true);
											}
											dialog.dismiss();
											ChatMsgManage.instance()
													.clearchatMsg();
											if (act != null
													&& !act.isFinishing()) {
												act.clearChatmag();
												ToastUtil
														.showMessage("聊天室记录已清空");
											}

										}
									}, true);
				} else {
					warningDlg.show();
				}
			} else {
				ChatMsgManage.instance().clearchatMsg();
				if (act != null && !act.isFinishing()) {
					act.clearChatmag();
					ToastUtil.showMessage("聊天室记录已清空");
				}
			}
			break;
		case R.id.exit_l:
			act.finish();
			break;
		default:
			break;
		}
	}

}
