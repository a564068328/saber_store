package com.icloud.listenbook.unit;

import java.util.List;
import java.util.Timer;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.qcloud.player.CallBack;
import com.qcloud.player.PlayerConfig;
import com.qcloud.player.VideoInfo;
import com.qcloud.player.ui.QCloudVideoView;
import com.qcloud.player.util.QLog;
import com.qcloud.player.util.SimpleLogger;

public class PlayerUnit {
	protected static PlayerUnit instance;
	private static final QLog logger = new SimpleLogger();
	private Context context;
	private LinearLayout ll;
	QCloudVideoView videoView;
	Timer timer;
	LinearLayout ll_playerror;
	Handler ManageHandler;
	int position;
	long cpid;
	boolean isJump = false;
	boolean isComplete = false;

	public void upCpid(long cpid) {
		this.cpid = cpid;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isJump() {
		return isJump;
	}

	public void setJump(boolean isJump) {
		this.isJump = isJump;
	}

	public static PlayerUnit instance() {
		if (instance == null) {
			instance = new PlayerUnit();
		}
		return instance;
	}

	private PlayerUnit() {
	}

	public void init(Context context) {
		// 使用前需要初始化PlayerConfig，整个应用初始化一次即可，传入一个context和用户的userkey
		PlayerConfig.init(context.getApplicationContext(), "1251007212");
		// 注册事件回调函数
		PlayerConfig.g().registerCallback(callBack);
		// 注册log类，需实现QLog接口，如果没有注册，默认使用Android.util.Log
		PlayerConfig.g().registerLogger(logger);
	}

	/**
	 * 回调的实现
	 */
	private CallBack callBack = new CallBack() {

		private int track;

		@Override
		public void onEvent(int what, String msg, VideoInfo videoInfo) {
			/*
			 * 判断消息的类型，目前支持下类型： 视频播放开始 EVENT_PLAY_START 视频播放资源被回收
			 * EVENT_PLAY_STOP 视频播放到结尾 EVENT_PLAY_COMPLETE 视频播放暂停
			 * EVENT_PLAY_PAUSE 视频播放继续 EVENT_PLAY_RESUME 播放出错 EVENT_PLAY_ERROR
			 * 退出播放 EVENT_PLAY_EXIT
			 */

			String TAG = this.getClass().getName();
			if (what == CallBack.EVENT_PLAY_ERROR) {
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);

				List<RunningTaskInfo> list = am.getRunningTasks(1);
				if (list != null && list.size() > 0) {
					ComponentName cpn = list.get(0).topActivity;
					if ("com.icloud.listenbook.ui.chipAct.VedioInfoAct"
							.equals(cpn.getClassName())) {
						track = videoInfo.getCurrentPosition();
						position=track;
//						IoUtils.instance().saveReadingTrack(cpid, track);
						LogUtil.e(TAG, "play error....:msg=" + msg+",track="+track);
						listten.onProcessViewError(track);
					}
				}
				
			} else if (what == EVENT_PLAY_START) {
				LogUtil.e(TAG, "play play srart...." + msg);
				if (context == null || ll == null) {
					LogUtil.e(TAG, "context或ll为空....\n");
					return;
				}
				/**
				 * 判断com.icloud.listenbook.ui.chipAct.VedioInfoAct是不是前台ACTIVITY
				 */
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);

				List<RunningTaskInfo> list = am.getRunningTasks(1);
				if (list != null && list.size() > 0) {
					ComponentName cpn = list.get(0).topActivity;
					if ("com.icloud.listenbook.ui.chipAct.VedioInfoAct"
							.equals(cpn.getClassName())) {
						ll.setVisibility(View.GONE);
						track = IoUtils.instance().getReadingTrack(cpid);
						// LogUtil.e(TAG, "track" + track+"aid"+cpid);
						if (track > 5000) {
							ManageHandler.obtainMessage(
									VedioPlayView.JUMP_IS_ENABLE, track)
									.sendToTarget();
						}
					}
				}
			} else if (what == EVENT_PLAY_STOP) {
				track = videoInfo.getCurrentPosition();
				LogUtil.e(TAG, "视频播放资源被回收track" + track + "aid" + cpid);
				if (!isComplete) {
					IoUtils.instance().saveReadingTrack(cpid, track);
				} else {
					isComplete = false;
				}
				LogUtil.e(TAG, "视频播放资源被回收...." + msg);
			} else if (what == EVENT_PLAY_COMPLETE) {
				IoUtils.instance().saveReadingTrack(cpid, 0);
				isComplete = true;
				listten.onProcessViewStop();
				LogUtil.e(TAG, "视频播放到结尾...." + msg);
			} else if (what == EVENT_PLAY_PAUSE) {
				track = videoInfo.getCurrentPosition();
				IoUtils.instance().saveReadingTrack(cpid, track);
				LogUtil.e(TAG, "视频播放暂停....+" + videoInfo.getCurrentPosition());
			} else if (what == EVENT_PLAY_RESUME) {
				LogUtil.e(TAG, "视频播放继续...." + msg);
			} else if (what == CallBack.EVENT_PLAY_POSITION_OUT_OF_BOUND) {
				LogUtil.e(TAG, "EVENT_PLAY_POSITION_OUT_OF_BOUND....\n");
			}

			// .......
		}
	};

	public void getView(QCloudVideoView videoView, LinearLayout ll,
			Timer timer, LinearLayout ll_playerror, Handler ManageHandler,
			long cpid) {
		this.context = GameApp.getContext();
		this.videoView = videoView;
		this.ll = ll;
		this.timer = timer;
		this.ll_playerror = ll_playerror;
		this.ManageHandler = ManageHandler;
		this.cpid = cpid;
	}

	public void releaseView() {
		this.context = null;
		this.videoView = null;
		this.ll = null;
		this.timer = null;
		this.ll_playerror = null;
		this.ManageHandler = null;
		this.cpid = 0;
		this.listten = null;
	}

	public int getposition() {
		return position;
	}
	public void setposition(int position) {
		this.position=position;
	}
	public interface ProcessListen {
		void onProcessViewStart();

		void onProcessViewStop();

		void onProcessViewError(int position);
	}

	public ProcessListen listten;

	public void setProcessListen(ProcessListen listten) {
		this.listten = listten;
	};
}
