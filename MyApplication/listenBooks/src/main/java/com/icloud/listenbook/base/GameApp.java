package com.icloud.listenbook.base;

import android.content.Intent;

import com.afollestad.appthemeengine.ATE;
import com.icloud.listenbook.R;
import com.icloud.listenbook.connector.ImageManage;
import com.icloud.listenbook.entity.ResultItemInfo;
import com.icloud.listenbook.http.VolleyUtils;
import com.icloud.listenbook.io.DBHelper;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.permissions.Nammu;
import com.icloud.listenbook.sdk.yunwa.LvieListenerImpl;
import com.icloud.listenbook.service.MediaPlayerService;
import com.icloud.listenbook.socket.pro.HallSocketManage;
import com.icloud.listenbook.unit.IflytekManage;
import com.icloud.listenbook.unit.PlayerUnit;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableChildren;
import com.yunva.live.sdk.YayaLiveApi;

import java.util.List;

public class GameApp extends BaseGameApp {
    public List<ResultItemInfo> ResultItemlist;
    public List<MeritTableAdult> MeritTableAdultlists;
    public List<MeritTableChildren> MeritTableChildrenlists;
	public static GameApp gameApp;
	public static GameApp getApplication(){
		return gameApp;
	}
	@Override
	public void onCreate() {
		IflytekManage.initIflyteK(this);
		super.onCreate();
		gameApp=this;
		instance = this;
		APP_NAME = getString(R.string.app_name);
		// 初始化
		VolleyUtils.instance().init(this);//创建一个newRequestQueue
		IoUtils.instance().init(this);//初始化数据库
		HandlerUtils.init();//
		ActivityUtils.init();
		ImageManage.instance().init(this).loadImage();
		DBHelper.getInstance(this);
		startMediaPlayer();
		PlayerUnit.instance().init(this);
		HallSocketManage.instance();
		/* 1000069* */
		YayaLiveApi.initApplicationOnCreate(this, LvieListenerImpl.APPID);

		Nammu.init(this);
		if (!ATE.config(this, "light_theme").isConfigured()) {
			ATE.config(this, "light_theme")
					.activityTheme(R.style.AppThemeLight)
					.primaryColorRes(R.color.colorPrimaryLightDefault)
					.accentColorRes(R.color.colorAccentLightDefault)
					.coloredNavigationBar(false)
					.usingMaterialDialogs(true)
					.commit();
		}
		if (!ATE.config(this, "dark_theme").isConfigured()) {
			ATE.config(this, "dark_theme")
					.activityTheme(R.style.AppThemeDark)
					.primaryColorRes(R.color.colorPrimaryDarkDefault)
					.accentColorRes(R.color.colorAccentDarkDefault)
					.coloredNavigationBar(false)
					.usingMaterialDialogs(true)
					.commit();
		}
		if (!ATE.config(this, "light_theme_notoolbar").isConfigured()) {
			ATE.config(this, "light_theme_notoolbar")
					.activityTheme(R.style.AppThemeLight)
					.coloredActionBar(false)
					.primaryColorRes(R.color.colorPrimaryLightDefault)
					.accentColorRes(R.color.colorAccentLightDefault)
					.coloredNavigationBar(false)
					.usingMaterialDialogs(true)
					.commit();
		}
		if (!ATE.config(this, "dark_theme_notoolbar").isConfigured()) {
			ATE.config(this, "dark_theme_notoolbar")
					.activityTheme(R.style.AppThemeDark)
					.coloredActionBar(false)
					.primaryColorRes(R.color.colorPrimaryDarkDefault)
					.accentColorRes(R.color.colorAccentDarkDefault)
					.coloredNavigationBar(true)
					.usingMaterialDialogs(true)
					.commit();
		}
	}

	private void startMediaPlayer() {
		Intent intent = new Intent();
		intent.setClass(this, MediaPlayerService.class);
		startService(intent);

	}

}
