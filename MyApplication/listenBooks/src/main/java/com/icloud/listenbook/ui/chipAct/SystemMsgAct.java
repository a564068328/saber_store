package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.SystemMsgAdapter;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.OtherUser;

public class SystemMsgAct extends BaseActivity implements OnClickListener,
		Response.Listener<JSONObject> {
	TextView name;
	View back;
	ListView list;
	SystemMsgAdapter systemMsgAdapter;
	ArrayList<ChatMsg> data;

	@Override
	public void init() {
		ChatMsgManage.instance().clearSystemMsgTip();
		data = new ArrayList<ChatMsg>();
		systemMsgAdapter = new SystemMsgAdapter(this, data);
	}

	@Override
	public int getLayout() {
		return R.layout.act_system_msg;
	}

	public void onResume() {
		super.onResume();
		data.clear();
		data.addAll(IoUtils.instance().getChatMsg(UserInfo.instance().getUid(),
				ChatMsgManage.SYS_CHAR_TAG));
		systemMsgAdapter.upData(data);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		name.setText(R.string.system_info);
		data.clear();
		data.addAll(IoUtils.instance().getChatMsg(UserInfo.instance().getUid(),
				ChatMsgManage.SYS_CHAR_TAG));
		systemMsgAdapter.upData(data);
		HttpUtils.getUserInfo(UserInfo.instance().getUid(), this, null);
	}

	@Override
	public void findViews() {
		name = (TextView) findViewById(R.id.name);
		back = findViewById(R.id.back);
		list = (ListView) findViewById(R.id.list);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {
				if (data != null && postion < data.size()) {
					ChatMsg chatMsg = data.get(postion);
					showSystemMsgDig(SystemMsgAct.this, chatMsg);
				}
			}
		});
		list.setAdapter(systemMsgAdapter);
	}

	public void showSystemMsgDig(final Activity act, final ChatMsg chatMsg) {
		DialogManage.showAlertDialog(act, chatMsg.getMsg(), R.string.del,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						IoUtils.instance().removeCharMsg(chatMsg);
						data.remove(chatMsg);
						systemMsgAdapter.upData(data);
					}
				}, R.string.close,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
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
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			LogUtil.e(Tag, "getUserInfo:" + response.toString());
			if (res == 0) {
				UserInfo.instance().setCurrency(response.optDouble("currency"));

			}
		} catch (Exception e) {
		}
	}
}
