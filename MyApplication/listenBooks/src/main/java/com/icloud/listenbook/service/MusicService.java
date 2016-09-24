package com.icloud.listenbook.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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

import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.listenBook.greendao.ArticleChapterInfo;

public class MusicService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		OnBufferingUpdateListener, OnCompletionListener {
	// 创建一个媒体播放器的对象
	MediaPlayer mp;
	int state;
	LocalBinder localBinder;
	ArticleChapterItem musicInfo;
	Timer mTimer;
	TimerTask mTimerTask;

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
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (state == Configuration.STATE_PLAY) {
					sendBoxBroadcast(Configuration.STATE_MUSIC_PLAY_POSTION);
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, 500);
		initMp();
		registerReceiver();

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
		filter.addAction(getRegisterReceiverAction());
		registerReceiver(receiver, filter);
	}

	protected String getRegisterReceiverAction() {
		return Configuration.MUSICS_REVICE_ACTION;
	}

	protected String getSendBroadcastAction() {
		return Configuration.MUSIC_BOX_ACTION;
	}

	protected void sendBoxBroadcast(int state) {
		sendBoxBroadcast(state, new Intent());
	}

	protected void sendBoxBroadcast(int state, Intent intent) {
		intent.putExtra("state", state);
		intent.setAction(getSendBroadcastAction());
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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
			return mp.isPlaying();
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
		return false;
	}

	/** 加载进度 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Intent intent = new Intent();
		intent.putExtra("percent", percent);
		sendBoxBroadcast(Configuration.STATE_MUSIC_LOADING_POS, intent);
	}

	/*** 播放完毕 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		state = Configuration.STATE_END;
		sendBoxBroadcast(Configuration.STATE_MUSIC_COMPLETION);
	}

	class MusicSercieReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int control = intent.getIntExtra("state", -1);

			switch (control) {// 播放音乐
			case Configuration.STATE_PLAY: {
				/** 暂停且 链接为空 恢复播放 **/
				String url = intent.getStringExtra("url");
				if ((state == Configuration.STATE_PAUSE || state == Configuration.STATE_END)
						&& TextUtils.isEmpty(url) && musicInfo != null) {
					start();
				} else if (!TextUtils.isEmpty(url)) {
					String value = intent.getStringExtra("data");
					musicInfo = JsonUtils.fromJson(value,
							ArticleChapterItem.class);
					start(url);

				} else {
					return;
				}
				state = Configuration.STATE_PLAY;
			}
				break;// 暂停播放
			case Configuration.STATE_PAUSE: {
				if (state == Configuration.STATE_PLAY) {
					pase();
					state = Configuration.STATE_PAUSE;
				} else {
					return;
				}
			}
				break;// 停止播放
			case Configuration.STATE_STOP: {
				if (state == Configuration.STATE_PLAY
						|| state == Configuration.STATE_PAUSE) {
					stop();
					state = Configuration.STATE_STOP;
				} else {
					return;
				}
			}
				break;// 停止播放
			case Configuration.STATE_SEEKTO: {
				int postion = intent.getIntExtra("postion", 0);
				seekTo(postion);
			}
				break;
			default:
				return;

			}
			sendBoxBroadcast(state);
		}
	}

	public class LocalBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

}
