package com.icloud.listenbook.ui.adapter;

import java.util.List;

import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.StandardInfo;
import com.icloud.listenbook.ui.adapter.HomeTypeAdapter.TitleHolder;
import com.icloud.listenbook.unit.Configuration;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StandardAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {

	List<StandardInfo> array;
	Activity act;

	public StandardAdapter(List<StandardInfo> array, Activity act) {
		super();
		this.array = array;
		this.act = act;
	}

	@Override
	public int getItemCount() {
		return array.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		StandardInfo item = array.get(pos);
		if(viewHolder instanceof TitleHolder){
			TitleHolder titleHolder =(TitleHolder)viewHolder;
			titleHolder.setView(item);
		}
		else if(viewHolder instanceof BodyHolder){
			BodyHolder bodyHolder =(BodyHolder)viewHolder;
			bodyHolder.setView(item);
		}
		else if(viewHolder instanceof BottomHolder){
			BottomHolder bottomHolder =(BottomHolder)viewHolder;
			bottomHolder.setView(item);
		}
	}

	public int getItemViewType(int pos) {
		return array.get(pos).getType();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if(viewType==Configuration.STANDARD_TITLE){
			view = LayoutInflater.from(act).inflate(
					R.layout.recommen_title_item, parent, false);
			return new TitleHolder(view);
		}
		if(viewType==Configuration.STANDARD_BODY){
			view = LayoutInflater.from(act).inflate(
					R.layout.standard_item_body, parent, false);
			return new BodyHolder(view);
		}
		if(viewType==Configuration.STANDARD_BOTTOM){
			view = LayoutInflater.from(act).inflate(
					R.layout.standard_item_bottom, parent, false);
			return new BottomHolder(view);
		}
		return null;
	}
	/*
	 * 标题的Holder
	 */
	public class TitleHolder extends RecyclerView.ViewHolder {
		TextView name;

		public TitleHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);

		}
		public void setView(StandardInfo item) {
			name.setText(item.getStr());
		}

	}
	
	public class BodyHolder extends RecyclerView.ViewHolder {
		TextView name;

		public BodyHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);

		}
		public void setView(StandardInfo item) {
			name.setText(item.getStr());
		}

	}
	
	public class BottomHolder extends RecyclerView.ViewHolder {
		Button confirm;

		public BottomHolder(View view) {
			super(view);
			confirm = (Button) view.findViewById(R.id.confirm);

		}
		public void setView(StandardInfo item) {
			confirm.setText(item.getStr());
			confirm.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					act.finish();
				}
			});
		}

	}
}
