package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONObject;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.unit.DataUpManage;
//vedioListAct和voiceListAct的基类
public abstract class BaseListAct extends BaseActivity implements
		Listener<JSONObject>, ErrorListener, OnClickListener {
	protected RecyclerView list;
	protected ArrayList<ArticleItem> datas;
	protected int page = 0;
	protected GridLayoutManager gridLayoutManager;
	protected ImageView back;
	protected int lastVisibleItem;
	protected long cId;
	protected TextView title;
	protected String titleName;

	@Override
	public void init() {
		cId = this.getIntent().getLongExtra("cId", 0);
		titleName = this.getIntent().getStringExtra("name");
		datas = new ArrayList<ArticleItem>();
	}

	@Override
	public void findViews() {
		list = (RecyclerView) findViewById(R.id.list);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
	}

	@Override
	public void setListeners() {
		if (!TextUtils.isEmpty(titleName))
			title.setText(titleName);
	}

	@Override
	public int getLayout() {
		return R.layout.act_vedio_list;
	}

	/** 保存缓存标示 */
	protected void saveVerCode(long cId, int page, String verCode) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("cId", cId);
			jb.put("page", page);
			DataUpManage.saveVerCode("readArticle" + jb.toString(), verCode);
		} catch (Exception e) {

		}
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

	abstract public void nextPage(int page);

	@Override
	public void getDatas() {
		/* 开启线程获取数据 */
		nextPage(page);

	}
}
