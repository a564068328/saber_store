package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.ui.adapter.LessonAdapter.ChioceHolder;
import com.icloud.listenbook.ui.chipAct.TtsAct;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ChantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	Activity act;
	ArrayList<LessonItem> datas;

	public ChantAdapter(Activity act) {
		this.act = act;
		datas = new ArrayList<LessonItem>();
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ItemHolder itemHolder = (ItemHolder) holder;
		itemHolder.setView(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
		View view = LayoutInflater.from(act).inflate(R.layout.chant_item,
				parent, false);
		return new ItemHolder(view);
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView tv_date;
		TextView tv_chant;
		TextView tv_jump;

		public ItemHolder(View view) {
			super(view);
			tv_date = (TextView) view.findViewById(R.id.tv_date);
			tv_chant = (TextView) view.findViewById(R.id.tv_chant);
			tv_jump=(TextView) view.findViewById(R.id.tv_jump);
			tv_jump.setOnClickListener(this);
		}

		public void setView(int position) {
			LessonItem item = datas.get(position);
			
			StringBuilder builder=new StringBuilder(item.date.substring(4).toString());
			builder.insert(2, "月").insert(5, "日");
			tv_date.setText(builder.toString());
			tv_chant.setText(item.chant.trim());
			tv_jump.setTag(position);
		}

		@Override
		public void onClick(View v) {
			int position =(Integer) v.getTag();
			Intent intent = new Intent(act, TtsAct.class);
			intent.putExtra("position",position);
			act.startActivity(intent);
		}

	}

	public void up(ArrayList<LessonItem> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}
}
