package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.entity.FriendItem;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.OtherUser;

public class FriendsListAdapter extends BaseAdapter {
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	ArrayList<FriendItem> userInfos;
	Activity act;

	public FriendsListAdapter(Activity act) {
		this.userInfos = new ArrayList<FriendItem>();
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	protected Drawable getDrawable(int id) {
		Drawable drawable = act.getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		return drawable;
	}

	public void upData(ArrayList<FriendItem> userInfos) {
		if (this.userInfos != userInfos) {
			this.userInfos.clear();
			this.userInfos.addAll(userInfos);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return userInfos.size();
	}

	@Override
	public OtherUser getItem(int arg0) {
		return userInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			LayoutInflater mInflater = LayoutInflater.from(act);
			view = mInflater.inflate(R.layout.friends_item, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.icon);
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.go = view.findViewById(R.id.go);
			holder.tip = view.findViewById(R.id.msgTip);
			holder.icon.setDefaultImageResId(R.drawable.user_defa_icon);
			holder.icon.setErrorImageResId(R.drawable.user_defa_icon);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		FriendItem item = userInfos.get(position);
		holder.name.setText(item.getNick());
		holder.tip.setVisibility(item.isSendChatMsg ? View.VISIBLE : View.GONE);
		holder.go.setTag(position);
		holder.icon.setTag(position);
		String iconUrl = item.getIcon();
		if (!TextUtils.isEmpty(iconUrl))
			holder.icon.setImageUrl(urlHead + iconUrl, imageLoader);

		return view;
	}

	class ViewHolder {
		NetworkImageView icon;
		View tip;
		TextView name;
		TextView signature;
		View go;
	}

	public void del(int postion) {
		if (postion >= 0 && postion < userInfos.size()) {
			userInfos.remove(postion);
			this.notifyDataSetChanged();
		}
	}

	public void clearTip(int postion) {
		if (postion >= 0 && postion < userInfos.size()) {
			userInfos.get(postion).isSendChatMsg = false;
			this.notifyDataSetChanged();
		}
	}

	public void setTip(long uid) {
		FriendItem item;
		for (int i = 0; i < userInfos.size(); i++) {
			item = userInfos.get(i);
			if (item.getUid() == uid) {
				item.isSendChatMsg = true;
				this.notifyDataSetChanged();
				return;
			}
		}

	}

}
