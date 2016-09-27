package com.icloud.listenbook.ui.adapter;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.braunster.tutorialview.object.TutorialBuilder;
import com.braunster.tutorialview.object.TutorialIntentBuilder;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.base.view.DraggableGridViewPager;
import com.icloud.listenbook.base.view.viewpagerindicator.CirclePageIndicator;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.http.VolleyUtils;
import com.icloud.listenbook.http.datas.HttpConfig;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.FreshPush;

import org.json.JSONObject;

import java.util.ArrayList;

/*
 * 
 * */
public class HomePageTopView implements OnClickListener, Listener<JSONObject> {
	final static int MAX_SETUP = 3;
	static int itemCount;
	static int currentCount;
	String TAG;
	View view;
	ViewPager viewPager;
	CirclePageIndicator indicator;
	LedPagerAdapter ledPagerAdapter;
	FreshPushAdapter freshPushAdapter;
	View mediumTypeTip;
	View rankingTip;
	View teachTip;
	View msgTip;
	View gongguo_tip;
	TextView msgSizeTxt;
	boolean viewPagerTouch = false;
	Activity act;
	ArrayList<Ads> asdInfo;
	DraggableGridViewPager list;
	View info;
	private ArrayList<FreshPush> saveitems;
	TutorialBuilder tBuilder;
	TutorialIntentBuilder builder;

	public View getView() {
		return view;
	}

	public HomePageTopView(Activity act, ArrayList<Ads> asdInfo) {
		TAG = this.getClass().getName();
		this.act = act;
		this.asdInfo = asdInfo;
		view = LayoutInflater.from(act).inflate(R.layout.home_top_merge, null,
				false);
		mediumTypeTip = view.findViewById(R.id.medium_type_tip);
		rankingTip = view.findViewById(R.id.ranking_tip);
		teachTip = view.findViewById(R.id.teach_tip);
		msgTip = view.findViewById(R.id.msg_tip);
		gongguo_tip = view.findViewById(R.id.gongguo_tip);
		msgSizeTxt = (TextView) view.findViewById(R.id.msgSizeTxt);
		viewPager = (ViewPager) view.findViewById(R.id.led_page);
		indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		ledPagerAdapter = new LedPagerAdapter(act);
		ledPagerAdapter.upData(asdInfo);
		viewPager.setAdapter(ledPagerAdapter);
		indicator.setViewPager(viewPager);
		/** 发送循环 */
		HandlerUtils.removeCallbacks(LedRun);
		HandlerUtils.postDelayed(LedRun, 5000L);
		/** 设置显示圆点 **/
		indicator.setPageColor(0x4F000000);
		indicator.setStrokeWidth(0);
		indicator.setSpacing(24f);

		mediumTypeTip.setOnClickListener(this);
		rankingTip.setOnClickListener(this);
		teachTip.setOnClickListener(this);
		msgTip.setOnClickListener(this);
		gongguo_tip.setOnClickListener(this);

		viewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				viewPagerTouch = true;
				return false;
			}
		});

		mediumTypeTip.setOnTouchListener(ViewUtils.instance().onTouchListener);
		rankingTip.setOnTouchListener(ViewUtils.instance().onTouchListener);
		teachTip.setOnTouchListener(ViewUtils.instance().onTouchListener);
		gongguo_tip.setOnTouchListener(ViewUtils.instance().onTouchListener);

		upMsgSize();

		list = (DraggableGridViewPager) view.findViewById(R.id.list);
		info = view.findViewById(R.id.info);
		info.setOnClickListener(this);
		freshPushAdapter = new FreshPushAdapter(act);
		list.setColCount(3);
		list.setRowCount(1);
		list.setAdapter(freshPushAdapter);
		ArrayList<FreshPush> saveitems = new ArrayList<FreshPush>(IoUtils
				.instance().getFreshPush());
		if (!ToolUtils.isSameDay(SharedPreferenceUtil.getfreshPushTime())
				|| saveitems.size() == 0)
			freshPush();
		else
			freshPushAdapter.upData(saveitems);

	}

	private void freshPush() {
		VolleyUtils.instance().post(
				ServerIps.getLoginAddr() + HttpConfig.freshPush, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							LogUtil.e(getClass().getSimpleName(), "freshPush"
									+ response.toString());
							int res = response.optInt("result");
							SharedPreferenceUtil.savefreshPushTime(System
									.currentTimeMillis());
							saveitems = IoUtils.toFreshPushInfo(response
									.optJSONArray("list"));
							for (FreshPush item : saveitems) {
								HttpUtils.getArticleInfo(item.getAId(),
										HomePageTopView.this, null);
							}
							IoUtils.instance().clearFreshPush();
							itemCount = saveitems.size();
						} catch (Exception e) {
						}

					}
				}, null);
	}

	public void upMsgSize() {
		if (act != null && msgSizeTxt != null) {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int size = ChatMsgManage.instance().chatSize();
					msgSizeTxt.setVisibility(size > 0 ? View.VISIBLE
							: View.GONE);
					msgSizeTxt.setText(size >= 10 ? "N" : String.valueOf(size));
				}
			});
		}
	}

	public void notifyDataSetChanged() {
		if (ledPagerAdapter != null)
			ledPagerAdapter.notifyDataSetChanged();
	}

	public void setAsdInfo(ArrayList<Ads> asdInfo) {
		if (ledPagerAdapter != null)
			ledPagerAdapter.upData(asdInfo);
	}

	public Runnable LedRun = new Runnable() {
		@Override
		public void run() {
			if (act.isFinishing() || viewPagerTouch)
				return;
			if (viewPager != null
					&& viewPager.getAdapter() != null
					&& viewPager.getAdapter().getCount() > 0
					&& indicator.getScrollState() == ViewPager.SCROLL_STATE_IDLE) {
				int max = ledPagerAdapter.getCount();
				int index = viewPager.getCurrentItem();
				index = (index + 1) % max;
				viewPager.setCurrentItem(index);
			}
			HandlerUtils.postDelayed(LedRun, 5000L);
		}
	};

	@Override
	public void onClick(View v) {

	}

	public void upData(ArrayList<Ads> asdinfo) {
		if (this.asdInfo != asdinfo) {
			this.asdInfo.clear();
			this.asdInfo.addAll(asdinfo);
		}
	}

	@Override
	public void onResponse(JSONObject response) {
		int res = response.optInt("result", -1);
		if (res == 0) {
			int media = 0;
			// LogUtil.e(getClass().getSimpleName(), response.toString());
			synchronized (response) {
				media = response.optInt("media", media);
				String nameString = response.optString("aName", "");
				// LogUtil.e(getClass().getSimpleName(),"aName:"
				// +response.optString("aName", ""));
				if (null != saveitems) {
					currentCount++;
					for (FreshPush item : saveitems) {
						if (item.getAName().equals(nameString)) {
							item.setMedia(media);
						}
					}
					if (currentCount == itemCount) {
						IoUtils.instance().saveFreshPush(saveitems);
						freshPushAdapter.upData(saveitems);
					}
				}
			}

		}
	}

	public void onSizeChanged() {

//		builder = new TutorialIntentBuilder(act);
//		builder.changeSystemUiColor(true);
//
//		ArrayList<Tutorial> tutorials = new ArrayList<Tutorial>();
//		Tutorial tutorial1 = new Tutorial();
//		tutorial1.setTutorialGotItPosition(Tutorial.GotItPosition.BOTTOM);
//		tutorial1.setViewToSurround(mediumTypeTip);
//		tutorial1.setTitle("分类栏目简介");
//		tutorial1.setTutorialText("分类栏目展示了APP主要内容的详细分类，融通智慧学的精华部分都可以通过这里获取哦~");
//		tutorial1.setTutorialTextTypeFace("fonts/olivier.ttf");
//		tutorial1.setTutorialTextSize(18);
//		tutorials.add(tutorial1);
//		tutorial1.setAnimationDuration(400);
//		Tutorial tutorial2 = new Tutorial();
//		tutorial2.setTutorialGotItPosition(Tutorial.GotItPosition.BOTTOM);
//		tutorial2.setViewToSurround(gongguo_tip);
//		tutorial2.setTitle("功过格栏目简介");
//		tutorial2.setTutorialText("功过格栏目给您提供一个检视自己功过，反省自己的平台。每日认真填写功过格，即是培植福报与开启心智的根本修为。 ");
//		tutorial2.setTutorialTextTypeFace("fonts/olivier.ttf");
//		tutorial2.setTutorialTextSize(18);
//		tutorial2.setAnimationDuration(400);
//		tutorials.add(tutorial2);
//		builder.skipTutorialOnBackPressed(true);
//		builder.setWalkThroughList(tutorials);
//		act.startActivity(builder.getIntent());
//		act.overridePendingTransition(R.anim.dummy, R.anim.dummy);
	}

	public void GuideEvent() {
			onSizeChanged();

	}
}
