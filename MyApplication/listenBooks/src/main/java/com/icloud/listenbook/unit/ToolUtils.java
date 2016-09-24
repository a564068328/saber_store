package com.icloud.listenbook.unit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.android.volley.Response.Listener;
import com.icloud.listenbook.R;
import com.icloud.listenbook.base.ActivityUtils;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.LoadingActivity;
import com.icloud.listenbook.ui.TablesActivity;
import com.icloud.listenbook.ui.adapter.entity.RightInfo;
import com.icloud.listenbook.ui.chipAct.ChantAct;
import com.icloud.listenbook.ui.chipAct.LoginAct;
import com.icloud.listenbook.ui.chipAct.SystemMsgAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.listenBook.greendao.Collect;
import com.listenBook.greendao.LessonMarksInfo;

public class ToolUtils {
	public static final int isNoComplete = 0;
	public static final int isComplete = isNoComplete + 1;

	public static boolean isSameDay(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		int lastDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(new Date());
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		if (lastDayOfYear == dayOfYear)
			return true;
		return false;
	}

	public static Class<?> stringToClass(String param) {
		Class<?> obj = null;
		if (!TextUtils.isEmpty(param)) {
			try {
				obj = Class.forName(param);
			} catch (Exception e) {
				obj = null;
			}
		}
		return obj;
	}

	public static void showNotificationMusic(Context ctx, int id, String title,
			String msg, Class<?> intentClass) {
		Intent intent = new Intent(ctx, intentClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		showNotification(ctx, id, title, msg, intent,
				Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT);
	}

	public static void showSystemNotification(String msg) {
		Activity act = ActivityUtils.instance().getCurrAct();
		if (act instanceof SystemMsgAct || act.isFinishing()) {
			return;
		}
		Intent intent = new Intent(TablesActivity.OPEN_MEDIA);
		intent.setClass(act, SystemMsgAct.class);
		int SYSTEM_NOTIFY_ID = 12460;
		ToolUtils.showNotification(act, SYSTEM_NOTIFY_ID,
				act.getString(R.string.SystemMsg), msg, intent,
				Notification.FLAG_AUTO_CANCEL);
	}

	public static void showNotification(Context ctx, int id, String title,
			String msg, Intent intent, int flags) {
		NotificationManager manager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				ctx.getApplicationContext());
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(0);
		builder.setAutoCancel(true);
		builder.setContentTitle(title);
		builder.setContentText(msg);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pIntent);
		Notification notification = builder.build();
		notification.flags = flags;
		manager.notify(id, notification);
	}

	public static void showNotification(Context ctx, int id, String title,
			String name, String msg, Intent intent, int flags, boolean defaults) {
		NotificationManager manager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				ctx.getApplicationContext());
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setDefaults(0);
		builder.setAutoCancel(true);
		builder.setContentTitle(title);
		builder.setContentText(name + ":" + msg);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pIntent);
		Notification notification = builder.build();
		if (SharedPreferenceUtil.getChatmsgNotifyVoice())
			notification.defaults |= Notification.DEFAULT_SOUND; // 使用默认的声音
		if (SharedPreferenceUtil.getChatmsgNotifyShark())
			notification.defaults |= Notification.DEFAULT_VIBRATE; // 使用默认的震动
		if (SharedPreferenceUtil.getChatmsgNotifyDetial())
			notification.tickerText = name + ":" + msg;// 设置显示提示信息，该信息也在状态栏显示
		notification.flags = flags;
		manager.notify(id, notification);
	}

	public static void showNotification(Context ctx, int id, String title,
			String msg, Class<?> intentClass, String value) {
		Intent intent = new Intent(ctx, intentClass);
		intent.putExtra("value", value);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		showNotification(ctx, id, title, msg, intent, Notification.DEFAULT_ALL);
	}

	public static void delNotification(Context ctx, int id) {
		NotificationManager manager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id);
	}

	/** 无网提示 */
	public static boolean isNetworkAvailableTip(Context context) {
		if (!HttpUtils.isNetworkAvailable(context)) {
			ToastUtil.showMessage(R.string.no_network_warning);
			return false;
		}
		return true;
	}

	/** 默认分享 */
	public static void shareDefa(Activity act, String name) {
		String msg = act.getResources().getString(R.string.share_defa);
		msg = String.format(msg, name);
		msg += ",下载地址http://988wan.com/index.php?ac=appdetail&id=92";
		share(act, act.getString(R.string.app_name), msg);
	}

	/** 分享 */
	public static void share(Activity act, String title, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		/** 分享类型 */
		// intent.setType("image/*");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		/** 图片链接 */
		// intent.putExtra(Intent.EXTRA_STREAM, url);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		act.startActivity(Intent.createChooser(intent,
				act.getString(R.string.app_name)));
	}

	/** 是否收藏 */
	public static boolean isCollect(long aid) {
		return !UserInfo.instance().isLogin()
				|| (IoUtils.instance().getCollect(aid,
						UserInfo.instance().getUid()) == null);
	}

	/** 收藏 */
	public static void collect(Activity act, long aid) {
		if (!UserInfo.instance().isLogin()) {
			ToastUtil.showMessage(R.string.request_login);
			/** 跳转到登陆页面 */
			LoadingTool.launchActivity(act, LoginAct.class);
			return;
		}
		Collect collect = IoUtils.instance().getCollect(aid,
				UserInfo.instance().getUid());
		if (collect == null) {
			HttpUtils.collectAdd(aid, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtil.e("collectAdd", response.toString());
				}
			}, null);
			IoUtils.instance().saveCollect(aid, UserInfo.instance().getUid());
		} else {
			HttpUtils.collectDel(aid, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtil.e("collectDel", response.toString());
				}
			}, null);
			IoUtils.instance().delCollect(collect);
		}
	}

	// 去除字符串中的空格、回车、换行符、制表符
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	// 数字to字母
	private static final int[] number = { 0, 1, 2, 3, 4, 5, 6 };
	private static final String[] letter = { "a", "b", "c", "d", "e", "f", "g" };

	public static String numToLetter(int id) {
		for (int i = 0; i < number.length; i++) {
			if (id == number[i])
				return letter[i];
		}
		return " ";
	}

	// 评分
	public static String checkAnswer(String date, int completeSize) {
		LessonMarksInfo marksInfo = IoUtils.instance().getLessonMarksInfo(date);
		ArrayList<LessonItem> datas = JsonUtils.toLessonItems(date);
		List<RightInfo> wholeRight = new ArrayList<RightInfo>();
		List<RightInfo> halfRight = new ArrayList<RightInfo>();
		for (LessonItem data : datas) {
			if (data.type == LessonItem.CHOICE) {
				// 部分对了一半分
				boolean half = false;
				// // 答案全为空就是错
				// int whiteCount = 0;
				// for (int i = 0; i < data.answer.length; i++) {
				// if (data.answer[i].equals(" ")
				// && !data.answer[i].equals(data.user_answer[i])) {
				// break;
				// } else if (!data.answer[i].equals(" ")
				// && data.user_answer[i])
				// half = true;
				// if (data.user_answer[i].equals(" "))
				// whiteCount++;
				// if (whiteCount == data.answer.length - 1) {
				// marksInfo.setMarks(marksInfo.getMarks());
				// LogUtil.e("TAG", "data.position" + data.position);
				// } else if (i == data.answer.length - 1 && half) {
				// marksInfo.setMarks(marksInfo.getMarks() + data.marks
				// / 2);
				// halfRight.add(data.position + 1);
				// } else if (i == data.answer.length - 1 && !half) {
				// marksInfo.setMarks(marksInfo.getMarks() + data.marks);
				// wholeRight.add(data.position + 1);
				// }
				for (int i = 0; i < data.answer.length; i++) {
					if (data.answer[i].equals(" ")
							&& !data.answer[i].equals(data.user_answer[i]))
						break;
					if (!data.answer[i].equals(" ")
							&& data.user_answer[i].equals(" "))
						half = true;
					if (i == data.answer.length - 1) {
						if (half == true) {
							marksInfo.setMarks(marksInfo.getMarks()
									+ data.marks/2);
							halfRight.add(new RightInfo(data.position
									- completeSize + 1, LessonItem.CHOICE));
						} else {
							marksInfo.setMarks(marksInfo.getMarks()
									+ data.marks);
							wholeRight.add(new RightInfo(data.position
									- completeSize + 1, LessonItem.CHOICE));
						}
					}
				}
				// }
			} else if (data.type == LessonItem.COMPLETION) {
				// 部分对了一半分
				boolean half = false;
				boolean error = false;
				int errorCount = 0;
				for (int i = 0; i < data.answer.length; i++) {
					String user_answer = replaceBlank(data.user_answer[i]);
					if (!data.answer[i].equals(user_answer))
						errorCount++;
				}
				if (errorCount == 0) {
					wholeRight.add(new RightInfo(data.position + 1,
							LessonItem.COMPLETION));
					marksInfo.setMarks(marksInfo.getMarks() + data.marks);
				} else if (errorCount < data.answer.length) {
					halfRight.add(new RightInfo(data.position + 1,
							LessonItem.COMPLETION));
					marksInfo.setMarks(marksInfo.getMarks() + data.marks / 2);
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append("您此次得分为").append(marksInfo.getMarks()).append("分。");
		if (wholeRight.size() != 0) {
			builder.append("其中完全做对的题有").append(wholeRight.size())
					.append("道，分别是：");

			for (RightInfo i : wholeRight) {
				if (i.issueType == LessonItem.CHOICE) {
					builder.append("选择题").append("第").append(i.posotion)
							.append("题，");
				} else {
					builder.append("填空题").append("第").append(i.posotion)
							.append("题，");
				}
			}
		}
		if (halfRight.size() != 0) {
			builder.append("其中部分做对的题有").append(halfRight.size())
					.append("道，分别是：");
			for (RightInfo i : halfRight) {
				if (i.issueType == LessonItem.CHOICE) {
					builder.append("选择题").append("第").append(i.posotion)
							.append("题，");
				} else {
					builder.append("填空题").append("第").append(i.posotion)
							.append("题，");
				}
			}
		}

		if (marksInfo.getMarks() <= 10) {
			marksInfo.setDate(datas.get(0).date);
			marksInfo.setMarks(0);
			marksInfo.setDescribe("未完成");
			marksInfo.setIscomplete(false);
			marksInfo.setRight_count(0);
			IoUtils.instance().saveLessonMarksInfo(marksInfo);
			return "您的得分太少了，请重新完成功课吧";
		}
		marksInfo.setDate(datas.get(0).date);
		marksInfo.setMarks(marksInfo.getMarks());
		marksInfo.setDescribe(builder.toString());
		marksInfo.setIscomplete(true);
		marksInfo.setRight_count(0);
		IoUtils.instance().saveLessonMarksInfo(marksInfo);
		builder.append("每道题的正确答案已经显示在题目下方了，请注意对比下答案吧!");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
