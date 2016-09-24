package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.WarningDlg;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.entity.TtsInfo;
import com.icloud.listenbook.ui.custom.ColorArcProgressBar;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;

public class TtsAct extends BaseActivity implements OnClickListener {
	public final static String TTS_SET_CHANGE = "TTS_SET_CHANGE";

	// 语音合成对象
	private SpeechSynthesizer mTts;

	private enum Status {
		play, pause, complete, error
	};

	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
	private int count = 0;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private ImageView music_play;
	private View back;
	private View music_up;
	private View music_down;
	private View iv_setting;
	private ColorArcProgressBar buf_process;
	private ColorArcProgressBar play_process;
	TextView tv_body;
	TextView tv_title;
	public ArrayList<TtsInfo> datas;
	private String currentChant;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		// 开始合成
		// 收到onCompleted 回调时，合成结束、生成合成音频
		// 合成的音频格式：只支持pcm格式
		case R.id.music_play:
			if (status == Status.complete) {
				play();
			} else if (status == Status.play) {
				status = Status.pause;
				mTts.pauseSpeaking();
				music_play.setBackgroundResource(R.drawable.icon_music_play);
			} else if (status == Status.pause) {
				status = Status.play;
				mTts.resumeSpeaking();
				music_play.setBackgroundResource(R.drawable.icon_music_stop);
			} else if (status == Status.error) {
				play_process.setCurrentValues(0);
				buf_process.setCurrentValues(0);
				play();
			}
			break;
		case R.id.back:
			finish();
			break;
		case R.id.music_up:
			position++;
			setNextOrPreviousView();
			setText();
			play();
			break;
		case R.id.music_down:
			position--;
			setNextOrPreviousView();
			setText();
			play();
			break;
		case R.id.iv_setting:
			Intent intent = new Intent(this, TtsSettingAct.class);
			startActivity(intent);
			break;
		}
	}

	private void play() {
		if ((!SharedPreferenceUtil.isTtsNoWifiLook())
				&& HttpUtils.isNetworkAvailable(this)
				&& !HttpUtils.isWifi(this)) {
			DialogManage
					.showWarningDlg(
							this,
							R.string.warning,
							R.string.tts_no_wifi_warning,
							R.string.no_tip,
							R.string.cancel,
							R.string.continue_tip,
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									if (dialog instanceof WarningDlg
											&& ((WarningDlg) dialog)
													.isCheck()) {
										SharedPreferenceUtil
												.saveTtsNoWifiLook(true);
									}
									dialog.dismiss();
									music_play.setBackgroundResource(R.drawable.icon_music_stop);
									status = Status.play;
									playEvent();
								}

							});
		} else {
			music_play.setBackgroundResource(R.drawable.icon_music_stop);
			status = Status.play;
			playEvent();
		}
	}

	private void playEvent() {
		// 移动数据分析，收集开始合成事件
		FlowerCollector.onEvent(TtsAct.this, "tts_play");
		// 设置参数
		setParam();
		int code = mTts.startSpeaking(currentChant, mTtsListener);
		// /**
		// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
		// * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
		// */
		// String path =
		// Environment.getExternalStorageDirectory()+"/tts.pcm";
		// int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
				// mInstaller.install();
			} else {
				ToastUtil.showMessage("语音合成失败,错误码: " + code);
			}
		}
	}

	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(Tag, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				ToastUtil.showMessage("初始化失败,错误码：" + code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {

		}

		@Override
		public void onSpeakPaused() {

		}

		@Override
		public void onSpeakResumed() {

		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;

			buf_process.setCurrentValues(mPercentForBuffering);

		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			count++;
			if (count == 20) {
				count = 0;
				play_process.setCurrentValues(mPercentForPlaying);

			}
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				status = Status.complete;
				music_play.setBackgroundResource(R.drawable.icon_music_play);
				mPercentForPlaying = 100;
				play_process.setCurrentValues(mPercentForPlaying);
				ToastUtil.showMessage("播放完成");
			} else if (error != null) {
				ToastUtil.showMessage(error.getPlainDescription(true));
				status = Status.error;
				music_play.setBackgroundResource(R.drawable.icon_music_play);
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	private Status status;
	private String body;
	private String date;
	private int position;

	private setChangrReceiver receiver;

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(
					SpeechConstant.VOICE_NAME,
					getResources().getStringArray(R.array.voicer_cloud_values)[SharedPreferenceUtil
							.getTtsVoice()]);
			// 设置合成语速
			mTts.setParameter(SpeechConstant.SPEED,
					SharedPreferenceUtil.getTtsSpeed());
			// 设置合成音调
			mTts.setParameter(SpeechConstant.PITCH, "50");
			// 设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME,
					SharedPreferenceUtil.getTtsVolumn());
		} else {
			// mTts.setParameter(SpeechConstant.ENGINE_TYPE,
			// SpeechConstant.TYPE_LOCAL);
			// // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			// mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			/**
			 * TODO 本地合成不设置语速、音调、音量，默认使用语记设置 开发者如需自定义参数，请参考在线合成参数设置
			 */
		}
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(this);
		FlowerCollector.onPageStart(Tag);
		super.onResume();
	}

	private class setChangrReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
//			mTts.setParameter(
//					SpeechConstant.VOICE_NAME,
//					getResources().getStringArray(R.array.voicer_cloud_values)[SharedPreferenceUtil
//							.getTtsVoice()]);
//			mTts.setParameter(SpeechConstant.SPEED,
//					SharedPreferenceUtil.getTtsSpeed());
//			mTts.setParameter(SpeechConstant.VOLUME,
//					SharedPreferenceUtil.getTtsVolumn());
			setText();
			play();
		}

	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd(Tag);
		FlowerCollector.onPause(this);
		super.onPause();
	}

	@Override
	public void init() {
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(TtsAct.this,
				mTtsInitListener);
		position = getIntent().getIntExtra("position", 0);
		IntentFilter filter = new IntentFilter();
		filter.addAction(TTS_SET_CHANGE);
		receiver = new setChangrReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public int getLayout() {
		return R.layout.act_tts;
	}

	@Override
	public void findViews() {
		music_play = (ImageView) findViewById(R.id.music_play);
		buf_process = (ColorArcProgressBar) findViewById(R.id.buf_process);
		play_process = (ColorArcProgressBar) findViewById(R.id.play_process);
		tv_body = (TextView) findViewById(R.id.tv_body);
		tv_title = (TextView) findViewById(R.id.tv_title);
		back = findViewById(R.id.back);
		music_up = findViewById(R.id.music_up);
		music_down = findViewById(R.id.music_down);
		iv_setting = findViewById(R.id.iv_setting);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		music_play.setOnClickListener(this);
		music_up.setOnClickListener(this);
		music_down.setOnClickListener(this);
		iv_setting.setOnClickListener(this);
		setNextOrPreviousView();
		setText();
	}

	private void setNextOrPreviousView() {
		if (position + 1 < ChantAct.datas_chant.size()) {
			music_up.setVisibility(View.VISIBLE);
		} else {
			music_up.setVisibility(View.INVISIBLE);
		}
		if (position - 1 >= 0) {
			music_down.setVisibility(View.VISIBLE);
		} else {
			music_down.setVisibility(View.INVISIBLE);
		}
	}

	private void setText() {
		mTts.stopSpeaking();
		body = ChantAct.datas_chant.get(position).chant;
		date = ChantAct.datas_chant.get(position).date;
		tv_title.setText(transformString(date));
		tv_body.setText(body);
		play_process.setCurrentValues(0);
		buf_process.setCurrentValues(0);
		status = Status.complete;
		music_play.setBackgroundResource(R.drawable.icon_music_play);
		currentChant = body;                                                                                                                                                                                                                                                                                                           
	}


	public String transformString(String date) {
		StringBuilder builder = new StringBuilder(date.substring(4).toString());
		builder.insert(2, "月").insert(5, "日").append("语音朗读");
		return builder.toString();
	}

}
