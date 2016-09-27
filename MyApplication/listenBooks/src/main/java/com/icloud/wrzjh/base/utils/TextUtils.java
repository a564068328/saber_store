package com.icloud.wrzjh.base.utils;

import java.text.SimpleDateFormat;

public class TextUtils {
	static java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
	static SimpleDateFormat SDFTime = new SimpleDateFormat(" MM.dd HH:mm:ss");

	public static String secToMDHMS(long time) {
		return SDFTime.format(time);
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + Integer.toString(i);
		return retStr;
	}

	public static String secToHMS(int time) {
		String timeStr = "00:00";
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String conversionNum(int num) {
		return conversionNum(num, 1000);
	}

	public static String conversionNum(int num, int max) {
		if (num > max) {
			return df.format(num / (0f + max));
		} else {
			return String.valueOf(num);
		}
	}

	public static String FormetFileSize(long fileS) {
		String fileSizeString = "";
		if (fileS < 1024) {
			//fileSizeString = df.format((double) fileS) + "B";
			return "0";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static int getProgressValue(long totalBytes, long currentBytes) {
		if (totalBytes == -1 || totalBytes == 0) {
			return 0;
		}
		return (int) (currentBytes * 100f / totalBytes);
	}

	public static boolean checkAcc(String account) {
		if (android.text.TextUtils.isEmpty(account) || account.length() < 6) {
			ToastUtil.showMessage("账号长度不足6位");
			return false;
		} else if (account.length() > 20) {
			ToastUtil.showMessage("账号长度超过20位");
			return false;
		}
		return true;
	}

	public static boolean checkPwd(String password) {
		if (android.text.TextUtils.isEmpty(password) || password.length() < 6) {
			ToastUtil.showMessage("密码长度不足6位");
			return false;
		} else if (password.length() > 10) {
			ToastUtil.showMessage("密码长度超过10位");
			return false;
		}
		return true;
	}

	public static boolean checkPwd(String password, String confirmPassword) {
		if (!password.equals(confirmPassword)) {
			ToastUtil.showMessage("两次密码输入不一致");
			return false;
		}
		return checkPwd(password);
	}
}
