package com.icloud.listenbook.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;

public class playerService extends Service {

	MediaPlayer player;
	private Timer timer;
	private String path;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		path = intent.getStringExtra("path");
		return new playerControll();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		player = new MediaPlayer();

	}

	class playerControll extends Binder implements playerInterface {

		@Override
		public void play() {
			// TODO Auto-generated method stub
			if (player != null)
				playerService.this.play();
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub
			if (player != null)
				playerService.this.pause();
		}

		@Override
		public void continueplay() {
			// TODO Auto-generated method stub
			if (player != null)
				playerService.this.continueplay();
		}

		@Override
		public void exit() {
			// TODO Auto-generated method stub
			if (player != null) {
				playerService.this.exit();
			}
		}

		@Override
		public void seekTo(int msec) {
			// TODO Auto-generated method stub
			if (player != null)
				player.seekTo(msec);
		}

	}

	// 播放音乐
	public void play() {
		// 重置，必须重置
		player.reset();
		// 加载资源
		try {
			// 访问本地歌曲
			player.setDataSource(path);
			player.setLooping(true);
			// 因为在本地，可以同步准备，没开启子线程
			player.prepare();
			// player.prepareAsync();
			player.start();
			// 开始进度条
			// addtime();

			// //访问网络歌曲
			// player.setDataSource("http://192.168.1.136:8080/dj.mp3");
			// //因为可能存在阻塞情况，开启子线程，必须异步准备，异步准备必须设侦听
			// player.prepareAsync();
			// //侦听是否准备好
			// player.setOnPreparedListener(new OnPreparedListener() {
			//
			// public void onPrepared(MediaPlayer mp) {
			// // TODO Auto-generated method stub
			// player.start();
			// }
			// });
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exit() {
		// 停止播放
		player.stop();
		// 释放资源
		player.release();
		player = null;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void pause() {
		player.pause();
	}

	public void continueplay() {
		player.start();
	}
	// public void addtime(){
	//
	// if(timer==null){
	// timer = new Timer();
	// timer.schedule(new TimerTask() {
	//
	// //这是一个子线程
	// public void run() {
	// //获取音乐最大时长
	// int maxtimes=player.getDuration();
	// //获取音乐当前时长
	// int curtimes=player.getCurrentPosition();
	// Message msg=MainActivity.handler.obtainMessage();
	// Bundle bundle=new Bundle();
	// bundle.putInt("max", maxtimes);
	// bundle.putInt("cur", curtimes);
	// System.out.println("----------"+curtimes);
	// msg.setData(bundle);
	// MainActivity.handler.sendMessage(msg);
	// }
	// //开始计时任务后的5毫秒，第一次执行run方法，以后每1000毫秒执行一次
	// }, 5, 1000);
	// }
	// }
}