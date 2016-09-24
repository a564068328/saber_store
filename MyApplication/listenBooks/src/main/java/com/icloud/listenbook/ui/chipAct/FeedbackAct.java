package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.FeedbackAdapter;
import com.icloud.listenbook.ui.adapter.entity.FeedbackItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ChipFeedback;
import com.listenBook.greendao.Feedback;

public class FeedbackAct extends BaseActivity implements OnClickListener {
	View sendbtn;
	EditText edit;
	FeedbackAdapter adapter;
	ArrayList<FeedbackItem> data;
	LinearLayoutManager lineLM;
	RecyclerView list;
	View back;

	@Override
	public void init() {
		lineLM = new LinearLayoutManager(this);
		lineLM.setOrientation(LinearLayoutManager.VERTICAL);
		data = new ArrayList<FeedbackItem>();
		adapter = new FeedbackAdapter(this, data);
	}

	@Override
	public int getLayout() {
		return R.layout.act_feedback;
	}

	@Override
	public void findViews() {
		sendbtn = findViewById(R.id.send);
		edit = (EditText) findViewById(R.id.edit);
		list = (RecyclerView) findViewById(R.id.list);
		back = findViewById(R.id.back);
	}

	@Override
	public void setListeners() {
		sendbtn.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		back.setOnClickListener(this);
		sendbtn.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setAdapter(adapter);
		list.setLayoutManager(lineLM);
	}

	@Override
	public void getDatas() {
		super.getDatas();
		getFeedback();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.send: {
			String msg = edit.getText().toString().trim();
			if (!TextUtils.isEmpty(msg) && msg.length() > 2) {
				edit.setText("");
				sendFeedback(0, msg);
			} else {
				ToastUtil.showMessage("反馈长度要大于2");
			}
		}
			break;
		case R.id.back:
			this.finish();
			break;
		}

	}

	public void getFeedback() {
		HttpUtils.appFeedbackList(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LogUtil.e("appFeedbackList", response.toString());
					int result = response.optInt("result", -1);
					if (result == 0) {

						ArrayList<Feedback> downFeedback = new ArrayList<Feedback>();
						ArrayList<ChipFeedback> downChipFeedback = new ArrayList<ChipFeedback>();
						JSONArray array = response.optJSONArray("list");
						JSONObject jsonObj;
						JSONArray content;
						FeedbackItem item;
						Feedback feedback;
						ChipFeedback chipFeedback;
						data.clear();
						for (int i = 0; i < array.length(); i++) {
							jsonObj = array.optJSONObject(i);
							feedback = JsonUtils.toFeedback(jsonObj);
							item = new FeedbackItem(feedback);
							item.viewType = Type.TABLE_TITLE;
							content = jsonObj.optJSONArray("content");
							data.add(item);
							downFeedback.add(feedback);
							for (int j = 0; j < content.length(); j++) {
								jsonObj = content.optJSONObject(i);
								chipFeedback = JsonUtils.toChipFeedback(
										item.getFid(), jsonObj);
								item = new FeedbackItem(chipFeedback);
								item.viewType = Type.TABLE_ITEM;
								data.add(item);
								downChipFeedback.add(chipFeedback);
							}
						}
						adapter.upData(data);
						IoUtils.instance().saveFeedback(downFeedback);
						IoUtils.instance().saveChipFeedback(downChipFeedback);
					}
				} catch (Exception e) {
				}
			}
		}, null);
	}

	public void sendFeedback(long fid, String msg) {

		HttpUtils.addAppFeedback(fid, msg, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.e("sendFeedback", response.toString());
				getFeedback();
				int result = response.optInt("result", -1);
				String msg = response.optString("msg");
				if (!TextUtils.isEmpty(msg)) {
					ToastUtil.showMessage(msg);
				}
				if (result == 0) {
					FeedbackAct.this.finish();
					/* 设置评论次数 再也不用评论 */
					SharedPreferenceUtil.saveFeedbackTipConut(
							String.valueOf(UserInfo.instance().getUid()),
							Integer.MAX_VALUE);
				}

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtil.showMessage("网络错误,请重新发送");
			}
		});

	}
}
