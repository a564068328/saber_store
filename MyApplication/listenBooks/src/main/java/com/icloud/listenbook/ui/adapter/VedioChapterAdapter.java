package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.chipAct.PPTAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.ui.chipAct.WebVedioAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.TextUtils;

public class VedioChapterAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	Activity act;
	ArrayList<ArticleChapterItem> datas;
	int index;
	SelectListener selectList;

	public VedioChapterAdapter(Activity act,
			ArrayList<ArticleChapterItem> datas, int index) {
		this.act = act;
		this.datas = datas;
		this.index = index;
	}

	public void upDatas(ArrayList<ArticleChapterItem> datas) {
		if (this.datas != datas) {
			this.datas.clear();
			this.datas.addAll(datas);
		}
		
		this.notifyDataSetChanged();
	}

	public int getSelectIndex() {
		return index;
	}

	public void setSelectIndex(int index) {
		if (index >= 0 || index < datas.size()) {
			this.index = index;
			if (selectList != null)
				selectList.up(index);
		}
	}

	public class NetSelectionHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView text_body;
        TextView text_title;
		public NetSelectionHolder(View view) {
			super(view);
			text_body = (TextView) view.findViewById(R.id.text_body);
			text_title=(TextView) view.findViewById(R.id.text_title);
		}

		public void setView(int pos) {
			String text=datas.get(pos).getCpDesc();
			int index=datas.get(pos).getCpName().length();
			if(text.length()<index)
				return;
			text_title.setText(datas.get(pos).getCpName());
			text_body.setText(text.substring(index).trim());
		}

		@Override
		public void onClick(View v) {

		}

	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView name;
		View bg;
		TextView desc;
		TextView size;
		TextView currency;
		View lay;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			lay = view.findViewById(R.id.lay);
			bg = view.findViewById(R.id.bg);
			desc = (TextView) view.findViewById(R.id.desc);
			size = (TextView) view.findViewById(R.id.size);
			currency = (TextView) view.findViewById(R.id.currency);
			lay.setOnClickListener(this);
		}

		public void setView(int pos) {
			name.setText(String.valueOf(pos + 1));
			lay.setTag(pos);
			name.setSelected(pos == index);
			bg.setSelected(pos == index);
			ArticleChapterItem item = datas.get(pos);
			currency.setText(act.getString(R.string.currency_expend,
					item.getMCurrency()));
			desc.setText(item.getCpDesc());
			size.setText(TextUtils.FormetFileSize(item.getCpSize()));
		}

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			if (index != pos) {
				index = pos;
				if (selectList != null)
					selectList.up(index);
				VedioChapterAdapter.this.notifyDataSetChanged();
			}

			// String url = datas.get(pos).getCpUrl();
			// //LogUtil.e(getClass().getSimpleName(), "url:   "+url);
			// if (android.text.TextUtils.isEmpty(url))
			// return;
			// if (url.startsWith("http://") || url.startsWith("www.")
			// || url.startsWith("https://")) {
			// Intent intent = new Intent();
			// intent.putExtra("url", url);
			// LoadingTool.launchActivity(act, WebVedioAct.class, intent);
			// }
		}
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		if (viewHolder instanceof ItemHolder) {
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(pos);
		}else{
			NetSelectionHolder holder = (NetSelectionHolder) viewHolder;
			holder.setView(pos);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return datas.get(position).viewType;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Configuration.STATE_NON) {
			view = LayoutInflater.from(act).inflate(
					R.layout.vedio_net_selection, parent, false);
			return new NetSelectionHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.vedio_chapter_item, parent, false);
			return new ItemHolder(view);
		}
	}

	public void addSelectList(SelectListener selectList) {
		this.selectList = selectList;
	}

	public interface SelectListener {
		public void up(int postion);
	}
}
