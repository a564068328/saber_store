package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.OtherUser;

public class FriendsInfoAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject>, Response.ErrorListener {
	View back, sendMessage;
	OtherUser user;
	TextView userName, userState, signature, uid;
	NetworkImageView userIcon;
	ImageLoader imageLoader;
	String urlHead;
	TextView collectConut, friendsConut;

	@Override
	public void init() {
		String value = this.getIntent().getStringExtra("data");
		ArticleFeedback Item = JsonUtils.fromJson(value, ArticleFeedback.class);
		user = new OtherUser();
		user.setNick(Item.getNick());
		user.setUid(Item.getUserId());
		user.setIcon(Item.getIcon());
		user.setIsfriend(false);
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public int getLayout() {
		return R.layout.act_friends_info;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		signature = (TextView) findViewById(R.id.signature);
		sendMessage = findViewById(R.id.sendMessage);
		userState = (TextView) findViewById(R.id.userState);
		userName = (TextView) findViewById(R.id.userName);
		userIcon = (NetworkImageView) findViewById(R.id.userIcon);
		collectConut = (TextView) findViewById(R.id.collectConut);
		friendsConut = (TextView) findViewById(R.id.friendsConut);
		uid = (TextView) findViewById(R.id.uid);

	}

	@Override
	public void getDatas() {
		super.getDatas();
		HttpUtils.getUserInfo(user.getUid(), this, this);
		userIcon.setDefaultImageResId(R.drawable.user_defa_icon);
		userIcon.setErrorImageResId(R.drawable.user_defa_icon);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		userName.setText(user.getNick());
		uid.setText(String.format(this.getString(R.string.uidStr),
				user.getUid()));

	}

	protected void upUserInfo() {
		userIcon.setImageUrl(urlHead + user.getIcon(), imageLoader);
		userName.setText(user.getNick());
		userState.setVisibility(View.VISIBLE);
		userState.setText(user.getIsfriend() ? R.string.del_friend
				: R.string.add_friend);
		String _signature = user.getSignature();
		if (!TextUtils.isEmpty(_signature))
			signature.setText(String.format(
					this.getResources().getString(R.string.signature_tip),
					_signature));
		sendMessage
				.setVisibility(UserInfo.instance().getUid() != user.getUid() ? View.VISIBLE
						: View.INVISIBLE);
		userState
				.setVisibility(UserInfo.instance().getUid() != user.getUid() ? View.VISIBLE
						: View.INVISIBLE);

		collectConut.setText(String.valueOf(user.getCollectCount()));
		friendsConut.setText(String.valueOf(user.getFriendsCount()));
		sendMessage.setVisibility(user.getIsfriend()
				&& UserInfo.instance().getUid() != user.getUid() ? View.VISIBLE
				: View.INVISIBLE);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		sendMessage.setOnClickListener(this);
		sendMessage.setOnTouchListener(ViewUtils.instance().onTouchListener);
		userState.setOnClickListener(this);
		userState.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	public void sendMessage() {
		OtherUser otherUser = user;
		Intent Intent = new Intent();
		Intent.putExtra("data", JsonUtils.toJson(otherUser));
		LoadingTool.launchActivity(this, FriendMsgAct.class, Intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.sendMessage:
			sendMessage();
			break;
		case R.id.back:
			this.finish();
			break;
		case R.id.userState:
			if (user.getIsfriend())
				delFriend();
			else
				addFriend();
			break;
		}
	}

	protected void addFriend() {
		HttpUtils.friendAdd(user.getUid(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int res = response.optInt("result", -1);
					LogUtil.e(Tag, "addFriend:" + response.toString());
					if (res == 0) {
						user.setIsfriend(true);
						UserInfo.instance().setFriendsCount(
								response.optInt("friendsCount"));
						ToastUtil.showMessage("加为好友成功");
						upUserInfo();

					} else {
						String msg = response.optString("msg");
						if (!android.text.TextUtils.isEmpty(msg)) {
							ToastUtil.showMessage(msg);
						}
					}
				} catch (Exception e) {
				}
			}
		}, null);
	}

	protected void delFriend() {
		HttpUtils.friendDel(user.getUid(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int res = response.optInt("result", -1);
					LogUtil.e(Tag, "addFriend:" + response.toString());
					if (res == 0) {
						user.setIsfriend(false);
						UserInfo.instance().setFriendsCount(
								response.optInt("friendsCount"));
						ToastUtil.showMessage("删除好友成功");
						upUserInfo();

					} else {
						String msg = response.optString("msg");
						if (!android.text.TextUtils.isEmpty(msg)) {
							ToastUtil.showMessage(msg);
						}
					}
				} catch (Exception e) {
				}

			}
		}, null);
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "getUserInfo:" + response.toString());
			if (res == 0) {
				user = JsonUtils.toOtherUser(response);
				upUserInfo();
			} else {
				String msg = response.optString("msg");
				if (!android.text.TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
			}
		} catch (Exception e) {
		}
	}

}
