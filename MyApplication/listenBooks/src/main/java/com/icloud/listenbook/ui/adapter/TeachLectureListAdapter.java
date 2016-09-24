package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.base.view.lableview.LableNetworkImageView;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.LectureInfo;
import com.icloud.listenbook.ui.chipAct.BuyCourseAct;
import com.icloud.listenbook.ui.chipAct.PPTAct;
import com.icloud.listenbook.ui.chipAct.TeachVedioList;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;

public class TeachLectureListAdapter extends BaseAdapter implements
		OnClickListener {
	public static final int NOTIFY_UP_promat = 20160428 + 2;
	ArrayList<LectureInfo> lectureInfo;
	Activity act;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	String urlHead;
	Handler uIHandler;

	public TeachLectureListAdapter(Activity act, Handler uIHandler) {
		this.uIHandler = uIHandler;
		lectureInfo = new ArrayList<LectureInfo>();
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	public void upData(ArrayList<LectureInfo> books) {
		if (lectureInfo != books) {
			this.lectureInfo.clear();
			this.lectureInfo.addAll(books);
		}	
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return lectureInfo.size();
	}

	@Override
	public LectureInfo getItem(int arg0) {
		return lectureInfo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder holder;
		if (view == null) {
			LayoutInflater mInflater = LayoutInflater.from(act);
			view = mInflater.inflate(R.layout.teach_lecture_item, null);
			holder = new ViewHolder();
			holder.icon = (LableNetworkImageView) view.findViewById(R.id.icon);
			holder.bg = view.findViewById(R.id.bg);
			holder.title = (TextView) view.findViewById(R.id.title);
			holder.time = (TextView) view.findViewById(R.id.time);
			holder.topic = (TextView) view.findViewById(R.id.topic);
			holder.intro = (TextView) view.findViewById(R.id.intro);
			holder.pptid = (TextView) view.findViewById(R.id.pptid);
			holder.tv_prompt = (TextView) view.findViewById(R.id.tv_prompt);
			holder.uri = (TextView) view.findViewById(R.id.uri);
			holder.icon.setDefaultImageResId(R.drawable.icon_default_voice);
			holder.icon.setErrorImageResId(R.drawable.icon_default_voice);
			holder.topic.setOnClickListener(this);
			holder.topic.setOnTouchListener(GameApp.instance().onTouchListener);
			holder.intro.setOnClickListener(this);
			holder.intro.setOnTouchListener(GameApp.instance().onTouchListener);
			holder.pptid.setOnClickListener(this);
			holder.pptid.setOnTouchListener(GameApp.instance().onTouchListener);
			holder.tv_prompt.setOnClickListener(this);
			holder.tv_prompt
					.setOnTouchListener(GameApp.instance().onTouchListener);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LectureInfo item = lectureInfo.get(position);
//		LogUtil.e("lectureInfo","lectureInfo.get(position)"+position);
//		LogUtil.e("lectureInfo","name"+item.title);
//		LogUtil.e("lectureInfo","isstart"+item.isstart);
		holder.title.setText(item.title);
		// holder.bg.setBackgroundColor(item.isstart?Color.WHITE:0x4fffffff);
		if (item.isstart) {
			holder.tv_prompt.setText("尚未开播");
			holder.tv_prompt.setTag(position);
			Message msg = Message.obtain();
			msg.obj = holder.tv_prompt;
			msg.what = NOTIFY_UP_promat;
			uIHandler.sendMessage(msg);
		} else {
			holder.tv_prompt.setBackgroundResource(R.drawable.white);
			holder.tv_prompt.setBackgroundColor(act.getResources()
					.getColor(R.color.white));
			holder.tv_prompt.setTextColor(act.getResources().getColor(
					R.color.black));
			holder.tv_prompt.setText("尚未开放");
		}
		holder.time.setText(item.time);
		holder.icon.setTag(position);
		holder.icon.setImageUrl(urlHead + item.icon, imageLoader);
		holder.pptid.setTag(position);
		holder.intro.setTag(position);
		holder.topic.setTag(position);
		holder.tv_prompt.setTag(position);
		
		if (!TextUtils.isEmpty(item.uri)) {
//			LogUtil.e("lectureInfo","position"+position+"name"+item.title+"isstart"+item.isstart);
			if (item.uri.startsWith("http://") || item.uri.startsWith("www.")
					|| item.uri.startsWith("https://")) {
				holder.uri.setVisibility(View.VISIBLE);
				holder.uri.setOnClickListener(this);
				holder.uri
						.setOnTouchListener(GameApp.instance().onTouchListener);
				holder.uri.setTag(position);
				
				holder.icon.setLabelVisual(true);
			}
		}else{
			holder.uri.setVisibility(View.INVISIBLE);
			holder.icon.setLabelVisual(false);
		}
		return view;
	}

	class ViewHolder {
		View bg;
		LableNetworkImageView icon;
		TextView title;
		TextView time;
		TextView topic;
		TextView intro;
		TextView pptid;
		TextView tv_prompt;
		TextView uri;
	}

	public void showInfo(String msg) {
		new AlertDialog.Builder(act)
				.setMessage(msg)
				.setNegativeButton(R.string.close,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						}).setCancelable(true).show();
	}

	@Override
	public void onClick(View v) {
		int postion = (Integer) v.getTag();
		LectureInfo item = lectureInfo.get(postion);
		switch (v.getId()) {
		case R.id.topic:
			if (!TextUtils.isEmpty(item.topic)) {
				showInfo(item.topic);
			}
			break;
		case R.id.intro:
			if (!TextUtils.isEmpty(item.intro)) {
				showInfo(item.intro);
			}
			break;
		case R.id.pptid:
			Intent intent = new Intent();
			intent.putExtra("id", item.pptid);
			LoadingTool.launchActivity(act, PPTAct.class, intent);
			break;
		case R.id.tv_prompt:
			if (item.isstart) {
			}
			break;
		case R.id.uri:
			if(item.title==null)
				return;
			long cid = IoUtils.instance().fromNameGetCid(item.title.trim());
			if(cid > 0){
				intent = new Intent();
				intent.putExtra("cid", cid);
				intent.putExtra("title", item.title);
				intent.putExtra("icon", item.icon);
				intent.putExtra("intro", item.intro);
				LoadingTool.launchActivity(act, BuyCourseAct.class,intent);
			}else{
				Uri uri = Uri.parse(item.uri);
				intent = new Intent("android.intent.action.VIEW");
				intent.setData(uri);
				act.startActivity(intent);
			}
			break;
		}

	}

}
