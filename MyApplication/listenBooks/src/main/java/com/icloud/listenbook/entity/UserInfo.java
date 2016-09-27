package com.icloud.listenbook.entity;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.http.datas.Login;
import com.listenBook.greendao.Baseuser;

import android.graphics.Bitmap;
import android.text.TextUtils;
/*
 * 用户信息类
 */
public class UserInfo {

	private static UserInfo instance;

	public static UserInfo instance() {
		if (instance == null) {
			instance = new UserInfo();
		}
		return instance;
	}

	protected long uid;
	protected String nick;
	protected String account;
	protected String token;
	protected String icon;
	protected String signature;
	protected Bitmap iconBmp;
	protected int vip;
	protected int friendsCount;
	protected int collectCount;
	protected double currency;//金币数量
	protected String type;
	protected String area;
	protected String phone;
	protected boolean isNew;
	protected boolean isComplete;//以完善资料？
	protected JSONObject tasks;
	protected long data_time_limit;

	public void init() {
		uid = 0;
		nick = "未登陆";
		account = "";
		token = "";
		icon = "";
		area = "";
		phone = "";
		signature = "";
		iconBmp = null;
		vip = 0;
		friendsCount = 0;
		collectCount = 0;
		isNew = false;
		tasks = new JSONObject();
	}

	public UserInfo() {
		init();
	}

	public long getDataTimeLimit() {
		return data_time_limit;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSignature() {
		return signature;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Bitmap getIconBmp() {
		return iconBmp;
	}

	public void setIconBmp(Bitmap iconBmp) {
		this.iconBmp = iconBmp;
	}

	public double getCurrency() {
		return currency;
	}

	public void setCurrency(double currency) {
		this.currency = currency;
	}
  
	/*
	 * 根据json数据来设置用户信息
	 */
	public void parse(JSONObject json) {
		init();
		nick = json.optString("nick", "");
		data_time_limit = json.optLong("data_time_limit", 0);
		uid = json.optLong("uid", 0);
		account = json.optString("account", "");
		token = json.optString("token", "");
		type = json.optString("type", "");
		currency = json.optDouble("currency", 0);
		friendsCount = json.optInt("friendsCount", 0);
		collectCount = json.optInt("collectCount", 0);
		area = json.optString("area", "");
		phone = json.optString("phone", "");
		signature = json.optString("signature", "");
		icon = json.optString("icon", "");
		isNew = json.optInt("isNew", 0) == 1;
		isComplete = json.optInt("isComplete", 0) == 1;
		tasks = json.optJSONObject("task");
		if (tasks == null) {
			try {
				tasks = new JSONObject(GameApp.instance().getString(
						R.string.task));
			} catch (Exception e) {
			}
		}

	}

	public boolean isLogin() {
		return uid > 0 && (!TextUtils.isEmpty(token));
	}

	public boolean isFastLogin() {
		return type.trim().equals(Login.TYPE_FAST);
	}

	public void reset() {
		init();
	}

	public Baseuser getUserMod(String password) {
		if (uid > 0) {
			Baseuser baseuser = new Baseuser();
			baseuser.setAccount(account);
			baseuser.setNick(nick);
			baseuser.setPwd(password);
			baseuser.setType(type);
			baseuser.setUid(uid);
			baseuser.setDate(new Date());
			return baseuser;
		} else {
			return null;
		}
	}

	public String getArea() {
		return area;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setArea(String area) {
		this.area = area;

	}

	public boolean isNew() {
		return isNew;
	}

	public JSONObject getTask() {
		return tasks;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean b) {
		isComplete = b;

	}

}
