package com.icloud.listenbook.connector;

import java.io.IOException;

import com.listenBook.greendao.Article;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoViewManage implements OnErrorListener, SurfaceHolder.Callback {
	static VideoViewManage instance;
	/** 加载进度 **/
	public int loadPos = 0;
	public int playPos;
	public boolean isFirstLoad;

	public static VideoViewManage instance() {
		if (instance == null)
			instance = new VideoViewManage();
		return instance;
	}

	private SurfaceView surfaceView;// surfaceVie对象
	private MediaPlayer player;// mediaplayer对象
	private SurfaceHolder surfaceHolder;
	Article article;

	public void setArticle(Article article) {
		this.article = article;
	}

	public Article getArticle() {
		return article;
	}

	private VideoViewManage() {
		article = null;
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnErrorListener(this);
	}

	

	public int getDuration() {
		try {
			return player.getDuration();
		} catch (Exception e) {
		}
		return 0;
	}

	public void play(String url) {
		if (android.text.TextUtils.isEmpty(url))
			return;
		try {
			if (player.isPlaying()) {
				player.stop();
			}
			player.reset();
			// player.release();
			player.setDataSource(url);
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void pause() {
		try {
			if (player.isPlaying()) {
				player.pause();
			}
		} catch (Exception e) {

		}
	}

	public boolean isPlaying() {
		try {
			return player.isPlaying();
		} catch (Exception e) {
		}

		return false;
	}

	public void reStart() {
		try {
			if (!player.isPlaying()) {
				player.start();
			}
		} catch (Exception e) {

		}
	}

	public void play() {
		try {
			if (!player.isPlaying()) {
				player.start();
			}
		} catch (Exception e) {

		}
	}

	public void stop() {
		try {
			if (player.isPlaying()) {
				player.stop();

			}
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		try {
			player.release();
		} catch (Exception e) {

		}

		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			player.setDisplay(surfaceHolder);
		} catch (Exception e) {
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		try {
			player.setDisplay(null);
		} catch (Exception e) {
		}
	}

	public void setOnPreparedListener(OnPreparedListener list) {
		player.setOnPreparedListener(list);

	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener list) {
		player.setOnBufferingUpdateListener(list);

	}

	public void setOnCompletionListener(OnCompletionListener list) {
		player.setOnCompletionListener(list);

	}

	public int getCurrentPosition() {
		try {
			return player.getCurrentPosition();
		} catch (Exception e) {
		}
		return 0;
	}

	public void seekTo(int i) {
		try {
			player.seekTo(i);
		} catch (Exception e) {
		}

	}

}
