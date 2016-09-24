package com.icloud.listenbook.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.ActivityUtils;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;
import com.listenBook.greendao.ArticleChapterInfo;

/**
 * 媒体播放器服务
 * 
 * @author Administrator
 * 
 */
public class MediaPlayerService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		OnBufferingUpdateListener, OnCompletionListener, SurfaceHolder.Callback {
	public static final int Notification_ID = 0x065;
	// 创建一个媒体播放器的对象
	MediaPlayer mp;
	int state;
	LocalBinder localBinder;
	ArticleChapterItem musicInfo;
	Timer mTimer;
	TimerTask mTimerTask;
	int percent;

	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;

	}

	public int getState() {
		return state;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		musicInfo = null;
		localBinder = new LocalBinder();
		initMp();
		registerReceiver();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (isPlaying())
					upState(Configuration.STATE_PLAY);
				if (state == Configuration.STATE_PLAY) {
					sendBoxBroadcast(Configuration.STATE_MUSIC_PLAY_POSTION);
					showNotification(GameApp.instance());
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, 500);

	}

	protected void initMp() {// 当前播放状态
		state = Configuration.STATE_NON;
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnErrorListener(this);
		mp.setOnPreparedListener(this);
		mp.setOnBufferingUpdateListener(this);
		mp.setOnCompletionListener(this);

	}

	protected void registerReceiver() {
		// 注册接收器
		MusicSercieReceiver receiver = new MusicSercieReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Configuration.MEDIA_PLAYER_REVICE_ACTION);
		registerReceiver(receiver, filter);
	}

	protected void sendBoxBroadcast(int state) {
		sendBoxBroadcast(state, new Intent());
	}

	protected void sendBoxBroadcast(int state, Intent intent) {
		intent.putExtra("state", state);
		intent.setAction(Configuration.MEDIA_PLAYER_BOX_ACTION);
		sendBroadcast(intent);
	}

	protected void start(String path) {
		try {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.reset();
			/** 设置数据源 */
			mp.setDataSource(path);
			/** 准备 */
			mp.prepareAsync();
			// } catch (IllegalArgumentException e) {
			// e.printStackTrace();
			// } catch (SecurityException e) {
			// e.printStackTrace();
			// } catch (IllegalStateException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			upState(Configuration.STATE_NON);
			sendBoxBroadcast(state);
		}
	}

	protected synchronized void upState(int state) {
		this.state = state;

	}

	public void showNotification(Context context) {
		if (musicInfo != null) {
			Activity act = ActivityUtils.instance().getCurrAct();
			Class<?> intentClass = TablesActivity.class;
			if ((!act.isFinishing()) && (act instanceof VoiceInfoAct)) {
				intentClass = VoiceInfoAct.class;
			}

			ToolUtils.showNotificationMusic(context, Notification_ID, context
					.getString(R.string.voice_notification_title), String
					.format(context.getString(R.string.voice_notification_msg),
							musicInfo.getCpName()), intentClass);
		}
	}

	protected void start() {
		try {
			if (!mp.isPlaying())
				mp.start();
		} catch (Exception e) {
		}
	}

	protected void stop() {
		try {
			mp.stop();
		} catch (Exception e) {
		}
	}

	protected void pase() {
		try {
			if (mp.isPlaying())
				mp.pause();
		} catch (Exception e) {
		}
	}

	protected void seekTo(int postion) {
		try {
			mp.seekTo(postion);
		} catch (Exception e) {
		}

	}

	public boolean isPlaying() {
		try {
			
			return mp != null && mp.isPlaying();
		} catch (Exception e) {
		}
		return false;
	}

	public ArticleChapterInfo getInfo() {
		return musicInfo;
	}

	public int getCurrentPosition() {
		try {
			return mp.getCurrentPosition();
		} catch (Exception e) {
		}
		return 0;
	}

	public int getDuration() {
		try {
			return mp.getDuration();
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			mp.stop();
			mp.reset();
			mp.release();
		} catch (Exception e) {
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Intent intent = new Intent();
		upState(Configuration.STATE_PLAY);
		sendBoxBroadcast(Configuration.STATE_MUSIC_PREPARED, intent);
		try {
			mp.start();
		} catch (Exception e) {
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		try {
			mp.release();
		} catch (Exception e) {

		}
		upState(Configuration.STATE_NON);
		sendBoxBroadcast(state);
		ToolUtils.delNotification(GameApp.instance(), Notification_ID);
		return false;
	}

	/** 加载进度 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Intent intent = new Intent();
		this.percent = percent;
		intent.putExtra("percent", percent);
		sendBoxBroadcast(Configuration.STATE_MUSIC_LOADING_POS, intent);
	}

	/*** 播放完毕 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		upState(Configuration.STATE_END);
		sendBoxBroadcast(Configuration.STATE_MUSIC_COMPLETION);
		ToolUtils.delNotification(GameApp.instance(), Notification_ID);
	}

	public void loadPercent(String url) {
		if (FileUtils.isExists(url)) {
			percent = 100;
			Intent intent = new Intent();
			intent.putExtra("percent", percent);
			sendBoxBroadcast(Configuration.STATE_MUSIC_LOADING_POS, intent);
		} else {
			percent = 0;
		}

	}

	class MusicSercieReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			int control = intent.getIntExtra("state", -1);
			ToolUtils.delNotification(context, Notification_ID);
			switch (control) {// 播放音乐
			case Configuration.STATE_PLAY: {
				/** 暂停且 链接为空 恢复播放 **/
				String url = intent.getStringExtra("url");
				if ((state == Configuration.STATE_PAUSE || state == Configuration.STATE_END)
						&& TextUtils.isEmpty(url) && musicInfo != null) {
					start();
					upState(Configuration.STATE_PLAY);
					showNotification(context);
				} else if (!TextUtils.isEmpty(url)) {
					String value = intent.getStringExtra("data");
					musicInfo = JsonUtils.fromJson(value,
							ArticleChapterItem.class);
					start(url);
					loadPercent(url);
					upState(Configuration.STATE_PLAY);
					showNotification(context);
				} else {
					upState(Configuration.STATE_NON);
				}

			}
				break;// 暂停播放
			case Configuration.STATE_STOP:
			case Configuration.STATE_PAUSE: {
				if (state == Configuration.STATE_PLAY || isPlaying()) {
					pase();
					upState(Configuration.STATE_PAUSE);
				}
			}
				break;// 停止播放
			// case Configuration.STATE_STOP: {
			// if (state == Configuration.STATE_PLAY
			// || state == Configuration.STATE_PAUSE) {
			// stop();
			// upState(Configuration.STATE_STOP);
			// }
			// }
			// break;// 停止播放
			case Configuration.STATE_SEEKTO: {
				int postion = intent.getIntExtra("postion", 0);
				if (state == Configuration.STATE_PAUSE
						|| state == Configuration.STATE_PLAY) {
					seekTo(postion);
				} else {
					return;
				}
			}
				break;
			default:
				break;
			}
			sendBoxBroadcast(state);
		}
	}

	public class LocalBinder extends Binder {
		public MediaPlayerService getService() {
			return MediaPlayerService.this;
		}
	}

	private SurfaceHolder surfaceHolder;

	public void setSurfaceView(SurfaceView surfaceView) {
		if (null != surfaceView) {
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.setKeepScreenOn(true);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			surfaceHolder.addCallback(this);
			try {
				mp.setDisplay(surfaceHolder);
			} catch (Exception e) {
			}

		} else {
			try {
				mp.setDisplay(null);
			} catch (Exception e) {
			}
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			mp.setDisplay(null);
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

}
