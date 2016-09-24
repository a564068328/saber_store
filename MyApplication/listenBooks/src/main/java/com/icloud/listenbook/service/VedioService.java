package com.icloud.listenbook.service;

import com.icloud.listenbook.service.MusicService.LocalBinder;
import com.icloud.listenbook.unit.Configuration;

import android.os.Binder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VedioService extends MusicService implements
		SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;

	@Override
	protected String getSendBroadcastAction() {
		return Configuration.VEDIO_BOX_ACTION;
	}

	@Override
	protected String getRegisterReceiverAction() {
		return Configuration.VEDIO_REVICE_ACTION;
	}

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

	@Override
	public void onCreate() {
		super.onCreate();
		localBinder = new LocalBinder();
	}

	public class LocalBinder extends
			com.icloud.listenbook.service.MusicService.LocalBinder {
		@Override
		public VedioService getService() {
			return VedioService.this;
		}
	}
}
