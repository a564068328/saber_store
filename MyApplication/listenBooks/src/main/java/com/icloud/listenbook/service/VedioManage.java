package com.icloud.listenbook.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.SurfaceView;

import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.listenBook.greendao.ArticleChapterInfo;

public class VedioManage {
	VedioService musicService;
	Activity act;
	VedioStateListener list;
	MusicBoxReceiver mReceiver;

	public VedioManage(Activity act) {
		this.act = act;
		mReceiver = new MusicBoxReceiver();
		bind();// 注册接收器

	}

	/** 注册音乐播放监听器 */
	protected void rReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Configuration.VEDIO_BOX_ACTION);
		act.registerReceiver(mReceiver, filter);
	}

	/** 注册音乐播放监听器 */
	protected void uReceiver() {
		act.unregisterReceiver(mReceiver);
	}

	protected void bind() {
		Intent intent = new Intent(act, VedioService.class);
		/** 进入Activity开始服务 */
		act.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	public void unbind() {
		act.unbindService(conn);
	}

	private ServiceConnection conn = new ServiceConnection() {
		/** 获取服务对象时的操作 */
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicService = ((VedioService.LocalBinder) service).getService();
			if (musicService != null && list != null) {
				Intent intent = new Intent();
				intent.putExtra("state", Configuration.STATE_MUSIC_BIND_END);
				list.upState(intent);
			}
		}

		/** 无法获取到服务对象时的操作 */
		public void onServiceDisconnected(ComponentName name) {
			musicService = null;
		}

	};

	public int getCurrentPosition() {
		if (musicService != null)
			return musicService.getCurrentPosition();
		return 0;
	}

	public int getDuration() {
		if (musicService != null)
			return musicService.getDuration();
		return 0;
	}

	public boolean isPause() {
		return musicService.getState() == Configuration.STATE_PAUSE;

	}

	public boolean isPlaying() {
		if (musicService != null)
			return musicService.isPlaying();
		return false;
	}

	public void seekTo(int postion) {
		Intent intent = new Intent();
		intent.putExtra("postion", postion);
		sendBroadcastToService(Configuration.STATE_SEEKTO, intent);
	}

	public void start(String url, ArticleChapterItem item) {
		Intent intent = new Intent();
		intent.putExtra("url", url);
		intent.putExtra("data", JsonUtils.toJson(item));
		sendBroadcastToService(Configuration.STATE_PLAY, intent);
	}

	public void start() {
		sendBroadcastToService(Configuration.STATE_PLAY);
	}

	public void pause() {
		sendBroadcastToService(Configuration.STATE_PAUSE);
	}

	public void stop() {
		sendBroadcastToService(Configuration.STATE_STOP);
	}

	public ArticleChapterInfo getInfo() {
		if (musicService != null)
			return musicService.getInfo();
		return null;
	}

	/**
	 * 向后台Service发送控制广播
	 * 
	 * @param state
	 *            int state 控制状态码
	 * */
	protected void sendBroadcastToService(int state, Intent intent) {
		intent.putExtra("state", state);
		intent.setAction(Configuration.VEDIO_REVICE_ACTION);
		// 向后台Service发送播放控制的广播
		act.sendBroadcast(intent);
	}// 创建一个广播接收器用于接收后台Service发出的广播

	protected void sendBroadcastToService(int state) {
		Intent intent = new Intent();
		sendBroadcastToService(state, intent);
	}

	class MusicBoxReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (list != null)
				list.upState(intent);
		}
	}

	public void addMusicList(VedioStateListener list) {
		this.list = list;
		if (list != null) {
			rReceiver();
		} else {
			uReceiver();
		}
	}

	public boolean isLoadData() {
		return musicService.getState() == Configuration.STATE_PAUSE
				|| musicService.getState() == Configuration.STATE_PLAY;
	}

	public interface VedioStateListener {
		public void upState(Intent intent);
	}

	public void setSurfaceView(SurfaceView surfaceView) {
		if (null != musicService)
			musicService.setSurfaceView(surfaceView);
	}

}
