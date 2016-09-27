package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.socket.pro.HallSocketManage;
import com.icloud.listenbook.ui.adapter.FriendMsgAdapter;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.listenbook.unit.ChatMsgManage.FriendMessageListener;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.OtherUser;

public class FriendMsgAct extends BaseActivity implements OnClickListener,
		FriendMessageListener {
	public static final long TIME_INTERVAL = 1 * 1000;
	TextView name;
	View back;
	ListView list;
	TextView send;
	EditText edit;
	OtherUser otherUser;
	FriendMsgAdapter adapter;
	private long lastSendLaba;
	protected Handler UIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (FriendMsgAct.this.isFinishing())
				return;
			ChatMsg chatMsg = (ChatMsg) msg.obj;
			if (chatMsg.getSuid() == otherUser.getUid()) {
				chatMsg.setIcon(otherUser.getIcon());
			}
			adapter.addMsg(chatMsg);
			adapter.notifyDataSetChanged();

		};
	};

	protected void onResume() {
		super.onResume();
		getMsgData();
	}

	protected void getMsgData() {
		ArrayList<ChatMsg> data = new ArrayList<ChatMsg>(IoUtils.instance()
				.getChatMsg(otherUser.getUid(), UserInfo.instance().getUid()));
		ChatMsg chatMsg;
		for (int i = 0; i < data.size(); i++) {
			chatMsg = data.get(i);
			if (chatMsg.getSuid() == otherUser.getUid()) {
				chatMsg.setIcon(otherUser.getIcon());
			} else if (chatMsg.getSuid() == UserInfo.instance().getUid()) {
				chatMsg.setIcon(UserInfo.instance().getIcon());
			}
		}
		if (adapter != null) {
			adapter.upData(data);
			list.setSelection(ListView.FOCUS_DOWN);
		}
	}

	@Override
	public void init() {
		String value = this.getIntent().getStringExtra("data");
		otherUser = JsonUtils.fromJson(value, OtherUser.class);
		adapter = new FriendMsgAdapter(this);
		ChatMsgManage.instance().addFriendMessageListener(this);
		ChatMsgManage.instance().clearFriendMsgTip(otherUser.getUid());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ChatMsgManage.instance().addFriendMessageListener(null);
	}

	@Override
	public void setDatas() {
		super.setDatas();
		if (!TextUtils.isEmpty(otherUser.getNick()))
			name.setText(otherUser.getNick());
	}

	@Override
	public int getLayout() {
		return R.layout.act_friend_msg;
	}

	@Override
	public void findViews() {
		name = (TextView) findViewById(R.id.name);
		back = findViewById(R.id.back);
		list = (ListView) findViewById(R.id.list);
		edit = (EditText) findViewById(R.id.edit);
		send = (TextView) findViewById(R.id.send);
	}

	TextWatcher tw = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			send.setEnabled(edit.getText().length() >= 1 ? true : false);

		}
	};

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);
		send.setOnClickListener(this);
		send.setOnTouchListener(ViewUtils.instance().onTouchListener);
		list.setAdapter(adapter);
		edit.addTextChangedListener(tw);
	}

	protected void send() {
		String msg = edit.getText().toString();
		if (TextUtils.isEmpty(msg)) {
			ToastUtil.showMessage("发送消息不能为空");
			return;
		}
		long delta = System.currentTimeMillis() - lastSendLaba;
		if (delta >= TIME_INTERVAL) {
			if (HallSocketManage.instance().sendMsgToFriend(otherUser.getUid(),
					msg)) {
				lastSendLaba = System.currentTimeMillis();
				edit.setText("");
				adapter.notifyDataSetChanged();
			} else {
				ToastUtil.showMessage("未连接服务器发送失败");
			}
		} else {
			ToastUtil.showMessage("话说的太快,歇息一下,1秒后再发吧");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		case R.id.send:
			if (ToolUtils.isNetworkAvailableTip(this))
				send();
			break;
		}
	}

	@Override
	public void add(ChatMsg chatMsg) {
		if (chatMsg.getSuid() == otherUser.getUid()
				|| chatMsg.getRuid() == otherUser.getUid())
			UIHandler.obtainMessage(0, chatMsg).sendToTarget();
	}

}
