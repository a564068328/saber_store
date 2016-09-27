package com.icloud.listenbook.ui.chipFrage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseFragement;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.base.view.RecyclerView;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.HomePageSpanSizeLookup;
import com.icloud.listenbook.ui.adapter.HomePageTopView;
import com.icloud.listenbook.ui.adapter.HomeTypeAdapter;
import com.icloud.listenbook.ui.adapter.entity.RecommendItem;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.listenBook.greendao.Ads;

import java.util.ArrayList;

/*
 * 主界面Fragement
 */
public class HomePageFrage extends BaseFragement implements OnClickListener {
	private static boolean isExct = false;
	public static final String UP_ACTION = "com.icloud.listenbook.ui.chipFrage.up";
	public static final String GUIDE_ACTION = "com.icloud.listenbook.ui.chipFrage.guide";
	HomeTypeAdapter homeTypeAdapter;
	ArrayList<RecommendItem> recommends;
	ArrayList<Ads> asdinfo;
	RecyclerView list;
	public static HomePageFrage instance;
	GridLayoutManager gridLayoutManager;
	upReceiver mReceiver;
	HomePageTopView topView;
	guideReceiver gReceiver;

	@Override
	public int getLayout() {
		return R.layout.frage_home_page;
	}

	@Override
	public void setListeners() {
		list.addHeaderView(topView.getView());
		list.setAdapter(homeTypeAdapter);
		gridLayoutManager.setSpanSizeLookup(new HomePageSpanSizeLookup(list));
		list.setLayoutManager(gridLayoutManager);
		list.setHasFixedSize(true);
		// 添加分割线
		// list.addItemDecoration(new HomeSampleDivider(list));
	}

	@Override
	public void findViews() {
		list = (RecyclerView)view.findViewById(R.id.list);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onResume() {
		super.onResume();
		rReceiver();
		upDada();
		if (topView != null && topView.LedRun != null) {
			HandlerUtils.removeCallbacks(topView.LedRun);
			HandlerUtils.postDelayed(topView.LedRun, 5000L);

		}
	}

	/**
	 * 注册自定义广播，以更新topView和今日推荐
	 */
	protected void rReceiver() {

		IntentFilter msgFilter = new IntentFilter();
		msgFilter.addAction(ChatMsgManage.CHAT_MSG_ADD_ACTION);
		act.registerReceiver(msgReceiver, msgFilter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(UP_ACTION);
		act.registerReceiver(mReceiver, filter);

	}

	/*
	 * 卸载自定义广播
	 */
	protected void uReceiver() {
		if (mReceiver != null)
			act.unregisterReceiver(mReceiver);
		if (null != msgReceiver)
			act.unregisterReceiver(msgReceiver);

	}

	@Override
	public void onPause() {
		super.onPause();
		uReceiver();
		if (topView != null && topView.LedRun != null) {
			HandlerUtils.removeCallbacks(topView.LedRun);
		}
	}

	public void upDada() {
		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				recommends = IoUtils.instance().getRecommendItems();
				asdinfo = new ArrayList<Ads>(IoUtils.instance().getAds(0));
				if (homeTypeAdapter != null)
					homeTypeAdapter.upData(recommends);
				if (topView != null)
					topView.upData(asdinfo);
			}
		});
	}

	@Override
	public void init() {
		super.init();
		mReceiver = new upReceiver();
		gReceiver = new guideReceiver();
		instance = this;
		// 得到推荐内容
		recommends = IoUtils.instance().getRecommendItems();
		asdinfo = new ArrayList<Ads>(IoUtils.instance().getAds(0));
		homeTypeAdapter = new HomeTypeAdapter(act, recommends);
		topView = new HomePageTopView(act, asdinfo);
		gridLayoutManager = new GridLayoutManager(act, 60);
		IntentFilter guidefilter = new IntentFilter();
		guidefilter.addAction(GUIDE_ACTION);
		act.registerReceiver(gReceiver, guidefilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;
		if (null != gReceiver) {
			act.unregisterReceiver(gReceiver);
		}
	}

	@Override
	public void onClick(View v) {

	}

	public static void sendUpUi() {
		Intent intent = new Intent();
		intent.setAction(HomePageFrage.UP_ACTION);
		// 向后台Service发送播放控制的广播
		GameApp.instance().sendBroadcast(intent);
	}

	BroadcastReceiver msgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (homeTypeAdapter != null) {
				if (topView != null)
					topView.upMsgSize();
				// homeTypeAdapter.notifyDataSetChanged();
				LogUtil.e("HomePageFrage", "更新topView和今日推荐");
			}
		}

	};

	class upReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			upDada();
		}

	}

	class guideReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferenceUtil.saveReiver_Guide_Notify(true);
			if (!isExct) {
				isExct=true;
				LogUtil.e("HomePageFrage", "接受到引导广播");
				topView.GuideEvent();
			}
		}

	};

}
