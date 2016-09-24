package com.icloud.listenbook.unit;

import java.util.HashSet;

import org.json.JSONObject;

import android.support.v4.util.LongSparseArray;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.icloud.listenbook.http.HttpUtils;

public class UserIconManage implements Listener<JSONObject>, ErrorListener {
	public static final long LOAD_TIME_SPAN = 1000 * 15;
	LruCache<Long, String> iconArray;
	LongSparseArray<Long> downList;
	static UserIconManage instance;
	HashSet<IconListener> Imagelist;

	public static UserIconManage instance() {
		if (instance == null) {
			instance = new UserIconManage();
		}

		return instance;
	}

	private UserIconManage() {
		iconArray = new LruCache<Long, String>(50);
		downList = new LongSparseArray<Long>();
		Imagelist = new HashSet<UserIconManage.IconListener>();
	}

	public String getUserIcon(long uid) {
		String url = iconArray.get(uid);
		Long time = downList.get(uid);
		if (time != null
				&& ((System.currentTimeMillis() - time) > LOAD_TIME_SPAN)) {
			downList.remove(uid);
			time = null;
		}
		if (url == null && time == null) {
			downList.append(uid, System.currentTimeMillis());
			HttpUtils.getUserIcon(uid, this, this);
		}
		return url;
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			int res = response.optInt("result", -1);
			if (res == 0) {
				String icon = response.optString("icon");
				long fuid = response.optLong("fuid");
				iconArray.put(fuid, icon);
				if (!TextUtils.isEmpty(icon)) {
					for (IconListener item : Imagelist)
						item.add();
				}
				downList.remove(fuid);
			} else {

			}
		} catch (Exception e) {
		}
	}

	public void addImageListener(IconListener list) {
		Imagelist.add(list);

	}

	public void removeImageListener(IconListener list) {
		Imagelist.remove(list);
	}

	public interface IconListener {
		public void add();
	}
}
