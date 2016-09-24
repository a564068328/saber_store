package com.icloud.listenbook.ui.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.CircleNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.entity.ResultItemInfo;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.ui.chipAct.ChatMsgAct;
import com.icloud.listenbook.ui.chipAct.ResultAct;
import com.icloud.listenbook.ui.chipAct.ResultItemAct;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final int NO = 0;
	private final int GONG = NO + 1;
	private final int GUO = GONG + 1;
	private final int ALL = GUO + 1;
	String TAG;
	private Context context;
	private List<MeritTableAdult> AdultList;
	private List<MeritTableChildren> ChildrenList;
	ImageLoader imageLoader;
	LruIcoCache lruIcoCache;
	ResultAct act;
	String urlHead;
	boolean isAdult;
	boolean isClick;
	long upTime;
	public static final long UP_TIME_SPAN = 1 * 800;
	public static final int UPDATE = 1;
	public static final int REMOVE = 2;
	// RecyclerView recyclerview;
	// MeritTableAdult adult;
	// ResultItemAdapet resultItemAdapet;
	List<ResultItemInfo> lists;
//	List<MeritTableAdult> bufferList;
//	List<MeritTableChildren> bufferChildrenList;

	// LinearLayoutManager manager;
	public ResultAdapter(Context context, List<MeritTableAdult> AdultList,
			List<MeritTableChildren> ChildrenList, boolean isAdult) {
		TAG = getClass().getSimpleName();
		// resultItemAdapet=itemAdapet;
		// recyclerview=recyclerviewitem;
		isClick = false;
		this.context = context;
		act = (ResultAct) context;
		this.AdultList = AdultList;
		this.ChildrenList = ChildrenList;
		this.isAdult = isAdult;
//		bufferList = new ArrayList<MeritTableAdult>();
//		bufferChildrenList=new ArrayList<MeritTableChildren>();
		RequestQueue mQueue = Volley.newRequestQueue(context);
		LruImageCache lruImageCache = LruImageCache.instance(context
				.getApplicationContext());
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);
	}

	protected Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (act.isFinishing())
				return;
			// 大于更新的时间跨度才刷新UI
			if ((System.currentTimeMillis() - upTime) > UP_TIME_SPAN) {
				upTime = System.currentTimeMillis();
			} else {
				UIHandler.sendMessageDelayed(UIHandler.obtainMessage(),
						UP_TIME_SPAN);
			}
			int what = msg.what;
			switch (what) {
			case UPDATE:
				// if (recyclerview == null )
				// break;

				// LogUtil.e(TAG, "UPDATE了。。。。。。。。");
				String date = (String) msg.obj;
				Intent intent = new Intent(act, ResultItemAct.class);
				intent.putExtra("date", date);
				act.startActivity(intent);
				break;

			case REMOVE:
				// recyclerview.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 条目数量
	 */
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		if (isAdult)
			return AdultList == null ? 0 : AdultList.size();
		else
			return ChildrenList == null ? 0 : ChildrenList.size();
	}

	/**
	 * 绑定数据到控件上
	 */
	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		// TODO Auto-generated method stub
		myViewHolder myHolder = (myViewHolder) viewholder;
		myHolder.setView(position);
	}

	/**
	 * 创建Item view,这个方法比onBindViewHolder先执行
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup root, int viewType) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(
				R.layout.third_body_item, root, false);
		myViewHolder myvh = new myViewHolder(view);
		return myvh;
	}

	public class myViewHolder extends RecyclerView.ViewHolder {

		CircleNetworkImageView icon = null;
		TextView name = null;
		TextView gongguo = null;
		TextView date = null;
		ImageView iv_extend = null;

		public myViewHolder(View view) {
			super(view);
			if (view != null) {
				name = (TextView) view.findViewById(R.id.name);
				icon = (CircleNetworkImageView) view.findViewById(R.id.icon);
				gongguo = (TextView) view.findViewById(R.id.gongguo);
				date = (TextView) view.findViewById(R.id.date);
				iv_extend = (ImageView) view.findViewById(R.id.iv_extend);
				// recyclerview = (RecyclerView) view
				// .findViewById(R.id.recyclerview);
			}
		}

		public void setView(int position) {
			if (isAdult) {
				MeritTableAdult adult = AdultList.get(position);
				
				icon.setDefaultImageResId(R.drawable.user_defa_icon);
				icon.setErrorImageResId(R.drawable.user_defa_icon);
				String iconUrl = UserInfo.instance().getIcon();
				if (!TextUtils.isEmpty(iconUrl)) {
					icon.setImageUrl(urlHead + iconUrl, imageLoader);
				}
				name.setText(UserInfo.instance().getNick());
				gongguo.setText("功数:" + adult.getGong() + " , " + "过数"
						+ adult.getGuo());
//				Date Date = new Date(adult.getDate());
//				SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String strdate = adult.getDate();
				date.setText(strdate);
				iv_extend.setTag(position);
				iv_extend.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int postion = (Integer) v.getTag();
						MeritTableAdult adult = AdultList.get(postion);
//						LogUtil.e(TAG, "onClick了。。。。。。。。");
						GameApp.instance().ResultItemlist = ParseAdult(adult);
						if (GameApp.instance().ResultItemlist == null)
							return;
						Message message = Message.obtain();
						//bufferList.add(adult);
//						Date Date = new Date(adult.getDate());
//						SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
						String strdate = adult.getDate();;
						message.what = UPDATE;
						message.obj = strdate;
						UIHandler.sendMessage(message);

					}
				});
			} else {
				MeritTableChildren children = ChildrenList.get(position);
				icon.setDefaultImageResId(R.drawable.user_defa_icon);
				icon.setErrorImageResId(R.drawable.user_defa_icon);
				String iconUrl = UserInfo.instance().getIcon();
				if (!TextUtils.isEmpty(iconUrl)) {
					icon.setImageUrl(urlHead + iconUrl, imageLoader);
				}
				name.setText(UserInfo.instance().getNick());
				gongguo.setText("功数:" + children.getGong() + " , " + "过数"
						+ children.getGuo());
//				Date Date = new Date(children.getDate());
//				SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String strdate = children.getDate();
				date.setText(strdate);
				iv_extend.setTag(position);
				iv_extend.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int postion = (Integer) v.getTag();
						MeritTableChildren children = ChildrenList.get(postion);
						//LogUtil.e(TAG, "onClick了。。。。。。。。");
						GameApp.instance().ResultItemlist = ParseChildren(children);
						if (GameApp.instance().ResultItemlist == null)
							return;
						Message message = Message.obtain();
//						Date Date = new Date(children.getDate());
//						SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
						String strdate =  children.getDate();
						
						message.what = UPDATE;
						message.obj = strdate;
						UIHandler.sendMessage(message);

					}
				});
			}
		}

		protected List<ResultItemInfo> ParseAdult(MeritTableAdult adult) {
//			lists = new ArrayList<ResultItemInfo>();
//			ResultItemInfo infodemo=new ResultItemInfo(true,true,"起心动念","无","无");
//			lists.add(infodemo);
//			return lists;
			if (adult == null)
				return null;
			lists = new ArrayList<ResultItemInfo>();
			int i = adult.getIdea();
			ResultItemInfo info = new ResultItemInfo(i == ALL || i == GONG,
					i == ALL || i == GUO, "起心动念", adult.getIdeades(),
					adult.getIdeaexc());
			lists.add(info);

			i = adult.getAttitude();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "言语态度", adult.getAttitudedes(),
					adult.getAttitudeexc());
			lists.add(info);

			i = adult.getAction();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "行为处事", adult.getActiondes(),
					adult.getActionexc());
			lists.add(info);

			i = adult.getTreat();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "待人接物", adult.getTreatdes(),
					adult.getTreatexc());
			lists.add(info);

			i = adult.getWork();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "工作事业", adult.getWorkdes(), adult.getWorkexc());
			lists.add(info);

			i = adult.getBelief();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "信仰修为", adult.getBeliefdes(),
					adult.getBeliefexc());
			lists.add(info);

			i = adult.getOther();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "其  它", adult.getOtherdes(),
					adult.getOtherexc());
			lists.add(info);

			return lists;
		}
		protected List<ResultItemInfo> ParseChildren(MeritTableChildren children) {
			// TODO Auto-generated method stub
			if (children == null)
				return null;
			lists = new ArrayList<ResultItemInfo>();
			if(children.getSchedules()==null){
				LogUtil.e("getSchedules", "is null");
			}
			int i = children.getSchedules();
			ResultItemInfo info = new ResultItemInfo(i == ALL || i == GONG,
					i == ALL || i == GUO, "作息规律", children.getSchedulesdes(),
							children.getSchedulesexc());
			lists.add(info);

			i = children.getAttitude();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "对人态度", children.getAttitudedes(),
							children.getAttitudeexc());
			lists.add(info);

			i = children.getStudy();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "学习专注", children.getStudydes(),
							children.getStudyexc());
			lists.add(info);

			i = children.getLove();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "爱心善意", children.getLovedes(),
							children.getLoveexc());
			lists.add(info);

			i = children.getRespect();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "尊师重教", children.getRespectdes(), children.getRespectexc());
			lists.add(info);

			i = children.getAction();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "思考行动", children.getActiondes(),
							children.getActionexc());
			lists.add(info);

			i = children.getOther();
			info = new ResultItemInfo(i == ALL || i == GONG, i == ALL
					|| i == GUO, "其  它", children.getOtherdes(),
							children.getOtherexc());
			lists.add(info);

			return lists;
		}
	}
}
