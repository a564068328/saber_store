package com.icloud.listenbook.ui.chipAct;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class TtsSettingAct extends BaseActivity implements OnClickListener {
	View back;
	TextView tv_speed;
	TextView tv_volume;
	SeekBar sb_speed;
	SeekBar sb_volume;
	Spinner spinner;
	ToggleButton toggleBtn;
	View tv_exit;
	private int startSpeed;
	private int endSpeed;
	private int startVolume;
	private int endVolume;
	private int startVoice;
	private int endVoice;
	private boolean startBtnStatus;
	private boolean endBtnStatus;
	private boolean settingIsChange = false;

	@Override
	public void init() {
		startSpeed = Integer.valueOf(SharedPreferenceUtil.getTtsSpeed());
		startVolume = Integer.valueOf(SharedPreferenceUtil.getTtsVolumn());
		startVoice = SharedPreferenceUtil.getTtsVoice();
		startBtnStatus = SharedPreferenceUtil.getTtsSetting();
		endBtnStatus = startBtnStatus;
		endVoice = startVoice;
		endVolume = startVolume;
		endSpeed = startSpeed;
	}

	@Override
	public int getLayout() {
		return R.layout.act_tts_setting;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		tv_speed = (TextView) findViewById(R.id.tv_speed);
		tv_volume = (TextView) findViewById(R.id.tv_volume);
		sb_speed = (SeekBar) findViewById(R.id.sb_speed);
		sb_volume = (SeekBar) findViewById(R.id.sb_volume);
		spinner = (Spinner) findViewById(R.id.spinner);
		toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
		tv_exit = findViewById(R.id.tv_exit);
		tv_speed.setText(String
				.format(getString(R.string.tv_speed), startSpeed));
		tv_volume.setText(String.format(getString(R.string.tv_volume),
				startVolume));
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		sb_speed.setMax(100);
		sb_volume.setMax(100);
		sb_speed.setProgress(startSpeed);
		sb_volume.setProgress(startVolume);
		sb_speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				endSpeed = progress;
				tv_speed.setText(String.format(getString(R.string.tv_speed),
						progress));
			}
		});
		sb_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				endVolume = progress;
				tv_volume.setText(String.format(getString(R.string.tv_volume),
						progress));
			}
		});

		spinner.setSelection(startVoice, true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				endVoice = position;
				// getResources()
				// .getStringArray(R.array.voicer_cloud_values)[position]
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		if (startBtnStatus) {
			toggleBtn.setToggleOn();
		} else
			toggleBtn.setToggleOff();
		toggleBtn.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				if (on) {
					endBtnStatus = true;
				} else {
					endBtnStatus = false;
				}
			}
		});
		tv_exit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.tv_exit:
			saveSetting();
			finish();
			break;
		default:
			break;
		}
	}

	private void saveSetting() {
		SharedPreferenceUtil.saveTtsSetting(endBtnStatus);
		SharedPreferenceUtil.saveTtsSpeed(String.valueOf(endSpeed));
		SharedPreferenceUtil.saveTtsVolumn(String.valueOf(endVolume));
		SharedPreferenceUtil.saveTtsVoice(endVoice);
		// LogUtil.e(Tag, "endBtnStatus" + endBtnStatus + ",endSpeed" + endSpeed
		// + ",endVolume" + endVolume + ",endVoice" + endVoice);
		if (endSpeed != startSpeed || endVolume != startVolume
				|| endVoice != startVoice)
			settingIsChange = true;
		if (endBtnStatus && settingIsChange) {
			Intent intent = new Intent();
			intent.setAction(TtsAct.TTS_SET_CHANGE);
			sendBroadcast(intent);
		}
	}

}
