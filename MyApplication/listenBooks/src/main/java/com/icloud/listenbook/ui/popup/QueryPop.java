package com.icloud.listenbook.ui.popup;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.icloud.listenbook.R;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class QueryPop implements OnClickListener, TextWatcher {
	int top;
	Button search;
	EditText edit;
	View clear;
	QueryListener list;
	View view;

	public QueryPop(View view) {
		this.view = view;
		findViews();
		setListeners();
	}

	protected void findViews() {
		search = (Button) findViewById(R.id.search);
		clear = findViewById(R.id.clear);
		edit = (EditText) findViewById(R.id.edit);

	}

	private View findViewById(int id) {
		return view != null ? view.findViewById(id) : null;
	}

	protected void setListeners() {
		search.setOnClickListener(this);
		search.setOnTouchListener(ViewUtils.instance().onTouchListener);
		clear.setOnClickListener(this);
		clear.setOnTouchListener(ViewUtils.instance().onTouchListener);
		edit.addTextChangedListener(this);
		clear.setVisibility(View.GONE);
	}

	public void show() {
		if (view.getVisibility() != View.VISIBLE)
			view.setVisibility(View.VISIBLE);
	}

	private boolean isShowing() {
		return view.getVisibility() == View.VISIBLE;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.clear:
			edit.setText("");
			clear.setVisibility(View.GONE);
			break;
		case R.id.search:
			String input = edit.getText().toString();
			if (TextUtils.isEmpty(input)) {
				this.dismiss();
			} else {
				search(input);
			}
			break;
		}

	}

	private void dismiss() {
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			if (list != null)
				list.End();
		}
	}

	protected void search(String search) {
		if (list != null)
			list.Query(search);
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

	public void addQueryListener(QueryListener list) {
		this.list = list;
	}

	public interface QueryListener {
		public void Query(String query);

		public void End();
	}
}
