package com.icloud.listenbook.ui.chipAct;

import android.R.integer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class ChatNotifySetAct extends BaseActivity implements OnClickListener {

	View back;
	ToggleButton toggleBtn1;
	ToggleButton toggleBtn2;
	ToggleButton toggleBtn3;
	ToggleButton toggleBtn4;
	RelativeLayout rl_1;
	RelativeLayout rl_2;
	RelativeLayout rl_3;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.act_chat_notify_set;
	}

	@Override
	public void findViews() {
		// TODO Auto-generated method stub
		back = findViewById(R.id.back);
		toggleBtn1 = (ToggleButton) findViewById(R.id.toggleBtn1);
		toggleBtn2 = (ToggleButton) findViewById(R.id.toggleBtn2);
		toggleBtn3 = (ToggleButton) findViewById(R.id.toggleBtn3);
		toggleBtn4 = (ToggleButton) findViewById(R.id.toggleBtn4);
		rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
		rl_2 = (RelativeLayout) findViewById(R.id.rl_2);
		rl_3 = (RelativeLayout) findViewById(R.id.rl_3);
		if(SharedPreferenceUtil.getChatmsgNotifySwith()){
			toggleBtn1.setToggleOn();
			if(SharedPreferenceUtil.getChatmsgNotifyDetial()){
				toggleBtn2.setToggleOn();
			}else{
				toggleBtn2.setToggleOff();
			}
			if(SharedPreferenceUtil.getChatmsgNotifyVoice()){
				toggleBtn3.setToggleOn();
			}else{
				toggleBtn3.setToggleOff();
			}
			if(SharedPreferenceUtil.getChatmsgNotifyShark()){
				toggleBtn4.setToggleOn();
			}else{
				toggleBtn4.setToggleOff();
			}
		}else{
			toggleBtn2.setToggleOff();
			rl_1.setVisibility(View.INVISIBLE);
			rl_2.setVisibility(View.INVISIBLE);
			rl_3.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		toggleBtn1.setOnToggleChanged(new OnToggleChanged() {
			public void onToggle(boolean on) {
				if (on) {
					SharedPreferenceUtil.saveChatmsgNotifySwith(true);
					toggleBtn1.setToggleOn();
					if(rl_1.getVisibility()==View.INVISIBLE){
						rl_1.setVisibility(View.VISIBLE);
						rl_2.setVisibility(View.VISIBLE);
						rl_3.setVisibility(View.VISIBLE);
					}
					if(SharedPreferenceUtil.getChatmsgNotifyDetial()){
						toggleBtn2.setToggleOn();
					}else{
						toggleBtn2.setToggleOff();
					}
					if(SharedPreferenceUtil.getChatmsgNotifyVoice()){
						toggleBtn3.setToggleOn();
					}else{
						toggleBtn3.setToggleOff();
					}
					if(SharedPreferenceUtil.getChatmsgNotifyShark()){
						toggleBtn4.setToggleOn();
					}else{
						toggleBtn4.setToggleOff();
					}
				} else {
					SharedPreferenceUtil.saveChatmsgNotifySwith(false);
					toggleBtn1.setToggleOff();
					if(rl_1.getVisibility()==View.VISIBLE){
						rl_1.setVisibility(View.INVISIBLE);
						rl_2.setVisibility(View.INVISIBLE);
						rl_3.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		toggleBtn2.setOnToggleChanged(new OnToggleChanged() {
			public void onToggle(boolean on) {
				if (on) {
					SharedPreferenceUtil.saveChatmsgNotifyDetial(true);
					toggleBtn2.setToggleOn();
				} else {
					SharedPreferenceUtil.saveChatmsgNotifyDetial(false);
					toggleBtn2.setToggleOff();
				}
			}
		});
		toggleBtn3.setOnToggleChanged(new OnToggleChanged() {
			public void onToggle(boolean on) {
				if (on) {
					SharedPreferenceUtil.saveChatmsgNotifyVoice(true);
					toggleBtn3.setToggleOn();
				} else {
					SharedPreferenceUtil.saveChatmsgNotifyVoice(false);
					toggleBtn3.setToggleOff();
				}
			}
		});
		toggleBtn4.setOnToggleChanged(new OnToggleChanged() {
			public void onToggle(boolean on) {
				if (on) {
					SharedPreferenceUtil.saveChatmsgNotifyShark(true);
					toggleBtn4.setToggleOn();
				} else {
					SharedPreferenceUtil.saveChatmsgNotifyShark(false);
					toggleBtn4.setToggleOff();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		switch (i) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
