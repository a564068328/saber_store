package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.entity.FeedbackItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.FeedbackAct;
import com.icloud.wrzjh.base.utils.ToastUtil;

public class FeedbackAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<FeedbackItem> data;
	FeedbackAct act;

	public FeedbackAdapter(FeedbackAct act, ArrayList<FeedbackItem> data) {
		this.act = act;
		this.data = data;
	}

	public void upData(ArrayList<FeedbackItem> data) {
		if (this.data != data) {
			data.clear();
			this.data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public int getItemViewType(int pos) {
		return data.get(pos).viewType;
	}

	public class FeedbackHolder extends RecyclerView.ViewHolder {
		TextView msg;
		TextView time;

		public FeedbackHolder(View view) {
			super(view);
			msg = (TextView) view.findViewById(R.id.msg);
			time = (TextView) view.findViewById(R.id.time);
		}

		public void setView(int pos) {
			FeedbackItem item = data.get(pos);
			msg.setText(item.getMsg());
			time.setText(item.getDateline());
		}
	}

	public class ChipFeedbackHolder extends RecyclerView.ViewHolder implements
			OnClickListener, TextView.OnEditorActionListener {
		TextView msg;
		TextView time;
		TextView reply;
		EditText edit;

		public ChipFeedbackHolder(View view) {
			super(view);
			msg = (TextView) view.findViewById(R.id.msg);
			time = (TextView) view.findViewById(R.id.time);
			reply = (TextView) view.findViewById(R.id.reply);
			edit = (EditText) view.findViewById(R.id.edit);
			msg.setOnClickListener(this);
			reply.setOnClickListener(this);
			edit.setOnEditorActionListener(this);
			edit.setImeOptions(EditorInfo.IME_ACTION_SEND);
		}

		public void setView(int pos) {
			FeedbackItem item = data.get(pos);
			String msgStr = (item.getUid() == 10000) ? "客服:" : "我:";
			msgStr += item.getMsg();
			msg.setText(msgStr);
			time.setText(item.getDateline());
			reply.setVisibility((item.getUid() == 10000) ? View.VISIBLE
					: View.GONE);
			msg.setTag(pos);
			reply.setTag(pos);
			msg.setTag(pos);
			edit.setTag(pos);
			edit.setVisibility((reply.getVisibility() == View.VISIBLE && item.isShowEdit) ? View.VISIBLE
					: View.GONE);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			int postion = (Integer) v.getTag();
			FeedbackItem item = data.get(postion);
			if (item.getUid() != 10000)
				return;
			switch (id) {
			case R.id.msg:
			case R.id.reply:
				boolean isShowEdit = item.isShowEdit;
				for (int i = 0; i < data.size(); i++) {
					data.get(i).isShowEdit = false;
				}
				item.isShowEdit = !isShowEdit;
				FeedbackAdapter.this.notifyDataSetChanged();
				break;
			}

		}

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String msgStr = v.getText().toString();
			switch (actionId) {
			case EditorInfo.IME_NULL:
				break;
			case EditorInfo.IME_ACTION_SEND:
			case EditorInfo.IME_ACTION_GO:
			case EditorInfo.IME_ACTION_SEARCH:
				if (!TextUtils.isEmpty(msgStr) && msgStr.length() > 6) {
					edit.setText("");
					int postion = (Integer) edit.getTag();
					FeedbackItem item = data.get(postion);
					item.isShowEdit = false;
					FeedbackAdapter.this.notifyDataSetChanged();
					act.sendFeedback(item.getFid(), msgStr);
				} else {
					ToastUtil.showMessage("回复长度至少为6");
				}
				break;
			case EditorInfo.IME_ACTION_DONE:
				break;
			}
			return true;
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {

		if (viewHolder instanceof ChipFeedbackHolder) {
			ChipFeedbackHolder headerHolder = (ChipFeedbackHolder) viewHolder;
			headerHolder.setView(pos);
		} else if (viewHolder instanceof FeedbackHolder) {
			FeedbackHolder holder = (FeedbackHolder) viewHolder;
			holder.setView(pos);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_TITLE) {
			view = LayoutInflater.from(act).inflate(R.layout.feedback_item,
					parent, false);
			return new FeedbackHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.feedback_chip_item, parent, false);
			return new ChipFeedbackHolder(view);
		}
	}
}
