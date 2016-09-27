package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.adapter.DownBookAdapter.ViewHolder;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ChatMsg;

public class FriendMsgAdapter extends BaseAdapter {
	private static final int LEFT = 0;
	private static final int RIGHT = LEFT + 1;
	ArrayList<ChatMsg> data;
	Context context;
	LayoutInflater mInflater;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;

	public FriendMsgAdapter(Context context) {
		data = new ArrayList<ChatMsg>();
		mInflater = LayoutInflater.from(context);
		RequestQueue mQueue = Volley.newRequestQueue(context);
		LruImageCache lruImageCache = LruImageCache.instance(context
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	public void upData(ArrayList<ChatMsg> data) {
		if (this.data != data) {
			this.data.clear();
			this.data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	public void addMsg(ChatMsg msg) {
		if (data.size() >= 50) {
			data.remove(0);
		}
		if (!data.contains(msg))
			data.add(msg);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public int getItemViewType(int position) {
		return data.get(position).getSuid() == UserInfo.instance().getUid() ? RIGHT
				: LEFT;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		NetworkImageView icon;
		TextView msg;
		TextView time;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case LEFT:
				convertView = mInflater.inflate(R.layout.friend_left_msg_item,
						null);
				break;
			case RIGHT:
				convertView = mInflater.inflate(R.layout.friend_right_msg_item,
						null);

				break;
			}
			holder.icon = (NetworkImageView) convertView
					.findViewById(R.id.icon);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.msg = (TextView) convertView.findViewById(R.id.msg);
			holder.icon.setDefaultImageResId(R.drawable.user_defa_icon);
			holder.icon.setErrorImageResId(R.drawable.user_defa_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChatMsg item = data.get(position);
		String iconUrl = item.getIcon();
		if (!TextUtils.isEmpty(iconUrl))
			holder.icon.setImageUrl(urlHead + iconUrl, imageLoader);

		holder.time.setText(com.icloud.wrzjh.base.utils.TextUtils
				.secToMDHMS(item.getDateline()));
		if (TextUtils.isEmpty(item.getMsg()))
			holder.msg.setText("");
		else
			holder.msg.setText(Html.fromHtml(item.getMsg()));
		return convertView;
	}

}
