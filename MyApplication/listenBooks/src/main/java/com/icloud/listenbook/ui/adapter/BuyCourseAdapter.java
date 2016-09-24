package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.DeductCurrencyInfo;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.Article;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class BuyCourseAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public Activity act;
	public ArrayList<DeductCurrencyInfo> datas;

	public BuyCourseAdapter(Activity act) {
		this.act = act;
		datas = new ArrayList<DeductCurrencyInfo>();
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public int getItemViewType(int position) {
		return datas.get(position).getType();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (holder instanceof AbstractHolder) {
			AbstractHolder abstractHolder = (AbstractHolder) holder;
			abstractHolder.setView(position);
		} else {
			ItemHolder itemHolder = (ItemHolder) holder;
			itemHolder.setView(position);
		}
	}

	public class AbstractHolder extends ViewHolder {

		public TextView tv_title;
		public TextView tv_body;

		public AbstractHolder(View view) {
			super(view);
			tv_title = (TextView) view.findViewById(R.id.tv_title);
			tv_body = (TextView) view.findViewById(R.id.tv_body);
		}

		public void setView(int position) {
			DeductCurrencyInfo info = datas.get(position);
			if (info.getType() == DeductCurrencyInfo.TYPE_ABSTRACT) {
				tv_title.setText("课程简介");
				tv_body.setText(info.getName());
			} else {
				tv_title.setText(info.getTitle());
				tv_body.setVisibility(View.GONE);
			}
		}
	}

	public class ItemHolder extends ViewHolder implements OnClickListener {

		public View rl;
		public TextView tv_chapter;
		public TextView tv_tip;
        public TextView tv_buy_status;
		public ItemHolder(View view) {
			super(view);
			rl = view.findViewById(R.id.rl);
			tv_chapter = (TextView) view.findViewById(R.id.tv_chapter);
			tv_tip = (TextView) view.findViewById(R.id.tv_tip);
			tv_buy_status=(TextView) view.findViewById(R.id.tv_buy_status);
		}

		public void setView(int position) {
			DeductCurrencyInfo info = datas.get(position);
			tv_chapter.setText(info.getCpname());
			rl.setTag(position);
			rl.setOnClickListener(this);
			if (info.getStatus() != DeductCurrencyInfo.STATUS_NO_BUY) {
				tv_tip.setVisibility(View.VISIBLE);
				if (info.getPos() > 0) {
					String secToHMS = com.icloud.wrzjh.base.utils.TextUtils
							.secToHMS(info.getPos() / 1000);
					tv_tip.setText("记录您上次观看到" + secToHMS);
				} else {
					tv_tip.setText("该视频尚未观看");
				}
				tv_buy_status.setText("已购买");
				tv_buy_status.setTextColor(act.getResources().getColor(R.color.gray));
			} else {
				tv_tip.setVisibility(View.GONE);
				tv_buy_status.setText("未购买");
				tv_buy_status.setTextColor(act.getResources().getColor(R.color.txt_blue));
			}
		}

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			Article article = IoUtils.instance().getArticleInfo(
					datas.get(position).getAid());
			Intent Intent = new Intent();
			Intent.putExtra("data", JsonUtils.toJson(article));
			Class<? extends Activity> activity = VedioInfoAct.class;
			LoadingTool.launchActivity(act, activity, Intent);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		View view = null;
		if (type == DeductCurrencyInfo.TYPE_ITEM) {
			view = LayoutInflater.from(act).inflate(
					R.layout.buycourse_chapter_item, parent, false);
			return new ItemHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.buycourse_abstract_item, parent, false);
			return new AbstractHolder(view);
		}
	}

	public void up(ArrayList<DeductCurrencyInfo> datas) {
		this.datas.clear();
		for (DeductCurrencyInfo info : datas) {
			if (info.getCpid() <= 0) {
				this.datas.add(info);
				continue;
			}
			if (info.getStatus() != DeductCurrencyInfo.STATUS_NO_BUY) {
				int track = IoUtils.instance().getReadingTrack(info.getCpid());
				if (track > 0) {
					info.setPos(track);
					IoUtils.instance().saveArticleChapterTtack(info);
				}
			}
			this.datas.add(info);
		}
		LogUtil.e("", "this.datas.size()=" + this.datas.size());
		notifyDataSetChanged();
	}
}
