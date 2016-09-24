package com.icloud.listenbook.connector;

import java.io.IOException;

import com.listenBook.greendao.Article;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class VoiceManage implements MediaPlayer.OnErrorListener,
		OnPreparedListener {
	static VoiceManage instance;
	MediaPlayer player;
	/** 加载进度 **/
	public int loadPos = 0;
	public int playPos;
	Article article;
	OnPreparedListener preparedListener;

	public void setArticle(Article article) {
		this.article = article;
	}

	public Article getArticle() {
		return article;
	}

	public static VoiceManage instance() {
		if (instance == null)
			instance = new VoiceManage();
		return instance;
	}

	public MediaPlayer getMediaPlayer() {
		return player;
	}

	private VoiceManage() {
		article = null;
		player = new MediaPlayer();
		player.setOnErrorListener(this);
		player.setOnPreparedListener(this);
	}

	public boolean isPlaying() {
		try {
			return player.isPlaying();
		} catch (Exception e) {
			return false;
		}
	}

	public void play(String path) {
		if (android.text.TextUtils.isEmpty(path))
			return;
		try {
			if (player.isPlaying()) {
				player.stop();
			}
			player.reset();
			/** 设置数据源 */
			player.setDataSource(path);
			/** 准备 */
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
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

	public void reStart() {
		try {
			if (!player.isPlaying()) {
				player.start();
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

	public void setOnPreparedListener(OnPreparedListener list) {
		this.preparedListener = list;
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

	public int getDuration() {
		try {
			return player.getDuration();
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

	@Override
	public void onPrepared(MediaPlayer mp) {
		player.start();
		if (preparedListener != null)
			preparedListener.onPrepared(mp);
	}

}
