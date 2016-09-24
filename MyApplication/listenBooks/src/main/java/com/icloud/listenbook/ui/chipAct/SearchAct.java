package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.SearchAdapter;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

public class SearchAct extends BaseActivity implements OnClickListener,
		Listener<JSONObject>, ErrorListener, TextWatcher,OnItemClickListener,
		RadioGroup.OnCheckedChangeListener {
	Button search;
	View clear;
	EditText edit;
	SearchAdapter adapter;
	LinearLayoutManager lineLM;
	RecyclerView list;
	ArrayList<Article> vedioItems;
	ArrayList<Article> voiceItems;
	ArrayList<Article> bookItems;
	RadioGroup tabs;
	int page;
	View hint;

	@Override
	public void init() {
		lineLM = new LinearLayoutManager(this);
		lineLM.setOrientation(LinearLayoutManager.VERTICAL);
		vedioItems = new ArrayList<Article>();
		voiceItems = new ArrayList<Article>();
		bookItems = new ArrayList<Article>();
		adapter = new SearchAdapter(this);

	}

	@Override
	public int getLayout() {
		return R.layout.act_search;
	}

	@Override
	public void findViews() {
		search = (Button) findViewById(R.id.search);
		clear = findViewById(R.id.clear);
		edit = (EditText) findViewById(R.id.edit);
		list = (RecyclerView) findViewById(R.id.list);
		tabs = (RadioGroup) findViewById(R.id.tabs);
		hint = findViewById(R.id.hint);
	}

	@Override
	public void setListeners() {
		search.setOnClickListener(this);
		search.setOnTouchListener(ViewUtils.instance().onTouchListener);
		clear.setOnClickListener(this);
		clear.setOnTouchListener(ViewUtils.instance().onTouchListener);
		edit.addTextChangedListener(this);
		tabs.setOnCheckedChangeListener(this);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		clear.setVisibility(View.GONE);
		tabs.setVisibility(View.GONE);
		list.setAdapter(adapter);
		list.setLayoutManager(lineLM);
        
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.clear:
			edit.setText("");
			break;
		case R.id.search: {
			if (ToolUtils.isNetworkAvailableTip(this)) {
				String input = edit.getText().toString();
				if (TextUtils.isEmpty(input)) {
					this.finish();
				} else {
					search(input);
				}
			}
		}
			break;
		}

	}

	public void search(String data) {
		HttpUtils.search(data, this, this);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (edit.getText().toString().length() > 0) {
			clear.setVisibility(View.VISIBLE);
			search.setText(R.string.search);
		} else {
			search.setText(R.string.cancel);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		clear.setVisibility(View.GONE);

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Log.e(Tag, error.toString());
	}

	public void setData(ArrayList<Article> items) {
		vedioItems.clear();
		voiceItems.clear();
		bookItems.clear();
		if (items.size() > 0)
			tabs.setVisibility(View.VISIBLE);
		for (Article item : items) {
			switch (item.getMedia()) {
			case Configuration.TYPE_VEDIO:
				vedioItems.add(item);
				break;
			case Configuration.TYPE_VOICE:
				voiceItems.add(item);
				break;
			case Configuration.TYPE_BOOK:
				bookItems.add(item);
				break;
			}
		}
		if (vedioItems.size() != 0) {
			page = Configuration.TYPE_VEDIO;
		} else if (voiceItems.size() != 0) {
			page = Configuration.TYPE_VOICE;
		} else if (bookItems.size() != 0) {
			page = Configuration.TYPE_BOOK;
		}
		upSeleclt();
	}

	public void upSeleclt() {
		ArrayList<Article> items;
		switch (page) {
		default:
		case Configuration.TYPE_VEDIO:
			items = vedioItems;
			tabs.check(R.id.vedio_r);
			break;
		case Configuration.TYPE_VOICE:
			items = voiceItems;
			tabs.check(R.id.voice_r);
			break;
		case Configuration.TYPE_BOOK:
			items = bookItems;
			tabs.check(R.id.book_r);
			break;
		}
		adapter.upDatas(items);
	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "onResponse:" + response.toString());
			if (res == 0) {

				JSONArray jsonArray = response.optJSONArray("list");
				if (jsonArray.length() == 0) {
					hint.setVisibility(View.VISIBLE);
				}else{
					hint.setVisibility(View.GONE);
				}
				Article item;
				ArrayList<Article> items = new ArrayList<Article>();
				JSONObject json;
				for (int i = 0; i < jsonArray.length(); i++) {
					json = jsonArray.optJSONObject(i);
					item = new Article();
					JsonUtils.toArticle(item, json);
					items.add(item);
				}
				setData(items);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.vedio_r:
			page = Configuration.TYPE_VEDIO;
			break;
		case R.id.voice_r:
			page = Configuration.TYPE_VOICE;
			break;
		case R.id.book_r:
			page = Configuration.TYPE_BOOK;
			break;
		}
		upSeleclt();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	
}
