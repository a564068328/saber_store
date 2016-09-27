package com.icloud.listenbook.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseAppCompatActivity;
import com.icloud.listenbook.base.view.ViewPager;
import com.icloud.listenbook.http.datas.Login;
import com.icloud.listenbook.service.MPManage;
import com.icloud.listenbook.service.MPManage.MediaPlayerStateListener;
import com.icloud.listenbook.ui.adapter.TablesAdapter;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.ui.chipFrage.HomePageFrage;
import com.icloud.listenbook.unit.ATEUtils;
import com.icloud.listenbook.unit.AccUtils;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.Article;

import org.json.JSONObject;

/*
 * 
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                   O\  =  /O
 *                ____/`---'\____
 *              .'  \\|     |//  `.
 *             /  \\|||  :  |||//  \
 *            /  _||||| -:- |||||-  \
 *            |   | \\\  -  /// |   |
 *            | \_|  ''\---/''  |   |
 *            \  .-\__  `-`  ___/-. /
 *          ___`. .'  /--.--\  '. .'__
 *       ."" '<  `.___\_<|>_/___.'  >'"".
 *      | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *      \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *				  	`=---='
 *^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *               佛祖保佑       永无BUG
 */

public class TablesActivity extends BaseAppCompatActivity implements
		OnCheckedChangeListener, MediaPlayerStateListener, OnClickListener {
	public static final String OPEN_MEDIA = "com.icloud.listenbook.open.media";
	private TablesAdapter adapter;
	RadioGroup selectRg;
	ImageView musicPlayIcon;
	ImageView musicPlayImg;
	MPManage mPManage;
	Animation anim;
	private ViewPager pager;

	@Override
	public void init() {
		// objectbuffer.instance().getHallSocket();
		/** 检测是否需要更新 */
		Login.checkVersion();
		/*** 保存启动时间 */
		SharedPreferenceUtil.saveLastPull(System.currentTimeMillis());
		adapter = new TablesAdapter(getSupportFragmentManager());
		mPManage = new MPManage(this);
		anim = AnimationUtils.loadAnimation(this, R.anim.loading_music);
		anim.setInterpolator(new LinearInterpolator());

	}

	@Override
	public void getDatas() {
		super.getDatas();
		openMedia();
	}

	private void openMedia() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action) && OPEN_MEDIA.equals(action)) {
			String data = intent.getStringExtra("data");
			try {
				if (!TextUtils.isEmpty(data)) {
					Article article = new Article();

					JsonUtils.toArticle(article, new JSONObject(data));

					Intent Intent = new Intent();
					Intent.putExtra("data", JsonUtils.toJson(article));
					/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
					Class<? extends Activity> activity = VedioInfoAct.class;
					if (article.getMedia() == null) {
						return;
					} else if (article.getMedia() == Configuration.TYPE_VEDIO) {
						activity = VedioInfoAct.class;
					} else if (article.getMedia() == Configuration.TYPE_VOICE) {
						activity = VoiceInfoAct.class;
					} else if (article.getMedia() == Configuration.TYPE_BOOK) {
						activity = BookInfoAct.class;
					}
					LoadingTool.launchActivity(this, activity, Intent);
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public int getLayout() {
		return R.layout.act_tables;
	}

	@Override
	public void findViews() {
		pager = (ViewPager) findViewById(R.id.container);
		selectRg = (RadioGroup) findViewById(R.id.bottom);
		musicPlayIcon = (ImageView) findViewById(R.id.music_play_icon);
		musicPlayImg = (ImageView) findViewById(R.id.music_play_img);

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	/*
	 * 退出时发送广播与服务解绑
	 * 
	 * @see com.icloud.listenbook.base.BaseFragmentActivity#onDestroy()
	 */
	protected void onDestroy() {
		super.onDestroy();
		MPManage.sendBroadcastToService(this,
				com.icloud.listenbook.unit.Configuration.STATE_PAUSE,
				new Intent());
		mPManage.unbind();
	}

	@Override
	public void setListeners() {
		selectRg.setOnCheckedChangeListener(this);
		pager.setAdapter(adapter);
//		pager.addOnPageChangeListener(new MyOnPageChangeListener());
		/** 设置缓存数量 ViewPager缓存页面数目;当前页面的相邻N各页面都会被缓存 ***/
		pager.setOffscreenPageLimit(1);
		musicPlayImg.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		upPlayIcon();
		mPManage.addMusicList(this);
		ATEUtils.setStatusColorForKitlat(this);
		if (!SharedPreferenceUtil.getReiver_Guide_Notify()) {
			new Thread() {
				public void run() {
					while (!TablesActivity.this.isFinishing()
							&& !SharedPreferenceUtil.getReiver_Guide_Notify()) {
						// while(true){
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// 引导事件的广播
						Intent intent = new Intent();
						intent.setAction(HomePageFrage.GUIDE_ACTION);
						if (!TablesActivity.this.isFinishing()) {
							// LogUtil.e(TAG, "发送引导事件的广播");
							TablesActivity.this.sendBroadcast(intent);
						}
					}
				}
			}.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPManage.addMusicList(null);
	}

	protected void upPlayIcon() {
		if (mPManage.isPlaying()) {
			musicPlayIcon.setVisibility(View.GONE);
			musicPlayImg.clearAnimation();
			musicPlayImg.startAnimation(anim);
		} else {
			musicPlayIcon.setVisibility(View.VISIBLE);
			musicPlayImg.clearAnimation();
		}
	}

	public void upPage(int index) {
		if (index >= 0 && index <= 3) {
			switch (index) {
			case 0:
				selectRg.check(R.id.home);
				break;
			case 1:
				selectRg.check(R.id.boutique);
				break;
			case 2:
				selectRg.check(R.id.download);
				break;
			case 3:
				selectRg.check(R.id.userinfo);
				break;
			}

		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = 0;
		switch (checkedId) {
		case R.id.home:
			index = 0;
			break;
		case R.id.boutique:
			index = 1;
			break;
		case R.id.download:
			index = 2;
			break;
		case R.id.userinfo:
			index = 3;
			break;
		}
		pager.setCurrentItem(index);
	}

	@Override
	public void upState(Intent intent) {
		int state = intent.getIntExtra("state", Integer.MAX_VALUE);
		UIHandler.obtainMessage(state).sendToTarget();
	}

	Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (TablesActivity.this.isFinishing())
				return;
			switch (msg.what) {
			case Configuration.STATE_PLAY: {
				if (musicPlayImg.getAnimation() == null)
					musicPlayImg.startAnimation(anim);
				musicPlayIcon.setVisibility(View.GONE);
			}
				break;
			case Configuration.STATE_STOP:
			case Configuration.STATE_NON:
			case Configuration.STATE_PAUSE: {
				musicPlayImg.clearAnimation();
				musicPlayIcon.setVisibility(View.VISIBLE);
			}
				break;
			/** 绑定完成 **/
			case Configuration.STATE_MUSIC_BIND_END: {
			}
				break;
			case Configuration.STATE_MUSIC_PLAY_POSTION: {
				if (musicPlayIcon.getVisibility() != View.GONE)
					musicPlayIcon.setVisibility(View.GONE);
				if (musicPlayImg.getAnimation() == null) {
					musicPlayImg.startAnimation(anim);
				}
			}
				break;
			case Configuration.STATE_MUSIC_PREPARED: {
				musicPlayIcon.setVisibility(View.GONE);
				musicPlayImg.clearAnimation();
				musicPlayImg.startAnimation(anim);
			}
				break;
			case Configuration.STATE_MUSIC_COMPLETION: {
				musicPlayIcon.setVisibility(View.VISIBLE);
				musicPlayImg.clearAnimation();
			}
				break;
			default:
				;
				break;
			}
		}
	};
	boolean backTip = false;

	@Override
	public void onBackPressed() {
		if (backTip || !AccUtils.onBackPressed(this))
			super.onBackPressed();
		backTip = true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.music_play_img:
			if (mPManage.isLoadData()) {
				if (mPManage.isPlaying()) {
					mPManage.pause();
				} else {
					mPManage.start();

				}
			}
			break;
		}
	}


}
