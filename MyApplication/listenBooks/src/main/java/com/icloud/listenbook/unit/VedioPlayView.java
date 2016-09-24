package com.icloud.listenbook.unit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.view.TxTView;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.WarningDlg;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.unit.PlayerUnit.ProcessListen;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;
import com.qcloud.player.VideoInfo;
import com.qcloud.player.ui.QCloudVideoView;
import com.qcloud.player.ui.QCloudVideoView.OnKeyDownListener;
import com.qcloud.player.ui.QCloudVideoView.OnToggleFullscreenListener;

/**
 * qcloud播放功能实现及横竖屏切换
 * 
 * @author Administrator
 * 
 */
public class VedioPlayView implements OnClickListener {
	private static final int INIT_VALUE = 1;
	private static final int SWITCH = INIT_VALUE + 1;
	private static final int TRAFFIC = SWITCH + 1;
	private static final int VISIABLE = TRAFFIC + 1;
	private static final int GONE = VISIABLE + 1;
	private static final int JUMP = GONE + 1;
	public static final int JUMP_IS_ENABLE = JUMP + 1;
	public static final int ERROR = JUMP_IS_ENABLE + 1;
	private static final int SEEK_OFFSET = 10;
	private static final int CONTROLLER_TIMEOUT = 10;
	private final String Tag = this.getClass().getName();
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	int CurrentPosition;
	int Positionbuf;
	View tv_progressbar;
	LinearLayout ll_progressbar;
	LinearLayout ll_playerror;
	View tv_reobtain;
	// 播放视频的RelativeLayout
	RelativeLayout top;
	VedioInfoAct act;
	QCloudVideoView videoView;
	// 跳转播放相关控件
	LinearLayout ll_jump;
	ImageView iv_jump;
	TextView tv_jump_report;
	TextView tv_jump_play;
	private static final String[] STREAM_NAMES = new String[] { "标清" };
	VideoInfo videoInfo;
	// 播放按钮
	View vedioManage;
	// 播放视频的RelativeLayout，不包含QCloudVideoView控件
	View vedioManageLay;
	ArticleChapterItem playItem;
	int pos;
	boolean autoPlay;// 自动播放？
	Timer timer;
	int Track;
	// 是否第一次显示跳转框
	boolean isFirstTrack = true;
	boolean timerIsStop = false;
	int Cpid;
	int pauseSeconds;
	int currentProcessBuf;

	public VedioPlayView(VedioInfoAct act) {
		autoPlay = false;
		this.act = act;
		findViews();
		init();
		setListeners();
		setDatas();

	}

	private void setDatas() {

	}

	protected void init() {
		// 找到com.qcloud.player.ui.QCloudVideoView控件
		videoView = (QCloudVideoView) findViewById(R.id.qcloud_video_view);

	}

	protected void findViews() {
		top = (RelativeLayout) findViewById(R.id.top);
		vedioManage = findViewById(R.id.vedioManage);
		vedioManageLay = findViewById(R.id.vedioManageLay);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar);
		tv_progressbar = findViewById(R.id.tv_progressbar);
		ll_playerror = (LinearLayout) findViewById(R.id.ll_playerror);
		tv_reobtain = findViewById(R.id.tv_reobtain);
		ll_jump = (LinearLayout) findViewById(R.id.ll_jump);
		iv_jump = (ImageView) findViewById(R.id.iv_jump);
		tv_jump_report = (TextView) findViewById(R.id.tv_jump_report);
		tv_jump_play = (TextView) findViewById(R.id.tv_jump_play);
	}

	protected void setListeners() {
		// 打开亮度控制功能
		videoView.setEnableBrightnessControll(act, true); // 播放全屏半屏切换需要使用者自己控制
		// 横竖屏切换监听
		videoView.setOnToggleFullscreenListener(onToggleFullscreenListener);
		videoView.setOnKeyDownListener(onKeyDownListener);
		// 打开手势支持
		videoView.setEnableGesture(true);
		// 打开顶部bar
		videoView.setEnableTopBar(true);
		// 打开返回按钮
		videoView.setEnableBackButton(act, true);

		vedioManage.setOnClickListener(this);

		tv_reobtain.setOnClickListener(this);

		iv_jump.setOnClickListener(this);
		tv_jump_play.setOnClickListener(this);
	}

	private View findViewById(int id) {
		return act.findViewById(id);
	}

	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		ViewGroup.LayoutParams lp = top.getLayoutParams();
		int[] dwh = ViewUtils.getWH(act);
		lp.width = dwh[0];
		Window window = act.getWindow();
		if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			lp.height = dwh[1];
			top.setLayoutParams(lp);
		} else {
			window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

			lp.height = (int) (dwh[1] / 3f * 0.95f);

		}
		top.setLayoutParams(lp);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.act.finish();
			break;
		case R.id.vedioManage: {
			// 网络非WiFi警告
			if ((!SharedPreferenceUtil.isNoWifiLook())
					&& HttpUtils.isNetworkAvailable(act)
					&& !HttpUtils.isWifi(act)) {
				DialogManage.showWarningDlg(act, R.string.warning,
						R.string.no_wifi_warning, R.string.no_tip,
						R.string.cancel, R.string.continue_tip,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (dialog instanceof WarningDlg
										&& ((WarningDlg) dialog).isCheck()) {
									SharedPreferenceUtil.saveNoWifiLook(true);
								}
								dialog.dismiss();
								ManageHandler.obtainMessage(SWITCH)
										.sendToTarget();

							}

						});

			} else
				// 播放视频
				ManageHandler.obtainMessage(SWITCH).sendToTarget();

		}
			break;
		case R.id.tv_reobtain:
			// 网络非WiFi警告
			if ((!SharedPreferenceUtil.isNoWifiLook())
					&& HttpUtils.isNetworkAvailable(act)
					&& !HttpUtils.isWifi(act)) {
				DialogManage.showWarningDlg(act, R.string.warning,
						R.string.no_wifi_warning, R.string.no_tip,
						R.string.cancel, R.string.continue_tip,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (dialog instanceof WarningDlg
										&& ((WarningDlg) dialog).isCheck()) {
									SharedPreferenceUtil.saveNoWifiLook(true);

								}
								dialog.dismiss();
								// videoView.seekTo(PlayerUnit.instance()
								// .getposition());
								videoInfo.setCurrentPosition(PlayerUnit
										.instance().getposition());
								videoView.setVideoInfo(videoInfo, true);
							}
						});

			} else {
				// 播放视频
				// videoView.seekTo(PlayerUnit.instance().getposition());
				videoInfo.setCurrentPosition(PlayerUnit.instance()
						.getposition());
				videoView.setVideoInfo(videoInfo, true);
			}
			ll_playerror.setVisibility(View.GONE);
			break;
		case R.id.iv_jump:
			ll_jump.setVisibility(View.GONE);
			break;
		case R.id.tv_jump_play:
			ManageHandler.sendEmptyMessage(JUMP);
		}
	}

	public void changeSurfaceSize() {

		android.content.res.Configuration newConfig = act.getResources()
				.getConfiguration();
		if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			/** 切换竖屏 **/
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			/** 切换 横屏 **/
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (newConfig.hardKeyboardHidden == android.content.res.Configuration.KEYBOARDHIDDEN_NO) {
			// 键盘没关闭。屏幕方向为横屏
			/** 切换竖屏 **/
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (newConfig.hardKeyboardHidden == android.content.res.Configuration.KEYBOARDHIDDEN_YES) {
			// 键盘关闭。屏幕方向为竖屏
			/** 切换 横屏 **/
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	public void onPause() {

	}

	public void onResume() {

	}

	public void setInfo(int selectIndex) {
		if (selectIndex < 0 || selectIndex >= act.chapterInfos.size())
			return;
		pos = selectIndex;
		ArticleChapterItem item = act.chapterInfos.get(selectIndex);
		ManageHandler.obtainMessage(INIT_VALUE, item).sendToTarget();
		if (item.getCpId() > 0) {
			PlayerUnit.instance().upCpid(item.getCpId());

		}
	}

	public void onDestroy() {
		try {
			if (timer != null)
				timer.cancel();
			videoView.stopPlayback();
			ManageHandler.removeMessages(JUMP_IS_ENABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnKeyDownListener onKeyDownListener = new OnKeyDownListener() {
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (videoView == null) {
				return false;
			}
			try {
				switch (keyCode) {
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				case KeyEvent.KEYCODE_DPAD_CENTER:
				case KeyEvent.KEYCODE_ENTER:
					videoView.togglePlay();
					return true;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					videoView.backward(SEEK_OFFSET);
					videoView.showController(CONTROLLER_TIMEOUT);
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					videoView.forward(SEEK_OFFSET);
					videoView.showController(CONTROLLER_TIMEOUT);
					return true;
				default:
					break;
				}
			} catch (Exception e) {
			}
			return false;
		}
	};
	private QCloudVideoView.OnToggleFullscreenListener onToggleFullscreenListener = new OnToggleFullscreenListener() {
		@Override
		public void onToggleFullscreen(boolean isFullscreen) {
			changeSurfaceSize();
		}
	};

	Handler ManageHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INIT_VALUE: {
				playItem = (ArticleChapterItem) msg.obj;
				String cpUrl = act.getDLUrl(playItem.did);
				if (TextUtils.isEmpty(cpUrl) || (!FileUtils.isExists(cpUrl))) {
					cpUrl = playItem.getCpUrl();
				}
				String[] url = { cpUrl };
				if (null != videoInfo
						&& videoInfo.getFileId().equals(
								String.valueOf(playItem.getCpId())))
					return;
				if (videoView.isPlaying()) {
					videoView.puase();
				}

				videoInfo = new VideoInfo(String.valueOf(playItem.getCpId()),
						STREAM_NAMES, url, 0, -1);
				try {
					// 判断数据合法性
					VideoInfo.validate(videoInfo);
				} catch (IllegalArgumentException e) {
					videoInfo = null;
					return;
				}
				vedioManage.setVisibility(View.VISIBLE);
				vedioManageLay.setVisibility(View.VISIBLE);
				if (autoPlay && HttpUtils.isWifi(act)) {
					autoPlay = false;
					ManageHandler.obtainMessage(SWITCH).sendToTarget();
				}

			}
				;
				break;
			case SWITCH: {
				if (videoInfo == null || playItem == null)
					return;
				if (!AccUtils.checkPay(act, playItem)) {
					return;
				}
				// 保存播放的资源id
				// IoUtils.instance().saveReadingTrack(act.article.getAId(),
				// pos);
				vedioManage.setVisibility(View.GONE);
				vedioManageLay.setVisibility(View.GONE);

				ll_progressbar.setVisibility(View.VISIBLE);
				timerIsStop = false;

				lastTotalRxBytes = getTotalRxBytes();
				lastTimeStamp = System.currentTimeMillis();
				setProcessTimer();
				PlayerUnit.instance().getView(videoView, ll_progressbar, timer,
						ll_playerror, ManageHandler,
						act.chapterInfos.get(0).getCpId());
				act.setTimer(timer);
				// 开始播放
				videoView.setVideoInfo(videoInfo, true);
			}
				;
				break;
			case TRAFFIC:
				String str = (String) msg.obj;
				((TextView) tv_progressbar).setText(str);
				break;

			case VISIABLE:

				if (!act.isFinishing()) {
					ll_progressbar.setVisibility(View.VISIBLE);
				}
				break;

			case GONE:
				if (!act.isFinishing()) {
					ll_progressbar.setVisibility(View.GONE);
					if (ll_playerror.getVisibility() == View.VISIBLE)
						ll_playerror.setVisibility(View.GONE);
				}
				break;
			case JUMP_IS_ENABLE:
				if (isFirstTrack) {
					Track = (Integer) msg.obj;
					String secToHMS = com.icloud.wrzjh.base.utils.TextUtils
							.secToHMS(Track / 1000);
					tv_jump_report.setText("记录您上次观看到" + secToHMS);
					ll_jump.setVisibility(View.VISIBLE);
					isFirstTrack = false;
					ManageHandler.sendEmptyMessageDelayed(JUMP_IS_ENABLE, 15000);
				} else {
					ll_jump.setVisibility(View.INVISIBLE);
				}
				break;
			case JUMP:
				if (!act.isFinishing()) {
					ll_jump.setVisibility(View.GONE);
					// LogUtil.e(Tag, "Track:"+Track);
					videoView.seekTo(Track);
				}
				break;
			case ERROR:
				ll_progressbar.setVisibility(View.GONE);
				ll_playerror.setVisibility(View.VISIBLE);
				break;
			}
		}

		private void setProcessTimer() {
			if (timer == null) {
				timer = new Timer();
				timer.schedule(task, 100, 1000); // 0.1s后启动任务，每1s执行一次
				PlayerUnit.instance().setProcessListen(listten);
			}
		};
	};

	public void setAutoPlay(boolean autoPlay) {
		this.autoPlay = autoPlay;
	}

	private void showNetSpeed() {
		long nowTotalRxBytes = getTotalRxBytes();
		long nowTimeStamp = System.currentTimeMillis();
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));// 毫秒转换

		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;

		Message msg = ManageHandler.obtainMessage();
		msg.what = TRAFFIC;
		msg.obj = com.icloud.wrzjh.base.utils.TextUtils.FormetFileSize(speed)
				+ "B/S";

		ManageHandler.sendMessage(msg);// 更新界面
	}

	private long getTotalRxBytes() {
		return TrafficStats.getUidRxBytes(act.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0
				: (TrafficStats.getTotalRxBytes());//
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			showNetSpeed();
			listenProcess();
		}
	};

	protected void listenProcess() {
		if (videoView == null && ll_progressbar == null)
			return;
		CurrentPosition = videoView.getCurrentPosition();
		if (!timerIsStop) {
			if (CurrentPosition <= Positionbuf) {
				Message msg = ManageHandler.obtainMessage();
				msg.what = VISIABLE;
				ManageHandler.sendMessage(msg);
				pauseSeconds++;
			} else {
				Message msg = ManageHandler.obtainMessage();
				msg.what = GONE;
				ManageHandler.sendMessage(msg);
				pauseSeconds = 0;
			}
			Positionbuf = CurrentPosition;
		} else {
			if (CurrentPosition < Positionbuf) {
				Message msg = ManageHandler.obtainMessage();
				msg.what = VISIABLE;
				ManageHandler.sendMessage(msg);
				timerIsStop = false;
				PlayerUnit.instance().setComplete(false);
				pauseSeconds++;
			} else {
				Message msg = ManageHandler.obtainMessage();
				msg.what = GONE;
				ManageHandler.sendMessage(msg);
			}
			Positionbuf = CurrentPosition;
		}
		if (pauseSeconds > 20) {
			pauseSeconds = 0;
			if (CurrentPosition > 20000) {
				currentProcessBuf = CurrentPosition;
				PlayerUnit.instance().setposition(currentProcessBuf);
			}
			ManageHandler.obtainMessage(ERROR, CurrentPosition).sendToTarget();
		}
//		LogUtil.e(Tag, "pauseSeconds=" + pauseSeconds + ";CurrentPosition="
//				+ CurrentPosition);
	}

	ProcessListen listten = new ProcessListen() {

		@Override
		public void onProcessViewStart() {

		}

		@Override
		public void onProcessViewStop() {
			timerIsStop = true;
			Positionbuf = videoView.getDuration();
		}

		@Override
		public void onProcessViewError(int position) {
			ManageHandler.obtainMessage(ERROR, position).sendToTarget();
		}

	};
}
