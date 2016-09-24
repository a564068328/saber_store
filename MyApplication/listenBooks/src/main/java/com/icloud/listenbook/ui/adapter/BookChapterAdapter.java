package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.view.CusView;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.DownLoadListener;
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.BookAct;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.unit.AccUtils;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;

public class BookChapterAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	Activity act;
	ArrayList<ArticleChapterItem> datas;
	final FooterManage footerManage;
	int selectIndex;

	public BookChapterAdapter(Activity act, ArrayList<ArticleChapterItem> datas) {
		this.act = act;
		this.datas = datas;
		footerManage = new FooterManage();
		selectIndex = -1;
	}

	public void upDatas(ArrayList<ArticleChapterItem> datas) {
		if (this.datas != datas) {
			this.datas.clear();
			this.datas.addAll(datas);
		}
		this.notifyDataSetChanged();
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		View lay;
		TextView name;
		TextView size;
		TextView currencyTxt;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			lay = view.findViewById(R.id.lay);
			size = (TextView) view.findViewById(R.id.size);
			currencyTxt = (TextView) view.findViewById(R.id.currencyTxt);
			lay.setOnClickListener(this);
		}

		public void setView(int selectIndex, int postion) {
			ArticleChapterItem item = datas.get(postion);
			size.setText(TextUtils.FormetFileSize(item.getCpSize()));
			lay.setTag(postion);

			name.setText(item.getCpName());
			currencyTxt.setText(act.getString(R.string.currency,
					item.getMCurrency()));
			if (selectIndex == postion) {
				name.setTextColor(act.getResources().getColor(R.color.orange));
			} else {
				name.setTextColor(act.getResources().getColor(
						android.R.color.black));
			}
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.lay: {
				int pos = (Integer) v.getTag();
				selectIndex = pos;
				ArticleChapterItem item = datas.get(pos);
				if (!AccUtils.checkPay(act, item)) {
					return;
				}
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(item));

				if (!android.text.TextUtils.isEmpty(item.getCpUrl())
						&& item.getCpUrl().endsWith(".txt"))
					LoadingTool.launchActivity(act, BookAct.class, Intent);
				else {
					Intent.putExtra("url", item.getCpUrl());
					LoadingTool.launchActivity(act, WebAct.class, Intent);
				}
				IoUtils.instance().saveReadingTrack(item.getAid(), pos);
				BookChapterAdapter.this.notifyDataSetChanged();
				break;
			}

			}
		}
	}

	@Override
	public int getItemCount() {
		return datas.size() + 1;
	}

	@Override
	public int getItemViewType(int pos) {
		if (isFooter(pos)) {
			return Type.TABLE_FOOTER;
		}
		return Type.TABLE_ITEM;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (isFooter(pos)) {
			footerManage.set();
		} else {
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(selectIndex, pos);
		}

	}

	public boolean isFooter(int pos) {
		return pos >= datas.size() || datas.size() == 0;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(R.layout.footer_item,
					parent, false);
			FooterHolder footerHolder = new FooterHolder(view);
			footerManage.footer = footerHolder;
			return footerHolder;
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.book_chapter_info_item, parent, false);
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
