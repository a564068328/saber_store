package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.util.LongSparseArray;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.InputDlg;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.FriendsListAdapter;
import com.icloud.listenbook.ui.adapter.entity.FriendItem;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.OtherUser;
import com.yunva.live.sdk.lib.channel.listener.OnClickDialogListener;

public class FriendsListAct extends BaseActivity implements
		Listener<JSONObject>, ErrorListener, OnClickListener,
		OnItemLongClickListener, AdapterView.OnItemClickListener {
	ListView list;
	ProgressDialog delProgeress;
	ProgressDialog addProgeress;
	FriendsListAdapter adapter;
	ArrayList<FriendItem> userInfos;
	View back;
	View addFriend;

	@Override
	public void init() {
		adapter = new FriendsListAdapter(this);
		userInfos = new ArrayList<FriendItem>();
		delProgeress = DialogManage.ProgressDialog(this, "提示", "删除中...");
		addProgeress = DialogManage.ProgressDialog(this, "提示", "添加...");
	}

	@Override
	public int getLayout() {
		return R.layout.act_friends_list;
	}

	@Override
	public void findViews() {
		list = (ListView) findViewById(R.id.list);
		back = findViewById(R.id.back);
		addFriend = findViewById(R.id.addFriend);
	}

	@Override
	public void setListeners() {
		list.setAdapter(adapter);
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setOnItemLongClickListener(this);
		list.setOnItemClickListener(this);
		addFriend.setOnClickListener(this);
		addFriend.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void setDatas() {
		super.setDatas();
	}

	@Override
	public void getDatas() {
		super.getDatas();
		HttpUtils.friendsList(this, this);
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "friendsList:" + response.toString());
			if (res == 0) {
				JSONArray list = response.optJSONArray("list");
				FriendItem otherUser;
				JSONObject json;
				userInfos.clear();
				LongSparseArray<Boolean> friendMsgTip = ChatMsgManage
						.instance().getFriendMsgTip();

				UserInfo.instance().setFriendsCount(list.length());
				for (int i = 0; i < list.length(); i++) {
					json = list.optJSONObject(i);
					otherUser = JsonUtils.toFriendItem(json);
					if (friendMsgTip.get(otherUser.getUid()) != null)
						otherUser.isSendChatMsg = true;
					if (!userInfos.contains(otherUser)) {
						userInfos.add(otherUser);
					}
				}
				adapter.upData(userInfos);

			} else {
				String msg = response.optString("msg");
				if (!android.text.TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
			}
		} catch (Exception e) {
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.addFriend:
			addFriend();
			break;
		}

	}

	InputDlg inputDlg;

	protected void addFriend() {
		if (inputDlg == null) {
			inputDlg = DialogManage.showInput(this, R.string.input_friend_uid,
					R.string.add,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (inputDlg != null) {
								String inputStr = inputDlg.getInput();
								if (!TextUtils.isEmpty(inputStr)) {
									try {
										addFriend(Long.valueOf(inputStr));
									} catch (Exception e) {
									}
								}
							}
						}
					}, R.string.cancel);
			inputDlg.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else
			inputDlg.show();
	}

	protected void addFriend(long uid) {
		if (addProgeress != null)
			addProgeress.show();
		HttpUtils.friendAdd(uid, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (addProgeress != null)
					addProgeress.dismiss();
				try {
					int res = response.optInt("result", -1);
					LogUtil.e(Tag, "addFriend:" + response.toString());
					if (res == 0) {
						UserInfo.instance().setFriendsCount(
								response.optInt("friendsCount"));
						ToastUtil.showMessage("加为好友成功");
						if (inputDlg != null)
							inputDlg.dismiss();
						HttpUtils.friendsList(FriendsListAct.this,
								FriendsListAct.this);

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

	protected void friendDel(final int pos) {
		delProgeress.show();
		OtherUser item = adapter.getItem(pos);
		HttpUtils.friendDel(item.getUid(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				delProgeress.dismiss();
				try {
					int res = response.optInt("result", -1);
					if (res == 0) {
						UserInfo.instance().setFriendsCount(
								response.optInt("friendsCount"));
						adapter.del(pos);

					}
				} catch (Exception e) {
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				delProgeress.dismiss();

			}
		});
	}

	protected void showDelDlg(final int pos) {
		DialogManage.showAlertDialog(this, R.string.tip, R.string.del_friend,
				R.string.cancel, R.string.del,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						friendDel(pos);
					}

				});

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
			long id) {
		showDelDlg(pos);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
		adapter.clearTip(pos);
		OtherUser otherUser = adapter.getItem(pos);
		Intent Intent = new Intent();
		Intent.putExtra("data", JsonUtils.toJson(otherUser));
		LoadingTool.launchActivity(this, FriendMsgAct.class, Intent);
	}

	protected void rReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ChatMsgManage.FRIEND_MSG_ADD_ACTION);
		this.registerReceiver(mReceiver, filter);
	}

	protected void uReceiver() {
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		rReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		uReceiver();
	}

	protected void upTip(long uid) {
		if (adapter != null && uid != -1) {
			adapter.setTip(uid);
		}
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			upTip(intent.getLongExtra("uid", -1));
		}

	};
}
