package com.icloud.listenbook.unit;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

import com.android.volley.Response.Listener;
import com.icloud.listenbook.R;
import com.icloud.listenbook.dialog.DialogManage;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.WapActivity;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;

public class AccUtils {
	public static final int SHOW_COMPLETE_INTERVAL = (24 * 60 * 60 * 1000);

	public static boolean onBackPressed(Activity act) {
		/** 未完善用户每日退出提示绑定账号赠送金币 */
		/** 新用户提示5次是否给予意见反馈 **/
		/** 已登陆 */
		if (UserInfo.instance().isLogin()
				&& UserInfo.instance().getTask() != null) {
			JSONObject task = UserInfo.instance().getTask();
			/** 优先提示是否意见反馈 */
			if (checkFeedback(task.optJSONObject("feedback"), act)) {
				return true;
			} else if (!UserInfo.instance().isComplete()) {
				return checkComplete(task.optJSONObject("complete"), act);
			}

		}
		return false;
	}

	public static boolean checkComplete(JSONObject item, Activity act) {
		if (item == null)
			return false;
		String title = item.optString("title");
		if (!TextUtils.isEmpty(title)) {
			long time = System.currentTimeMillis()
					- SharedPreferenceUtil.getCompleteAccInterval(String
							.valueOf(UserInfo.instance().getUid()));
			if (time > SHOW_COMPLETE_INTERVAL) {
				DialogManage.showCompleteDig(act, title);
				SharedPreferenceUtil.saveCompleteAccInterval(
						String.valueOf(UserInfo.instance().getUid()),
						System.currentTimeMillis());
				return true;
			}
		}
		return false;
	}

	public static boolean checkFeedback(JSONObject item, Activity act) {
		if (item == null)
			return false;
		String title = item.optString("title", null);
		int conut = item.optInt("conut", 0);
		if (!TextUtils.isEmpty(title)) {
			int saveConut = SharedPreferenceUtil.getFeedbackTipConut(String
					.valueOf(UserInfo.instance().getUid()));
			if (saveConut > conut)
				return false;
			else {
				DialogManage.showFeedbackDig(act, title);
				SharedPreferenceUtil.saveFeedbackTipConut(
						String.valueOf(UserInfo.instance().getUid()),
						saveConut + 1);
				return true;
			}
		}
		return false;
	}

	public static void savePaySucc(String id) {
		SharedPreferenceUtil.savePaySuccTime(String.valueOf(id),
				System.currentTimeMillis());
	}

	public static boolean checkIsPay(String id) {
		/** 获取ID */
		long time = System.currentTimeMillis()
				- SharedPreferenceUtil.getPaySuccTime(String.valueOf(id));
		if (time <= (UserInfo.instance().getDataTimeLimit() * 1000)) {
			return true;
		}
		return false;
	}

	public static boolean checkPay(Activity act, ArticleChapterItem playItem) {
		if (playItem.getMCurrency() > 0) {
			if (UserInfo.instance().isLogin()) {
				if (UserInfo.instance().getCurrency() < playItem.getMCurrency()
						&& (!checkIsPay(playItem.getAid() + ":"
								+ playItem.getCpId()))) {
					AccUtils.showTip(act, playItem);
					return false;
				} else
					AccUtils.deductCurrency(act, playItem.getAid(),
							playItem.getCpId());
			} else {
				LoadingTool.launchActivity(act, LoginAct.class);
				return false;
			}
		}
		return true;
	}

	protected static void deductCurrency(final Activity act, final long aid,
			final long cpid) {
		HttpUtils.deductCurrency(aid, cpid, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				int result = response.optInt("result", -1);
				if (result == 0) {
					savePaySucc(aid + ":" + cpid);
					UserInfo.instance().setCurrency(
							response.optDouble("currency", UserInfo.instance()
									.getCurrency()));
				} else {
					String msg = response.optString("msg");
					if (!TextUtils.isEmpty(msg)) {
						DialogManage.showAlertDlg(act, R.string.currency_un,
								msg, R.string.buy, new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										/* 跳转到购买界面* */LoadingTool
												.launchActivity(act,
														WapActivity.class);
									}
								}, R.string.cancel);
					}
				}
			}
		});
	}

	public static void showTip(final Activity act, ArticleChapterItem article) {
		String cpName = article.getCpName();
		if ((cpName.indexOf("《") == -1) && (cpName.indexOf("》") == -1))
			cpName = "《" + cpName + "》";
		String msg = act.getString(R.string.currency_un_tip, cpName,
				article.getMCurrency(), UserInfo.instance().getCurrency());

		DialogManage.showAlertDlg(act, R.string.currency_un, msg, R.string.buy,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LoadingTool.launchActivity(act, WapActivity.class);
					}
				}, R.string.cancel);
	}
}
