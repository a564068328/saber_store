package com.icloud.listenbook.ui.chipAct;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.PPTAdapter;
import com.icloud.listenbook.ui.adapter.entity.PPtItem;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.down.DownloadMp3;

public class PPTAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener {
	View back, progressBar;
	ListView list;
	PPTAdapter adapter;
	ArrayList<PPtItem> PPtItemArray;
	int id;
	
	@Override
	public void init() {
		PPtItemArray = new ArrayList<PPtItem>();
		adapter = new PPTAdapter(this);
	}

	@Override
	public void getDatas() {
		super.getDatas();
		id = this.getIntent().getIntExtra("id", 0);
		HttpUtils.getPPT(this, this, id);

	}

	@Override
	public int getLayout() {
		return R.layout.act_ppt;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		list = (ListView) findViewById(R.id.list);
		progressBar = findViewById(R.id.progressBar);

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if(what==0){
				progressBar.setVisibility(View.INVISIBLE);
				ToastUtil.showMessage("您的存储空间可能遇到问题了");
				return;
			}
			File file = new File((String) msg.obj);
			Uri uri = Uri.fromFile(file);
			Intent intent=null;
			switch (what) {
			case 1:
				progressBar.setVisibility(View.INVISIBLE);
				intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);				
				intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
				startActivity(intent);
				break;
			case 2:
				progressBar.setVisibility(View.INVISIBLE);
				intent = new Intent("android.intent.action.VIEW"); 
				intent.addCategory("android.intent.category.DEFAULT"); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				intent.setDataAndType(uri, "application/msword");
				startActivity(intent);
				break;
			case 3:
				progressBar.setVisibility(View.INVISIBLE);
				intent = new Intent("android.intent.action.VIEW"); 
				intent.addCategory("android.intent.category.DEFAULT"); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				intent.setDataAndType(uri, "application/vnd.ms-excel");
				startActivity(intent);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position >= 0 && position < PPtItemArray.size()) {
					PPtItem _PPtItem = PPtItemArray.get(position);
					String url = _PPtItem.url;
					if (TextUtils.isEmpty(url))
						return;
					if (url.startsWith("http://") || url.startsWith("www.")
							|| url.startsWith("https://")) {
						String name = url.substring(url.lastIndexOf(".") + 1)
								.toString().toLowerCase();
						progressBar.setVisibility(View.INVISIBLE);
						if (name.equals("ppt")) {
							progressBar.setVisibility(View.VISIBLE);
							new DownloadMp3(PPTAct.this).downPPT(url, handler,
									0);
						} else if (name.equals("doc")) {
							progressBar.setVisibility(View.VISIBLE);
							new DownloadMp3(PPTAct.this).downPPT(url, handler,
									1);
						} else if (name.equals("xls")) {
							progressBar.setVisibility(View.VISIBLE);
							new DownloadMp3(PPTAct.this).downPPT(url, handler,
									2);
						} else {
							Intent intent = new Intent();
							intent.putExtra("url", url);
							LoadingTool.launchActivity(PPTAct.this,
									WebAct.class, intent);
						}
					} else {
						return;
					}
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
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onResponse(JSONObject response) {
		progressBar.setVisibility(View.GONE);
		int res = response.optInt("result", -1);
		if (res == 0) {
			PPtItemArray = JsonUtils.toPPtItems(response.optJSONArray("list"));
			adapter.upData(PPtItemArray);
		}
	}

}
