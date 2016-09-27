package com.icloud.wrzjh.base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.icloud.listenbook.base.GameApp;
import com.icloud.wrzjh.base.utils.io.FileUtils;

/**
 * Wrapper of android.util.Log.<br>
 * 支持写到SD卡文<br>
 */

public class LogUtil {
	public static final int LOG_LEVEL_VERBOSE = 0x0001; //
	public static final int LOG_LEVEL_INFO = 0x0002;
	public static final int LOG_LEVEL_WARNING = 0x0004;
	public static final int LOG_LEVEL_DEBUG = 0x0008;
	public static final int LOG_LEVEL_ERROR = 0x0010;

	public static final int FOR_DEBUG = 0x0100;

	private static final String LOG_SUFFIX = ".log";
	private static final String LOG_STRIP = " ";
	private static final SimpleDateFormat dformat = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss-SSS");
	private static byte[] sync = new byte[0];
	private static int num;
	private static int sequence;
	private static String logPath;
	private static final int maxSequence = 8000;

	public static void init(Context pContext) {
		sequence = 0;
		num = 0;
		logPath = FileUtils.getLogPath(pContext);
	}

	public static void v(Object obj, String info) {

		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.v(obj.getClass().getName(), info);
		// LogUtil.LogFile(obj.getClass().getName() + LOG_STRIP + info);
	}

	public static void e(Object obj, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.e(obj.getClass().getName(), info);
		LogUtil.LogFile(obj.getClass().getName() + LOG_STRIP + info);
	}

	public static void i(Object obj, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.i(obj.getClass().getName(), info);
		LogUtil.LogFile(obj.getClass().getName() + LOG_STRIP + info);
	}

	public static void d(Object obj, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.d(obj.getClass().getName(), info);
		// LogUtil.LogFile(obj.getClass().getName() + LOG_STRIP + info);
	}

	public static void w(Object obj, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.w(obj.getClass().getName(), info);
		LogUtil.LogFile(obj.getClass().getName() + LOG_STRIP + info);
	}

	public static void v(String name, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.v(name, info);
		// LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void e(String name, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.e(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void i(String name, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.i(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void i(Object info) {
		if (info == null)
			return;
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.i("sysout", info.toString());
		LogUtil.LogFile("sysout" + info);
	}

	public static void d(String name, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.d(name, info);
		// LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void w(String name, String info) {
		if (!GameApp.DEBUG)
			return;
		info = catInfo(info);
		android.util.Log.w(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void v(String name, Exception e) {
		if (!GameApp.DEBUG)
			return;
		String info = genStackTrace(e);
		android.util.Log.v(name, info);
		// LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void e(String name, Exception e) {
		if (!GameApp.DEBUG)
			return;
		String info = genStackTrace(e);
		android.util.Log.e(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void i(String name, Exception e) {
		if (!GameApp.DEBUG)
			return;
		String info = genStackTrace(e);
		android.util.Log.i(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void d(String name, Exception e) {
		if (!GameApp.DEBUG)
			return;
		String info = genStackTrace(e);
		android.util.Log.d(name, info);
		// LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void w(String name, Exception e) {
		if (!GameApp.DEBUG)
			return;
		String info = genStackTrace(e);
		android.util.Log.w(name, info);
		LogUtil.LogFile(name + LOG_STRIP + info);
	}

	public static void crash(Context pContext, Throwable ex) {
		e("Exception", ex.getMessage());
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : DeviceUtils.collectDeviceInfo(
				pContext).entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		sb.append(genStackTrace(ex));
		try {
			if (!FileUtils.isSDCardMounted())
				return;
			long timestamp = System.currentTimeMillis();
			String time = dformat.format(new Date());
			String fileName = "error-" + time + "-" + timestamp + LOG_SUFFIX;
			FileOutputStream fos = new FileOutputStream(logPath
					+ File.separator + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("crash", "an error occured while writing file...", e);
		}
	}

	/**
	 * 利用异常获取堆栈信息
	 * */
	public static String genStackTrace(Throwable e) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String stackTrace = writer.toString();
		return stackTrace;
	}

	/**
	 * 将日志输出到SD目录中的日志文件
	 * */
	private static void LogFile(String info) {
		synchronized (LogUtil.sync) {
			if (!FileUtils.isSDCardMounted() || !GameApp.DEBUG)
				return;
			try {
				num = sequence / maxSequence;
				FileOutputStream fos = new FileOutputStream(logPath + "/log"
						+ num + LOG_SUFFIX, true);
				info = dformat.format(Calendar.getInstance().getTimeInMillis())
						+ LOG_STRIP + info;
				fos.write(info.getBytes());
				fos.write('\n');
				fos.close();
			} catch (IOException ex) {
				//Log.e("crash", "an error occured while writing file...", ex);
			}
		}
	}

	/**
	 * 生成要输出的信息
	 * */
	private static String catInfo(Object... objects) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[ThreadID=%04d]", Thread.currentThread()
				.getId()));
		sb.append(LOG_STRIP);
		for (Object obj : objects) {
			sb.append(String.valueOf(obj)).append(LOG_STRIP);
		}
		return sb.toString();
	}

}
