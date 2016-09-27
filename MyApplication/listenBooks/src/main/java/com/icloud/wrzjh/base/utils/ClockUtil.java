package com.icloud.wrzjh.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClockUtil {
	private static final SimpleDateFormat dformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String getClock() {
		return dformat.format(Calendar.getInstance().getTimeInMillis());
	}
}
