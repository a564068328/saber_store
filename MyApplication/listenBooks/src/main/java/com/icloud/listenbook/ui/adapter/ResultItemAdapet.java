package com.icloud.listenbook.ui.adapter;

import java.util.List;


import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.ResultItemInfo;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.MeritTableAdult;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultItemAdapet extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

	String TAG=getClass().getSimpleName();
	private Context context;
	private List<ResultItemInfo> dataList;

	public List<ResultItemInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<ResultItemInfo> dataList) {
		this.dataList = dataList;
	}

	public ResultItemAdapet(Context context,List<ResultItemInfo> dataList){
		this.context=context;
		this.dataList=dataList;
	}
	
	public void setdataList(List<ResultItemInfo> dataList){
		this.dataList=dataList;
	}
	/**
	 * 条目数量
	 */
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		//LogUtil.e(TAG, "dataList.size()"+dataList.size());
		return dataList==null?0:dataList.size();
	}
    /**
     * 绑定数据到控件上
     */
	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		// TODO Auto-generated method stub
		myViewHolder myHolder=(myViewHolder)viewholder;
        myHolder.setView(position);
	}

	/**
	 * 创建Item view,这个方法比onBindViewHolder先执行
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup root, int viewType) {
		// TODO Auto-generated method stub
		View view=LayoutInflater.from(context).inflate(R.layout.result_item, root, false);
		myViewHolder myvh=new myViewHolder(view);
		return myvh;
	}

	public class myViewHolder extends RecyclerView.ViewHolder{

		TextView tv_master=null;
		ImageView iv_gong=null;
		ImageView iv_guo=null;
		TextView tv_event=null;
		TextView tv_except=null;
		public myViewHolder(View view) {
			super(view);
			if(view!=null){
				tv_master=(TextView) view.findViewById(R.id.tv_master);
				iv_gong=(ImageView) view.findViewById(R.id.iv_gong);
				iv_guo=(ImageView) view.findViewById(R.id.iv_guo);
				tv_event=(TextView) view.findViewById(R.id.tv_event);
				tv_except=(TextView) view.findViewById(R.id.tv_except);
			}
		}
		
		public void setView(int position){
			//LogUtil.e(TAG, "setView(int position)"+position);
			ResultItemInfo info=dataList.get(position);
			tv_master.setText(info.masterTextView);
			if(info.gong){
				iv_gong.setVisibility(View.VISIBLE);
			}else{
				iv_gong.setVisibility(View.GONE);
			}
			if(info.guo){
				iv_guo.setVisibility(View.VISIBLE);
			}else{
				iv_guo.setVisibility(View.GONE);
			}
			if(TextUtils.isEmpty(info.event)){
				tv_event.setText("无");
			}else{
				tv_event.setText(info.event);
			}
			if(TextUtils.isEmpty(info.except)){
				tv_except.setText("无");
			}else{
				tv_except.setText(info.except);
			}
		}
		
	}
}
