package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.FriendsInfoAct;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleFeedback;

public class VedioCommentsAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	VedioInfoAct act;
	Article Item;
	ArrayList<ArticleFeedback> datas;
	final FooterManage footerManage;

	public VedioCommentsAdapter(VedioInfoAct act, Article Item,
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
		}

		public void setView(ArticleFeedback item) {
			icon.setTag(item);
			icon.setImageUrl(urlHead + item.getIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.user_defa_icon);
			icon.setErrorImageResId(R.drawable.user_defa_icon);
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
		TextView aAuthor;
		TextView content_txt;
		TextView content_switch;
		EditText send_msg;

		String aDesc;

		public View findViewById(int id) {
			return view.findViewById(id);
		}

		public VoiceInfoHolder(View view) {
			super(view);
			this.view = view;
			icon = (NetworkImageView) findViewById(R.id.icon);
			name = (TextView) findViewById(R.id.name);
			aAuthor = (TextView) findViewById(R.id.aAuthor);
			content_txt = (TextView) findViewById(R.id.content_txt);
			content_switch = (TextView) findViewById(R.id.content_switch);
			send_msg = (EditText) findViewById(R.id.send_msg);

		}

		public void setView(Article Item) {
			send_msg.setOnEditorActionListener(this);
			content_switch.setOnClickListener(this);
			content_switch
					.setOnTouchListener(ViewUtils.instance().onTouchListener);

			Resources res = act.getResources();
			if (!TextUtils.isEmpty(Item.getAName())) {
				name.setText(Item.getAName());
			}
			icon.setImageUrl(urlHead + Item.getAIcon(), imageLoader);
			icon.setDefaultImageResId(R.drawable.icon_default_voice);
			icon.setErrorImageResId(R.drawable.icon_default_voice);

			if (!TextUtils.isEmpty(Item.getAAuthor()))
				aAuthor.setText(res.getString(R.string.aAuthor_to,
						Item.getAAuthor()));

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
			}
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
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String msg = v.getText().toString();
			switch (actionId) {
			case EditorInfo.IME_NULL:
				break;
			case EditorInfo.IME_ACTION_SEND:
				if (ToolUtils.isNetworkAvailableTip(act))
					if (!TextUtils.isEmpty(msg) && msg.length() >= 6) {
						/** 先判断是否登陆 **/
						if (UserInfo.instance().isLogin()) {
							HttpUtils.addFeedback(Item.getAId(), msg,
									VoiceInfoHolder.this, VoiceInfoHolder.this);
						} else {
							LoadingTool.launchActivity(act, LoginAct.class);
						}
					} else {
						ToastUtil.showMessage("留言长度至少为6位");
					}
				break;
			case EditorInfo.IME_ACTION_DONE:

				break;
			}
			return true;
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
					VedioCommentsAdapter.this.notifyDataSetChanged();
					IoUtils.instance().saveArticleFeedback(down);

				}
			} catch (Exception e) {
			}

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
			view = LayoutInflater.from(act).inflate(R.layout.vedio_info_view,
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
