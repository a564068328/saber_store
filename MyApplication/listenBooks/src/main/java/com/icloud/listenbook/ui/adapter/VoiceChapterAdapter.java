package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.view.CusView;
import com.icloud.listenbook.connector.MediaPlayerManage;
import com.icloud.listenbook.service.MPManage;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.FooterHolder;
import com.icloud.listenbook.ui.adapter.entity.FooterManage;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.wrzjh.base.utils.TextUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.icloud.listenbook.ui.adapter.entity.DownLoadListener;

public class VoiceChapterAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	Activity act;
	ArrayList<ArticleChapterItem> datas;
	final FooterManage footerManage;
	DownLoadListener downList;
	int selectIndex;
	int[] proDrawId = { R.drawable.progress_start, R.drawable.progress_stop,
			R.drawable.progress_end };
	Drawable[] proDraw;
	ViewPlayListner viewPlayListner;

	public VoiceChapterAdapter(Activity act, ArrayList<ArticleChapterItem> datas) {
		this.act = act;
		this.datas = datas;
		getProDraw();
		footerManage = new FooterManage();
		selectIndex = 0;

	}

	private void getProDraw() {
		proDraw = new Drawable[proDrawId.length];
		for (int i = 0; i < proDrawId.length; i++) {
			proDraw[i] = act.getResources().getDrawable(proDrawId[i]);
		}
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
		CusView pos;

		public ItemHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			lay = view.findViewById(R.id.lay);
			size = (TextView) view.findViewById(R.id.size);
			currencyTxt = (TextView) view.findViewById(R.id.currencyTxt);
			pos = (CusView) view.findViewById(R.id.pos);
			pos.setOnClickListener(this);
			pos.setProgressImage(proDraw);
			lay.setOnClickListener(this);

		}

		public void setView(int selectIndex, int postion) {
			ArticleChapterItem item = datas.get(postion);
			pos.setStatus(item.status == DownloadManager.STATUS_RUNNING);
			pos.setProgress(item.pos);

			size.setText(TextUtils.FormetFileSize(item.getCpSize()));
			currencyTxt.setText(act.getString(R.string.currency,
					item.getMCurrency()));
			pos.setTag(postion);
			lay.setTag(postion);
			name.setText(item.getCpName());
			if (selectIndex == postion) {
				if (viewPlayListner != null)
					viewPlayListner.playIn(postion);
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
				if (viewPlayListner != null)
					MPManage.palySwith=false;
					viewPlayListner.playIn(pos);
			}
				break;
			case R.id.pos: {
				int pos = (Integer) v.getTag();
				if (downList != null)
					downList.down(pos);

			}
				break;

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
			if(selectIndex<0)
				selectIndex=0;
			else if(selectIndex>datas.size()-1)
				selectIndex=datas.size()-1;
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
					R.layout.article_chapter_info_item, parent, false);
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

	public void setSelectIndex(int pos) {
		if (selectIndex != pos && pos >= 0 && pos < datas.size()) {
			selectIndex = pos;
			this.notifyDataSetChanged();
		}
	}

	public void addDownList(DownLoadListener downList) {
		this.downList = downList;
	}

	public void removeDownList() {
		downList = null;
	}

	public void addVoiceManage(ViewPlayListner list) {
		this.viewPlayListner = list;
	}

	public interface ViewPlayListner {
		public void playIn(int postion);
	}

}
