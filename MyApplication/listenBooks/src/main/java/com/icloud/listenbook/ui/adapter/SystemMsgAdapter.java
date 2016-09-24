package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.DownBookAdapter.ViewHolder;
import com.icloud.listenbook.unit.LruIcoCache;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.ChatMsg;

public class SystemMsgAdapter extends BaseAdapter implements OnClickListener {
	ArrayList<ChatMsg> data;
	Context context;
	LayoutInflater mInflater;

	public SystemMsgAdapter(Context context, ArrayList<ChatMsg> data) {
		this.data = data;
		mInflater = LayoutInflater.from(context);
	}

	public void upData(ArrayList<ChatMsg> data) {
		if (this.data != data) {
			this.data.clear();
			this.data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView msg;
		TextView time;
		View del;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_system_msg, null);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.msg = (TextView) convertView.findViewById(R.id.msg);
			holder.del = convertView.findViewById(R.id.del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChatMsg item = data.get(position);
		holder.time.setText(com.icloud.wrzjh.base.utils.TextUtils
				.secToMDHMS(item.getDateline()));
		holder.del.setTag(position);
		holder.del.setOnClickListener(this);
		if (TextUtils.isEmpty(item.getMsg()))
			holder.msg.setText("");
		else
			holder.msg.setText(Html.fromHtml(item.getMsg()));
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.del) {
			int postion = (Integer) v.getTag();
			IoUtils.instance().removeCharMsg(data.get(postion));
			data.remove(postion);
			this.notifyDataSetChanged();

		}

	}
}
