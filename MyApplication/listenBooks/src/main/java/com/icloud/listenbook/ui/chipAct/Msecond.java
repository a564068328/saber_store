package com.icloud.listenbook.ui.chipAct;

import java.io.File;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.DownFileUtils;
import com.icloud.listenbook.service.playerInterface;
import com.icloud.listenbook.service.playerService;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Msecond extends BaseActivity implements OnClickListener {
	private static final String Url = "http://www.xuetong111.com/audiocdn/忏悔歌.mp3";
//	private static final int ISLOADING = 1;
//	private static final int ISFINISH = ISLOADING + 1;
	TextView btn_adult;
	TextView btn_children;
	View back;
	Intent intent;
	playerInterface pi;
	private myBindService conn;
	ToggleButton toggleBtn;
	TextView tv_musicloading;
	View tv;
//	private mHandler handler;
	private String target;

	@Override
	public void init() {
		target = Environment.getExternalStorageDirectory()
				+ "/com.icloud.listenbook/" + "忏悔歌.mp3";
//		handler = new mHandler(this);
		
	}

	public void isPlay() {
		if(DownFileUtils.fileIsExist(target)){
			SharedPreferenceUtil.setMP3IsExist(true);
		}else{
			SharedPreferenceUtil.setMP3IsExist(false);
		}
		if (SharedPreferenceUtil.getMP3IsExist()) {
			if (SharedPreferenceUtil.getSET_PLAY_MP3()) {
				connectService();
			}
		} else {
			downMP3();
		}
	}

	public void connectService() {
		intent = new Intent(this, playerService.class);
		intent.putExtra("path", Environment.getExternalStorageDirectory()
				+ "/com.icloud.listenbook/" + "忏悔歌.mp3");
		startService(intent);
		conn = new myBindService();
		bindService(intent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		exit();
	}

	@Override
	public int getLayout() {
		return R.layout.act_msecond;
	}

	@Override
	public void findViews() {
		btn_adult = (TextView) findViewById(R.id.btn_adult);
		btn_children = (TextView) findViewById(R.id.btn_children);
		toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
		back = findViewById(R.id.back);
		tv_musicloading = (TextView) findViewById(R.id.tv_musicloading);
		tv = findViewById(R.id.tv);
		isPlay();
		if (SharedPreferenceUtil.getMP3IsExist()) {
			if (SharedPreferenceUtil.getSET_PLAY_MP3())
				toggleBtn.setToggleOn();
			else
				toggleBtn.setToggleOff();
		} else {
			tv_musicloading.setVisibility(View.VISIBLE);
			toggleBtn.setVisibility(View.GONE);
		}
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		btn_adult.setOnClickListener(this);
		btn_adult.setOnTouchListener(ViewUtils.instance().onTouchListener);
		btn_children.setOnClickListener(this);
		btn_children.setOnTouchListener(ViewUtils.instance().onTouchListener);
		toggleBtn.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				if (on) {
					if (SharedPreferenceUtil.getMP3IsExist()) {
						if (conn != null) {
							unbindService(conn);
						}
						if (intent != null) {
							stopService(intent);
						}
						SharedPreferenceUtil.setSET_PLAY_MP3(true);
						connectService();
					} else {
						//mp3不存在的话
						toggleBtn.setToggleOff();
					}
				} else {
					SharedPreferenceUtil.setSET_PLAY_MP3(false);
					if (pi != null)
						pi.exit();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.btn_adult:
			if (UserInfo.instance().isLogin()) {
				Intent intent = new Intent(this, ThirdAdult.class);
				intent.putExtra("isAdult", true);
				LoadingTool.launchActivity(this, ThirdAdult.class, intent);
			} else {
				LoadingTool.launchActivity(this, LoginAct.class);
			}
			break;
		case R.id.btn_children:
			if (UserInfo.instance().isLogin()) {
				Intent intent = new Intent(this, ThirdAdult.class);
				intent.putExtra("isAdult", false);
				LoadingTool.launchActivity(this, ThirdAdult.class, intent);
			} else {
				LoadingTool.launchActivity(this, LoginAct.class);
			}
			break;

		}
	}

	class myBindService implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtil.e("onServiceConnected", "连接服务\n");
			pi = (playerInterface) service;
			play();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	}

	public void play() {
		if (pi != null)
			pi.play();
	}

	public void pause() {
		if (pi != null)
			pi.pause();
	}

	public void seekTo(int msc) {
		if (pi != null)
			pi.seekTo(msc);
	}

	public void continueplay() {
		if (pi != null)
			pi.continueplay();
	}

	public void exit() {
		if (conn != null) {
			unbindService(conn);
		}
		if (intent != null) {
			stopService(intent);
		}
		if (pi != null) {
			pi.exit();
		}
	}

	public void downMP3() {
		// 判断手机内存是否够下载文件
		if (DownFileUtils.canDownload(this, Url)) {
			HttpUtils utils = new HttpUtils();
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/com.icloud.listenbook");
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(target);
			utils.download(Url, target, true, true,
					new RequestCallBack<File>() {
						@Override
						public void onStart() {
						}

						public void onLoading(long total, long current,
								boolean isUploading) {
							int percent = (int) (current * 100 / total);
//							Message.obtain(handler, ISLOADING, percent)
//									.sendToTarget();
							tv_musicloading.setText("已加载"+percent+"%");
						}

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
//							Message.obtain(handler, ISFINISH).sendToTarget();
							SharedPreferenceUtil.setMP3IsExist(true);
							SharedPreferenceUtil.setSET_PLAY_MP3(true);
							toggleBtn.setVisibility(View.VISIBLE);
							toggleBtn.setToggleOn();
							tv_musicloading.setVisibility(View.GONE);
							connectService();
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
						}
					});
		}
	}

//	public static class mHandler extends Handler {
//		WeakReference<Activity> WRact;
//
//		public mHandler(Activity act) {
//			WRact = new WeakReference<Activity>(act);
//		}
//
//		public void handleMessage(android.os.Message msg) {
//			Msecond act = (Msecond) WRact.get();
//			int what = msg.what;
//			switch (what) {
//			case ISLOADING:
//				act.tv_musicloading.setText("已加载"+(Integer)msg.obj);
//				break;
//			case ISFINISH:
//				SharedPreferenceUtil.setMP3IsExist(true);
//				SharedPreferenceUtil.setSET_PLAY_MP3(true);
//				act.toggleBtn.setVisibility(View.VISIBLE);
//				act.tv_musicloading.setVisibility(View.GONE);
//				act.connectService();
//				break;
//			default:
//				break;
//			}
//		}
//	}
}
