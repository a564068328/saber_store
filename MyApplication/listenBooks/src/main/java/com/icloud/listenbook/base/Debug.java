package com.icloud.listenbook.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import com.icloud.listenbook.http.HttpUtils;
import com.icloud.wrzjh.base.utils.LogUtil;

/**
 * Uncaught Exception Handler.<br>
 * 每个线程(主线程/显式创建的或隐式创建的线程)尽量在开始时调用 Debug.threadInit()
 */

public class Debug {

	public class DefaultExceptionHandler implements UncaughtExceptionHandler {

		public DefaultExceptionHandler() {
		}

		public void uncaughtException(Thread t, Throwable e) {
			final String info = genStackTrace(e);
			new Thread(new Runnable() {
				public void run() {
					HttpUtils.reportException(info);
					try {
						System.exit(0);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			LogUtil.e(t, info);
		}
	}

	public DefaultExceptionHandler handler = new DefaultExceptionHandler();

	public void set() {
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	public static String genStackTrace(Throwable e) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stackTrace = result.toString();
		printWriter.close();
		return stackTrace;
	}

	public static String MName() {
		StackTraceElement stackTraceElements[] = (new Throwable())
				.getStackTrace();
		return stackTraceElements[1].toString();
	}

	public static Debug self = null;

	public static void threadInit() {
		if (null == self) {
			self = new Debug();
		}
		self.set();
	}
}
