package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.CircleNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.chipAct.FriendsInfoAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.DateKit;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.ChatPeopleInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatPeopleInfoAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	final String TAG = getClass().getName();
	List<ChatPeopleInfo> list;
	ImageLoader imageLoader;
	Activity act;
	String urlHead;

	public ChatPeopleInfoAdapter(Activity act, ImageLoader imageLoader) {
		this.act = act;
		this.imageLoader = imageLoader;
		urlHead = ServerIps.getLoginAddr();
		list = new ArrayList<ChatPeopleInfo>();
	}

	@Override
	public int getItemCount() {
		// LogUtil.e(TAG, "list.size()"+list.size());
		return list.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		if (viewholder instanceof itemViewHolder) {
			itemViewHolder holder = (itemViewHolder) viewholder;
			holder.setView(position);
		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup root, int viewType) {
		View view = LayoutInflater.from(act).inflate(
				R.layout.chat_people_info_item, root, false);
		return new itemViewHolder(view);
	}

	public class itemViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		CircleNetworkImageView icon = null;
		TextView name = null;
		ImageView sex = null;
		TextView signature;
		TextView date;
		View iv_extend;
		TextView friend;

		public itemViewHolder(View view) {
			super(view);
			if (view != null) {
				icon = (CircleNetworkImageView) view.findViewById(R.id.icon);
				name = (TextView) view.findViewById(R.id.name);
				sex = (ImageView) view.findViewById(R.id.sex);
				signature = (TextView) view.findViewById(R.id.signature);
				date = (TextView) view.findViewById(R.id.date);
				friend = (TextView) view.findViewById(R.id.friend);
				iv_extend = view.findViewById(R.id.iv_extend);
				iv_extend.setOnClickListener(this);
			}
		}

		public void setView(int position) {

			ChatPeopleInfo info = list.get(position);
			// LogUtil.e(TAG, "ChatPeopleInfo"+info.getDate());
			icon.setDefaultImageResId(R.drawable.user_defa_icon);
			icon.setErrorImageResId(R.drawable.user_defa_icon);
			if (!TextUtils.isEmpty(info.getIcon())) {
				icon.setImageUrl(urlHead + info.getIcon(), imageLoader);
			}
			if (!TextUtils.isEmpty(info.getNick())) {
				name.setText(info.getNick());
			} else {
				name.setText(info.getArea() + "网友" + info.getUid());
			}
			if (!TextUtils.isEmpty(info.getSex())) {

			} else {
				sex.setVisibility(View.INVISIBLE);
			}
			if (!TextUtils.isEmpty(info.getSignature())) {
				signature.setText("签名:" + info.getSignature());
			} else {
				signature.setText("签名:" + "暂无");
			}
			if (info.getUid() != UserInfo.instance().getUid()) {
				if (info.getIsfriend()) {
					friend.setText("好友");
				} else {
					friend.setVisibility(View.INVISIBLE);
				}
			} else {
				friend.setText("自己"); 
			}
			date.setText("上线时间:" + DateKit.friendlyFormat(info.getDate()));
			iv_extend.setTag(info);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.iv_extend:
				ChatPeopleInfo item = (ChatPeopleInfo) v.getTag();
				ArticleFeedback user = new ArticleFeedback();
				user.setUserId(item.getUid());
				user.setNick(item.getNick());
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(user));
				LoadingTool.launchActivity(act, FriendsInfoAct.class, Intent);
				break;
			}
		}
	}

	public void upData(List<ChatPeopleInfo> upDatalist) {
		list.clear();
		list.addAll(upDatalist);
	}

}
