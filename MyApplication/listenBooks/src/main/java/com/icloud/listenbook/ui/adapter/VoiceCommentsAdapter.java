package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.VedioCommentsAdapter.VoiceInfoHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.FriendsInfoAct;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.Collect;

public class VoiceCommentsAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	Activity act;
	Article Item;
	ArrayList<ArticleFeedback> datas;
	final FooterManage footerManage;
	OnClickListener gotoListener;

	public VoiceCommentsAdapter(Activity act, Article Item,
			ArrayList<ArticleFeedback> datas) {
		this.act = act;
		this.Item = Item;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
		footerManage = new FooterManage();
		this.datas = datas;
	}

	public void addGotoListener(OnClickListener gotoListener) {
		this.gotoListener = gotoListener;
	}

	public void upDatas(ArrayList<ArticleFeedback> datas) {
		if (this.datas != datas) {
			this.datas.clear();
			this.datas.addAll(datas);
		}
		this.notifyDataSetChanged();
	}

	public void setArticle(Article article) {
		this.Item = article;
		this.notifyDataSetChanged();
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView name;
		TextView msg;
		TextView time;

		public ItemHolder(View view) {
			super(view);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			name = (TextView) view.findViewById(R.id.name);
			msg = (TextView) view.findViewById(R.id.msg);
			time = (TextView) view.findViewById(R.id.time);
			icon.setOnClickListener(this);
			icon.setDefaultImageResId(R.drawable.user_defa_icon);
			icon.setErrorImageResId(R.drawable.user_defa_icon);
		}

		public void setView(ArticleFeedback item) {
			icon.setImageUrl(urlHead + item.getIcon(), imageLoader);
			icon.setTag(item);
			time.setText(item.getDateline());
			name.setText(item.getNick());
			msg.setText(item.getMsg());
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.icon:
				if (UserInfo.instance().isLogin()&&v.getTag() != null
						&& (v.getTag() instanceof ArticleFeedback)) {
					ArticleFeedback Item = (ArticleFeedback) v.getTag();
					Intent Intent = new Intent();
					Intent.putExtra("data", JsonUtils.toJson(Item));
					LoadingTool.launchActivity(act, FriendsInfoAct.class,
							Intent);

				}
				break;
			}
		}
	}

	public class VoiceInfoHolder extends RecyclerView.ViewHolder implements
			OnClickListener, TextView.OnEditorActionListener,
			Listener<JSONObject>, ErrorListener {
		View view;
		NetworkImageView icon;
		TextView name;
		TextView type;
		TextView aAuthor;
		TextView heat;
		TextView vip;
		TextView time;
		TextView content_txt;
		TextView content_switch;
		EditText send_msg;
		View goPlay;
		TextView collection;
		View share;
		String aDesc;

		public View findViewById(int id) {
			return view.findViewById(id);
		}

		public VoiceInfoHolder(View view) {
			super(view);
			this.view = view;
			icon = (NetworkImageView) findViewById(R.id.icon);
			name = (TextView) findViewById(R.id.name);
			type = (TextView) findViewById(R.id.type);
			aAuthor = (TextView) findViewById(R.id.aAuthor);
			heat = (TextView) findViewById(R.id.heat);
			vip = (TextView) findViewById(R.id.vip);
			time = (TextView) findViewById(R.id.time);
			content_txt = (TextView) findViewById(R.id.content_txt);
			content_switch = (TextView) findViewById(R.id.content_switch);
			send_msg = (EditText) findViewById(R.id.send_msg);

			goPlay = findViewById(R.id.goPlay);
			collection = (TextView) findViewById(R.id.collection);
			share = findViewById(R.id.share);
		}

		public void setView(Article Item) {
			send_msg.setOnEditorActionListener(this);
			content_switch.setOnClickListener(this);
			content_switch
					.setOnTouchListener(ViewUtils.instance().onTouchListener);
			goPlay.setOnClickListener(this);
			goPlay.setOnTouchListener(ViewUtils.instance().onTouchListener);
			collection.setOnClickListener(this);
			collection.setOnTouchListener(ViewUtils.instance().onTouchListener);
			setCollect();
			share.setTag(Item);
			share.setOnClickListener(this);
			share.setOnTouchListener(ViewUtils.instance().onTouchListener);
			vip.setOnClickListener(this);
			vip.setOnTouchListener(ViewUtils.instance().onTouchListener);

			Resources res = act.getResources();
			if (!TextUtils.isEmpty(Item.getAName())) {
				name.setText(Item.getAName());
			}
			icon.setImageUrl(urlHead + Item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_voice);
			icon.setErrorImageResId(R.drawable.icon_default_voice);
			if (!TextUtils.isEmpty(Item.getSubCaterory())) {
				type.setText(res.getString(R.string.type_to,
						Item.getSubCaterory()));
			}
			String statusStr;
			// 1:更新中 2：已完结 3：下线',
			// switch (Item.getStatus()) {
			// default:
			// case 3:
			// statusStr = "下线";
			// break;
			// case 2:
			// statusStr = "已完结";
			// break;
			// case 1:
			// statusStr = "更新中";
			// break;
			// }
			// status.setText(res.getString(R.string.status_to, statusStr));
			if (!TextUtils.isEmpty(Item.getAAuthor()))
				aAuthor.setText(res.getString(R.string.aAuthor_to,
						Item.getAAuthor()));

			// if (!TextUtils.isEmpty(Item.getBroadAuthor()))
			// musicaAuthor.setText(res.getString(R.string.musicaAuthor_to,
			// Item.getBroadAuthor()));

			if (!TextUtils.isEmpty(Item.getDateline()))
				time.setText(res.getString(R.string.time_to, Item.getDateline()));
			Integer clickConut = Item.getClickConut();
			if (clickConut == null)
				clickConut = 0;
			heat.setText(res.getString(R.string.heat_to, clickConut));

			aDesc = Item.getADesc();
			if (!TextUtils.isEmpty(aDesc))
				content_txt.setText(aDesc.length() > 50 ? aDesc
						.substring(0, 50) : aDesc);
			if (TextUtils.isEmpty(aDesc) || aDesc.length() <= 50)
				content_switch.setVisibility(View.GONE);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.content_switch:
				swicthDesc();
				break;
			case R.id.collection:
				collect();
				break;
			case R.id.goPlay:
				if (gotoListener != null)
					gotoListener.onClick(v);
				break;
			case R.id.share:
				Article item = (Article) v.getTag();
				ToolUtils.shareDefa(act, item.getAName());
				break;
			}
		}

		public void setCollect() {
			int drawableId = R.drawable.icon_collection;
			if (ToolUtils.isCollect(Item.getAId())) {
				drawableId = R.drawable.icon_collection;
				collection.setText(R.string.collect);
			} else {
				drawableId = R.drawable.icon_collection_ing;
				collection.setText(R.string.un_collect);
			}
			Drawable icon = act.getResources().getDrawable(drawableId);
			icon.setBounds(0, 0, icon.getMinimumWidth(),
					icon.getMinimumHeight());
			collection.setCompoundDrawables(icon, null, null, null);
		}

		public void collect() {
			ToolUtils.collect(act, Item.getAId());
			setCollect();
		}

		public void swicthDesc() {
			String str = content_txt.getText().toString().trim();
			if (str.equals(aDesc)) {
				content_txt.setText(aDesc.substring(0, 50));
				Drawable icon = act.getResources().getDrawable(
						R.drawable.icon_arrow_down);
				icon.setBounds(0, 0, icon.getMinimumWidth(),
						icon.getMinimumHeight());
				content_switch.setCompoundDrawables(icon, null, null, null);
			} else {
				content_txt.setText(aDesc);
				Drawable icon = act.getResources().getDrawable(
						R.drawable.icon_arrow_up);
				icon.setBounds(0, 0, icon.getMinimumWidth(),
						icon.getMinimumHeight());
				content_switch.setCompoundDrawables(icon, null, null, null);
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			ToastUtil.showMessage(R.string.comments_erro);

		}

		@Override
		public void onResponse(JSONObject response) {
			try {
				Log.e("addFeedback", response.toString());
				int result = response.optInt("result", -1);
				String msg = response.optString("msg");
				if (result == 0) {
					send_msg.setText("");
				}
				if (!TextUtils.isEmpty(msg))
					ToastUtil.showMessage(msg);
				JSONObject item = response.optJSONObject("list");
				if (item != null) {
					ArticleFeedback down = JsonUtils.toArticleFeedback(
							Item.getAId(), item);
					datas.add(0, down);
					VoiceCommentsAdapter.this.notifyDataSetChanged();
					IoUtils.instance().saveArticleFeedback(down);

				}
			} catch (Exception e) {
			}

		}

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String msg = v.getText().toString();
			switch (actionId) {
			case EditorInfo.IME_NULL:
				break;
			case EditorInfo.IME_ACTION_SEND:
				if (ToolUtils.isNetworkAvailableTip(act))
					if (!TextUtils.isEmpty(msg)) {
						/** 先判断是否登陆 **/
						if (UserInfo.instance().isLogin()) {
							HttpUtils.addFeedback(Item.getAId(), msg,
									VoiceInfoHolder.this, VoiceInfoHolder.this);
						} else {
							LoadingTool.launchActivity(act, LoginAct.class);
						}
					}
				break;
			case EditorInfo.IME_ACTION_DONE:

				break;
			}
			return true;
		}
	}

	public boolean isFooter(int pos) {
		return pos > datas.size() || datas.size() == 0;
	}

	@Override
	public int getItemCount() {
		return datas.size() + 2;
	}

	@Override
	public int getItemViewType(int pos) {
		if (pos == 0) {
			return Type.TABLE_TITLE;
		} else if (isFooter(pos)) {
			return Type.TABLE_FOOTER;
		}
		return Type.TABLE_ITEM;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (pos == 0) {
			VoiceInfoHolder holder = (VoiceInfoHolder) viewHolder;
			holder.setView(Item);
		} else if (isFooter(pos)) {
			footerManage.set();
		} else {
			pos -= 1;
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(datas.get(pos));
		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_TITLE) {
			view = LayoutInflater.from(act).inflate(R.layout.voice_info_view,
					parent, false);
			return new VoiceInfoHolder(view);
		}
		if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(R.layout.footer_item,
					parent, false);
			FooterHolder footerHolder = new FooterHolder(view);
			footerManage.footer = footerHolder;
			return footerHolder;
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.voice_comments_item, parent, false);
			return new ItemHolder(view);
		}
	}

	public void setStatus(int status) {
		if (footerManage != null) {
			footerManage.setStatus(status);
		}
	}

	public int getStatus() {
		if (footerManage != null) {
			return footerManage.getStatus();
		}
		return FooterManage.FREE;
	}
}
