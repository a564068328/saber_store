package com.icloud.listenbook.ui;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.OnThreadTask;
import com.icloud.listenbook.base.ThreadTask;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.sdk.yunwa.yunwaSDKInit;
import com.icloud.listenbook.ui.chipAct.LoadingAdAct;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.down.DownloadMp3;
import com.listenBook.greendao.ChatMsg;

public class LoadingActivity extends BaseActivity {

	@Override
	public int getLayout() {
		return R.layout.act_loading;
	}

	@Override
	public void findViews() {

	}

	@Override
	public void setListeners() {

	}

	@Override
	public void init() {
		/** 清空wifi下记录 */
		SharedPreferenceUtil.saveNoWifiLook(false);
		//查询当前应用的Heap Size阈值
//		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		int memory = manager.getMemoryClass();
//		LogUtil.e("TAG", "memory:"+memory+"MB");
		/* 预加载数据* */
		ThreadTask.start(this, null, false, new OnThreadTask() {
			@Override
			public String onThreadRun() {
				
				/** 登陆 */
				HttpUtils.login();
				/** 获取分类 **/
				HttpUtils.getMedia();
				/** 获取推荐页 */
				HttpUtils.readRecommend();
				/** 获取广告链接 */
				HttpUtils.getHomeAds();
				/* 获取新增内容 */
//				HttpUtils.getNewContent();
				
//				IoUtils.instance().clearChatMsg();
//				for(int i=1;i<210;i++){
//					
//					ChatMsg msg = new ChatMsg((long)i,1,(long)ChatMsgManage.CHAR_TAG
//							,null,"sdk","haha"+i,System.currentTimeMillis());
//					IoUtils.instance().saveChatMsg(msg);
//				}
				try {
					Thread.sleep(800);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			@Override
			public void onAfterUIRun(String result) {
				// 跳转到主页
				LoadingTool.launchActivity(LoadingActivity.this,
						TablesActivity.class, LoadingActivity.this.getIntent());
//				LoadingTool.launchActivity(LoadingActivity.this,
//						LoadingAdAct.class, LoadingActivity.this.getIntent());
//				LoadingActivity.this.startActivity(new Intent(LoadingActivity.this,LoadingAdAct.class));
				LoadingActivity.this.finish();
			}
		});
	}

    @Override
    public void setDatas() {
    	super.setDatas();

    }
	protected void onDestroy() {
		super.onDestroy();

	}

}
