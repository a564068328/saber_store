package com.icloud.listenbook.ui.chipAct;


import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.base.view.TxTView;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.unit.DataUpManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.icloud.wrzjh.base.utils.io.FileUtils;

public class BookAct extends BaseActivity implements OnClickListener,
		Listener<String>, ErrorListener {
	ImageView back;
	TextView titleTxt;
	TxTView dataTxt;
	ArticleChapterItem articleChapterItem;
	String url;
	ProgressBar progress;
	View addSize;
	View reduceSize;
	View refresh;

	@Override
	public void init() {
		String value = this.getIntent().getStringExtra("data");
		articleChapterItem = JsonUtils
				.fromJson(value, ArticleChapterItem.class);
		url = articleChapterItem.getCpUrl();

	}

	@Override
	public int getLayout() {
		return R.layout.act_book;
	}

	@Override
	public void findViews() {
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		dataTxt = (TxTView) findViewById(R.id.dataTxt);
		back = (ImageView) findViewById(R.id.back);
		progress = (ProgressBar) findViewById(R.id.progress);
		addSize = findViewById(R.id.add_size);
		reduceSize = findViewById(R.id.reduce_size);
		refresh = findViewById(R.id.refresh);
		dataTxt.setTextIsSelectable(true); 
	}

	@Override
	public void getDatas() {
		super.getDatas();
		getTxtValue();
	}

	protected void getTxtValue() {
		if (!TextUtils.isEmpty(url)) {
			// if (DataUpManage.isUp(this, url))
			// get();
			// else
			{
				getTxtValueCache(true);
			}
		}
	}

	protected void getTxtValueCache(boolean isUp) {
		String value = FileUtils.readPrivateContent(saveFileName());
		if (!TextUtils.isEmpty(value)) {
			progress.setVisibility(View.GONE);
			dataTxt.setText(value, BufferType.SPANNABLE);
		} else if (isUp) {
			get();
		}
	}

	@Override
	public void setDatas() {
		super.setDatas();
		titleTxt.setText(articleChapterItem.getCpName());

	}

	public void setListeners() {
		// dataTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
		back.setOnClickListener(this);
		addSize.setOnClickListener(this);
		reduceSize.setOnClickListener(this);
		refresh.setOnClickListener(this);
		refresh.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.refresh: {
			get();
		}
			break;
		case R.id.back: {
			this.finish();
		}
			break;
		case R.id.add_size:
			dataTxt.addTextSize();
			break;
		case R.id.reduce_size:
			dataTxt.reduceTextSize();
			break;
		}
	}

	protected String saveFileName() {
		return "ID_" + articleChapterItem.getCpId() + "_N_"
				+ articleChapterItem.getCpName();
	}

	protected void get() {
		refresh.setVisibility(View.GONE);
		HttpUtils.get(url, this, this);
	}

	@Override
	public void onResponse(String response) {
		progress.setVisibility(View.GONE);
		refresh.setVisibility(View.GONE);
		try {
			if (!TextUtils.isEmpty(response)) {
				dataTxt.setText(response, BufferType.SPANNABLE);
				if (FileUtils.writePrivateContent(saveFileName(), response)) {
					DataUpManage.save(BookAct.this, url);
				}
			}
		} catch (Exception e) {
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		progress.setVisibility(View.GONE);
		refresh.setVisibility(View.VISIBLE);
		dataTxt.setText("");
		getTxtValueCache(false);
	}

}
