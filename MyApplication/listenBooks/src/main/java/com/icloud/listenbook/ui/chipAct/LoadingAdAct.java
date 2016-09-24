package com.icloud.listenbook.ui.chipAct;

import org.json.JSONObject;

import android.R.integer;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.unit.BitmapUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class LoadingAdAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	public  final String TAG=getClass().getName();
	TextView tv_ad;
	ImageView iv_ad;
	private Bitmap bitmap;
	private MyCountDownTimer mc;
	@Override
	public void init() {
		// String imageName=getIntent().getStringExtra("imageName");
		// String pathName=Environment.getExternalStorageDirectory()
		// + "/com.icloud.listenbook/"+imageName;
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(),
				R.drawable.icon_loading_ad, width, height);
		mc=new MyCountDownTimer(2000, 1000);
		mc.start();
	}

	@Override
	public int getLayout() {
		return R.layout.act_loading_ad;
	}

	@Override
	public void findViews() {
         tv_ad=(TextView) findViewById(R.id.tv_ad);
         iv_ad=(ImageView) findViewById(R.id.iv_ad);
         tv_ad.setText( "2秒跳过");
	}

	@Override
	public void setListeners() {
		tv_ad.setOnClickListener(this);
		tv_ad.setOnTouchListener(ViewUtils.instance().onTouchListener);
		iv_ad.setOnClickListener(this);
		iv_ad.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}
    @Override
    protected void onStart() {
    	super.onStart();
    	iv_ad.setImageBitmap(bitmap);
    }
	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {

	}

	@Override
	public void onClick(View v) {
       int i=v.getId();
       switch (i) {
	case R.id.tv_ad:
		mc.cancel();
		LoadingTool.launchActivity(LoadingAdAct.this,
				TablesActivity.class, LoadingAdAct.this.getIntent());
		LoadingAdAct.this.finish();
		break;
	case R.id.iv_ad:
		
		break;
	default:
		break;
	}
	}

	class MyCountDownTimer extends CountDownTimer { 
		/** 
		* 
		* @param millisInFuture 
		* 表示以毫秒为单位 倒计时的总数 
		* 
		* 例如 millisInFuture=1000 表示1秒 
		* 
		* @param countDownInterval 
		* 表示 间隔 多少微秒 调用一次 onTick 方法 
		* 
		* 例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick() 
		* 
		*/
		public MyCountDownTimer(long millisInFuture, long countDownInterval) { 
			super(millisInFuture, countDownInterval); 
		} 
		public void onFinish() { 
			tv_ad.setText("1秒后跳过"); 
			LoadingTool.launchActivity(LoadingAdAct.this,
					TablesActivity.class, LoadingAdAct.this.getIntent());
			LoadingAdAct.this.finish();
		} 
		public void onTick(long millisUntilFinished) { 
			
			tv_ad.setText( (millisUntilFinished / 1000 +1) + "秒后跳过"); 
		} 
		}
}
