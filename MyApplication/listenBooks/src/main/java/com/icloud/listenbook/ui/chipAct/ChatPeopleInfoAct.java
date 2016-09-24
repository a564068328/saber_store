package com.icloud.listenbook.ui.chipAct;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.BaseFragmentActivity;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.recyclerplus.DividerItemDecoration;
import com.icloud.listenbook.ui.adapter.ChatPeopleInfoAdapter;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ChatPeopleInfo;

public class ChatPeopleInfoAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	static final String TAG = "com.icloud.listenbook.ui.chipAct.ChatPeopleInfoAct";
	View back;
	TextView onlinecount;
	RelativeLayout ll_progress;
	RecyclerView recyclerview;
	LinearLayoutManager manager;
	ChatPeopleInfoAdapter adapter;
	ImageLoader imageLoader;
	int totalCount;
	int currentCount;

	@Override
	public void init() {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		LruImageCache lruImageCache = LruImageCache.instance(this
				.getApplicationContext());
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	@Override
	public int getLayout() {

		return R.layout.act_chat_people_info;
	}

	public void findViews() {
		back = findViewById(R.id.back);
		onlinecount=(TextView) findViewById(R.id.onlinecount);
		ll_progress = (RelativeLayout) findViewById(R.id.ll_progress);
		recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		manager = new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		adapter = new ChatPeopleInfoAdapter(this, imageLoader);
		recyclerview.setLayoutManager(manager);
		recyclerview.setAdapter(adapter);
		recyclerview.setHasFixedSize(true);
		recyclerview.addItemDecoration(new DividerItemDecoration(this,
				DividerItemDecoration.VERTICAL_LIST));
	}

	private void initData() {
		List<ChatPeopleInfo> list = IoUtils.instance().getAllChatPeopleInfo();
		if (null == list)
			return;
		ChatPeopleInfo userinfo = IoUtils.instance().getChatPeopleInfo(
				UserInfo.instance().getUid());
		if (userinfo == null) {
			userinfo = new ChatPeopleInfo();
			userinfo.setUid(UserInfo.instance().getUid());
			userinfo.setDate(new Date(System.currentTimeMillis()));
		}
		userinfo.setNick(UserInfo.instance().getNick());
		userinfo.setIcon(UserInfo.instance().getIcon());
		userinfo.setSignature(UserInfo.instance().getSignature());
		userinfo.setArea(UserInfo.instance().getArea());
		userinfo.setIsfriend(true);
		userinfo.setInterval_count((long) 1);
		// LogUtil.e(TAG, "ChatPeopleInfo"+userinfo.getDate());
		IoUtils.instance().saveChatPeopleInfo(userinfo);
		int count = 0;
		for (ChatPeopleInfo info : list) {
			if (info.getInterval_count() != 1
					&& info.getUid() != UserInfo.instance().getUid()) {
				// LogUtil.e(TAG, "HttpUtils.getUserInfo" + info.getUid());
				HttpUtils.getUserInfo(info.getUid(), this, this);
				count++;
			}
		}
		totalCount = count;
		if (totalCount == 0) {
			ll_progress.setVisibility(View.INVISIBLE);
			list = IoUtils.instance().getAllChatPeopleInfo();
			onlinecount.setText("当前在线人数:"+list.size()+"人");
			// LogUtil.e(TAG, "list.size()" + list.size());
			adapter.upData(list);
			adapter.notifyDataSetChanged();
			currentCount = 0;
			list.clear();
		}

	}

	@Override
	public void getDatas() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		ll_progress.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onResponse(JSONObject response) {
		int res = response.optInt("result", -1);
		if (res == 0) {
			synchronized (response) {
				LogUtil.e(TAG, response.toString());
				currentCount++;
				long uid = response.optLong("uid");
				ChatPeopleInfo info = IoUtils.instance().getChatPeopleInfo(uid);
				if (null == info) {
					return;
				}
				info.setNick(response.optString("nick"));
				info.setIcon(response.optString("icon"));
				info.setSignature(response.optString("signature"));
				info.setArea(response.optString("area"));
				info.setIsfriend(response.optInt("isfriend") == 1);
				info.setInterval_count((long) 1);
				// info.setFriendsCount(response.optInt("friendsCount"));
				// info.setCollectCount(response.optInt("collectCount"));
				IoUtils.instance().saveChatPeopleInfo(info);
				// LogUtil.e(TAG, "currentCount:" + currentCount + "totalCount:"
				// + totalCount);
				if (currentCount == totalCount) {
					ll_progress.setVisibility(View.INVISIBLE);
					List<ChatPeopleInfo> list = IoUtils.instance()
							.getAllChatPeopleInfo();
					onlinecount.setText("当前在线人数:"+list.size()+"人");
					if (!ChatPeopleInfoAct.this.isFinishing()) {
						adapter.upData(list);
						adapter.notifyDataSetChanged();
					}
					currentCount = 0;
					list.clear();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
