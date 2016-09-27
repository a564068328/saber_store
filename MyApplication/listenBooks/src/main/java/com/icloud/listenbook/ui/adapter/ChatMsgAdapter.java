package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.DownBookAdapter.ViewHolder;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.ui.chipAct.FriendsInfoAct;
import com.icloud.listenbook.ui.popup.PopuItem;
import com.icloud.listenbook.ui.popup.PopuJar;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.listenbook.unit.UIUtils;
import com.icloud.listenbook.unit.UserIconManage;
import com.icloud.wrzjh.base.utils.ClipboardUtil;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.ChatMsg;

public class ChatMsgAdapter extends BaseAdapter implements OnClickListener {
	private static final int LEFT = 0;
	private static final int RIGHT = LEFT + 1;
	private static final int ID_CHECK_ALL = 1;
	private static final int ID_COPY = ID_CHECK_ALL + 1;
	private static final int ID_DELETE = ID_COPY + 1;
	public ArrayList<ChatMsg> data;
	Activity act;
	LayoutInflater mInflater;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	private PopuJar mPopu;
	private int PopupPosition;

	public ChatMsgAdapter(final Activity act, ImageLoader imageLoader2) {
		this.act = act;
		data = new ArrayList<ChatMsg>();
		mInflater = LayoutInflater.from(act);
		// RequestQueue mQueue = Volley.newRequestQueue(act);
		// LruImageCache lruImageCache = LruImageCache.instance(act
		// .getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = imageLoader2;

		// 对消息textview的复制和删除操作
		// PopuItem checkAllItem = new PopuItem(ID_CHECK_ALL, "全选", null);
		PopuItem copyItem = new PopuItem(ID_COPY, "复制", null);
		PopuItem deleteItem = new PopuItem(ID_DELETE, "删除", null);
		mPopu = new PopuJar(act, PopuJar.HORIZONTAL);
		// mPopu.addPopuItem(checkAllItem);
		mPopu.addPopuItem(copyItem);
		mPopu.addPopuItem(deleteItem);
		mPopu.setOnPopuItemClickListener(new PopuJar.OnPopuItemClickListener() {
			@Override
			public void onItemClick(PopuJar source, int pos, int actionId) {
//				PopuItem PopuItem = mPopu.getPopuItem(pos);
				ChatMsg chatMsg = data.get(PopupPosition);
				if(chatMsg==null)
					return;
				// here we can filter which action item was clicked with pos or
				// actionId parameter
				if (actionId == ID_DELETE) {
					IoUtils.instance().removeCharMsg(chatMsg);
					data.remove(chatMsg);
					ChatMsgAdapter.this.notifyDataSetChanged();
				} else if (actionId == ID_COPY) {
					ClipboardUtil.copy(chatMsg.getMsg(), act);
				} else {

				}
			}
		});
		mPopu.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});
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
		TextView name;
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
				convertView = mInflater.inflate(R.layout.chat_left_msg_item,
						null);
				break;
			case RIGHT:
				convertView = mInflater.inflate(R.layout.chat_right_msg_item,
						null);

				break;
			}
			holder.icon = (NetworkImageView) convertView
					.findViewById(R.id.icon);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.msg = (TextView) convertView.findViewById(R.id.msg);
			holder.msg.setTextSize(14f);
			holder.msg.setAutoLinkMask(Linkify.WEB_URLS
					| Linkify.EMAIL_ADDRESSES);
			holder.msg.setMovementMethod(LinkMovementMethod.getInstance());
			holder.icon.setDefaultImageResId(R.drawable.user_defa_icon);
			holder.icon.setErrorImageResId(R.drawable.user_defa_icon);
			holder.icon.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChatMsg item = data.get(position);
		holder.icon.setTag(position);
		String iconUrl = item.getIcon();
		if (!TextUtils.isEmpty(iconUrl))
			holder.icon.setImageUrl(urlHead + iconUrl, imageLoader);
		else {
			iconUrl = UserIconManage.instance().getUserIcon(item.getSuid());
			if (!TextUtils.isEmpty(iconUrl)) {
				item.setIcon(iconUrl);
				holder.icon.setImageUrl(urlHead + iconUrl, imageLoader);
			}
		}
		if (TextUtils.isEmpty(item.getName()))
			holder.name.setText("");
		else
			holder.name.setText(item.getName());

		holder.time.setText(com.icloud.wrzjh.base.utils.TextUtils
				.secToMDHMS(item.getDateline()));
		if (TextUtils.isEmpty(item.getMsg()))
			holder.msg.setText("");
		else
			holder.msg.setText(item.getMsg());
		holder.msg.setTag(position);
		holder.msg.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mPopu.show(v);
				PopupPosition = (Integer) v.getTag();
				return true;
			}
		});
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.icon:
			int position = (Integer) v.getTag();
			ChatMsg item = data.get(position);
			ArticleFeedback user = new ArticleFeedback();
			user.setUserId(item.getSuid());
			user.setNick(item.getName());
			Intent Intent = new Intent();
			Intent.putExtra("data", JsonUtils.toJson(user));
			LoadingTool.launchActivity(act, FriendsInfoAct.class, Intent);
			break;
		}

	}

}
