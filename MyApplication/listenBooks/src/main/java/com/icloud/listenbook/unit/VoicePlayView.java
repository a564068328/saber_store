package com.icloud.listenbook.unit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.connector.MediaPlayerManage;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.dialog.WarningDlg;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.service.MPManage;
import com.icloud.listenbook.service.MPManage.MediaPlayerStateListener;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;
import com.listenBook.greendao.ArticleChapterInfo;

public class VoicePlayView implements MediaPlayerManage,
		MediaPlayerStateListener, OnClickListener,
		SeekBar.OnSeekBarChangeListener {
	static final int UP_END_TIME = 1000;
	static final int UP_TIME_POS = UP_END_TIME + 1;
	static final int UP_INFO_ADD_END = UP_TIME_POS + 1;
	public String Tag = this.getClass().getName();
	SeekBar playPosBar;

	int maxPos;
	TextView sumTime;
	TextView playTime;

	VoiceInfoAct act;
	ImageView musicPlay;
	ImageView musicUp;
	ImageView musicDown;
	MusicListener musicListener;
	MPManage musicManage;
	int playPos;
	int loadPos;

	public VoicePlayView(VoiceInfoAct act) {
		this.act = act;
		init();
		findViews();
		setListeners();
		setDatas();
	}

	protected void init() {
		playPos = -1;
		musicManage = new MPManage(act);
		musicManage.setSurfaceView(null);
	}

	protected void findViews() {
		playPosBar = (SeekBar) findViewById(R.id.play_pos);
		sumTime = (TextView) findViewById(R.id.sum_time);
		playTime = (TextView) findViewById(R.id.play_time);

		musicPlay = (ImageView) findViewById(R.id.music_play);
		musicUp = (ImageView) findViewById(R.id.music_up);
		musicDown = (ImageView) findViewById(R.id.music_down);

	}

	protected void setDatas() {
		musicPlay
				.setBackgroundResource(musicManage.isPlaying() ? R.drawable.icon_music_stop
						: R.drawable.icon_music_play);
		maxPos = playPosBar.getMax();

	}

	protected void setListeners() {
		musicPlay.setOnClickListener(this);
		musicUp.setOnClickListener(this);
		musicDown.setOnClickListener(this);

		musicPlay.setOnTouchListener(ViewUtils.instance().onTouchListener);
		musicUp.setOnTouchListener(ViewUtils.instance().onTouchListener);
		musicDown.setOnTouchListener(ViewUtils.instance().onTouchListener);

		playPosBar.setOnSeekBarChangeListener(this);

	}

	private View findViewById(int id) {
		return act.findViewById(id);
	}

	protected void initPlayPos() {
		musicPlay
				.setBackgroundResource(musicManage.isPlaying() ? R.drawable.icon_music_stop
						: R.drawable.icon_music_play);
		if ((musicManage.isPlaying() || musicManage.isPause()) && playPos != -1) {
			int position = musicManage.getCurrentPosition();
			int duration = musicManage.getDuration();
			playTime.setText(com.icloud.wrzjh.base.utils.TextUtils
					.secToHMS(position / 1000));
			sumTime.setText(com.icloud.wrzjh.base.utils.TextUtils
					.secToHMS(duration / 1000));
			if (duration > 0) {
				double pos = Math.floor(maxPos * (position * 1f) / duration);
				playPosBar.setProgress((int) pos);
			}
			playPosBar.setSecondaryProgress(loadPos);
		} else {
			playTime.setText(com.icloud.wrzjh.base.utils.TextUtils.secToHMS(0));
			sumTime.setText(com.icloud.wrzjh.base.utils.TextUtils.secToHMS(0));
			playPosBar.setProgress(0);
			playPosBar.setSecondaryProgress(0);
		}

	}

	protected void upPlayPos() {
		if ((musicManage.isPlaying() || musicManage.isPause()) && playPos != -1) {
			int position = musicManage.getCurrentPosition();
			int duration = musicManage.getDuration();
			playTime.setText(com.icloud.wrzjh.base.utils.TextUtils
					.secToHMS(position / 1000));
			if (duration > 0) {
				double pos = Math.floor(maxPos * position * 1f / duration);
				playPosBar.setProgress((int) pos);

			}
		}

	}

	protected void playEnd() {
		musicPlay.setBackgroundResource(R.drawable.icon_music_play);
		playPosBar.setProgress(playPosBar.getMax());
	}

	protected void seekTo(int pos) {
		float percent = (pos + 0f) / playPosBar.getMax();
		int duration = musicManage.getDuration();
		musicManage.seekTo((int) (duration * percent));

	}

	@Override
	public void play(int pos) {
		if (pos < 0 || pos >= act.chapterInfos.size())
			return;
		if (playPos != pos) {
			ArticleChapterItem item = act.chapterInfos.get(pos);
			/** 分数不够直接弹出 **/
			if (item.getMCurrency() > 0) {
				if (UserInfo.instance().isLogin()) {
					if (UserInfo.instance().getCurrency() < item.getMCurrency()) {
						AccUtils.showTip(act, item);
						return;
					} else
						AccUtils.deductCurrency(act, item.getAid(),
								item.getCpId());
				} else {
					LoadingTool.launchActivity(act, LoginAct.class);
					return;
				}
			}
			this.playPos = pos;
			musicListener.play(pos);
			String url = act.getDLUrl(item.did);
			if (TextUtils.isEmpty(url) || (!FileUtils.isExists(url))) {
				url = item.getCpUrl();
			}
			musicPlay.setBackgroundResource(R.drawable.icon_music_stop);
			musicManage.start(url, item);
			IoUtils.instance().saveReadingTrack(act.article.getAId(), pos);
		} else if (musicManage.isPlaying()) {
			pause();
		} else if (!musicManage.isPlaying()) {
			reStart();
		}

	}

	@Override
	public void stop() {

	}

	@Override
	public void pause() {
		musicPlay.setBackgroundResource(R.drawable.icon_music_play);
		musicManage.pause();

	}

	@Override
	public void reStart() {
		musicPlay.setBackgroundResource(R.drawable.icon_music_stop);
		musicManage.start();
	}

	public void palyTip(final int playPos) {
		if ((!SharedPreferenceUtil.isNoWifiLook())
				&& HttpUtils.isNetworkAvailable(act) && !HttpUtils.isWifi(act)) {
			DialogManage.showWarningDlg(act, R.string.warning,
					R.string.voices_no_wifi_warning, R.string.no_tip, R.string.cancel,
					R.string.continue_tip,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dialog instanceof WarningDlg
									&& ((WarningDlg) dialog).isCheck()) {
								SharedPreferenceUtil.saveNoWifiLook(true);
							}
							dialog.dismiss();
							play(playPos);

						}

					});

		} else
			play(playPos);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.music_play: {
			if (playPos != -1) {
				if (musicManage.isPlaying()) {
					pause();
				} else {
					palyTip(playPos);
				}
			}
			
			break;
		}
		case R.id.music_up: {
			if (playPos != -1)
				play(playPos - 1);
			break;
		}
		case R.id.music_down: {
			if (playPos != -1)
				play(playPos + 1);
			break;
		}
		}

	}

	/*** 加载播放下标 */
	protected void loadPlayIndex() {
		int postion = getPlayIndex();
		if (postion != -1) {
			playPos = postion;
			if (musicListener != null)
				musicListener.play(playPos);
			initPlayPos();
			halder.sendMessageDelayed(halder.obtainMessage(UP_TIME_POS),
					1 * 1000);
		} else if (musicManage.isPlaying()) {
			initPlayPos();
			halder.sendMessageDelayed(halder.obtainMessage(UP_TIME_POS),
					1 * 1000);
		} else if (musicManage.getState() != Configuration.STATE_NON) {
			initPlayPos();
		}
	}

	public int getPlayIndex() {
		int postion = -1;
		ArticleChapterInfo article = musicManage.getInfo();
		if (article != null && article.getAid() == act.article.getAId()) {
			for (int i = 0; i < act.chapterInfos.size(); i++) {
				if (act.chapterInfos.get(i).getCpId() == article.getCpId()) {
					postion = i;
					break;
				}
			}
		}
		playPos = postion;
		return postion;
	}

	public void load() {
		halder.obtainMessage(UP_INFO_ADD_END).sendToTarget();
	}

	Handler halder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (act.isFinishing())
				return;
			switch (msg.what) {
			case UP_INFO_ADD_END: {
				loadPlayIndex();
			}
				break;

			/** 绑定完成 **/
			case Configuration.STATE_MUSIC_BIND_END: {
				Intent intent = (Intent) msg.obj;
				loadPos = intent.getIntExtra("percent", 0);
				/** 更新播放列表 */
				loadPlayIndex();
				if (playPos == -1) {
					loadPos = 0;
				}
			}
				break;
			case Configuration.STATE_MUSIC_LOADING_POS: {
				Intent intent = (Intent) msg.obj;
				loadPos = intent.getIntExtra("percent", 0);
				if (playPos == -1) {
					loadPos = 0;
					playPosBar.setSecondaryProgress(0);
				} else
					playPosBar.setSecondaryProgress(loadPos);
			}
				break;
			case Configuration.STATE_MUSIC_PREPARED: {
				initPlayPos();
				halder.sendMessageDelayed(halder.obtainMessage(UP_TIME_POS),
						1 * 1000);
			}
				break;
			case Configuration.STATE_MUSIC_COMPLETION: {
				playEnd();
			}
				break;
			case Configuration.STATE_MUSIC_PLAY_POSTION: {
				upPlayPos();
			}
				break;
			case Configuration.STATE_NON:
			case UP_END_TIME: {
				initPlayPos();
			}
				break;
			case UP_TIME_POS: {
				upPlayPos();

			}
				break;
			}
		}
	};

	public int getPos() {
		return playPos;
	}

	public void onResume() {
		musicManage.addMusicList(this);
	}

	public void onPause() {
		musicManage.addMusicList(null);
	}

	public void onDestroy() {
		musicManage.unbind();
	}

	public void onStop() {
		musicManage.pause();

	}

	@Override
	public void upState(Intent intent) {
		int state = intent.getIntExtra("state", Integer.MAX_VALUE);
		halder.obtainMessage(state, intent).sendToTarget();

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		try {
			int progress = playPosBar.getProgress();
			if (progress > loadPos) {
				playPosBar.setProgress(loadPos);

			}
			seekTo(playPosBar.getProgress());
			halder.sendMessageDelayed(halder.obtainMessage(UP_TIME_POS),
					1 * 300);
		} catch (Exception e) {
		}
	}

	public interface MusicListener {
		public void play(int postion);
	}

	public void addMusicListener(MusicListener musicListener) {
		this.musicListener = musicListener;
	}

}
