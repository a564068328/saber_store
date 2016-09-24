package com.icloud.wrzjh.base.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolFactory {
	private static ThreadPoolFactory ourInstance = new ThreadPoolFactory();

	public static ThreadPoolFactory getInstance() {
		return ourInstance;
	}

	private ExecutorService threadPool;

	private ThreadPoolFactory() {
		threadPool = Executors.newCachedThreadPool();
	}

	/**
	 * 提交给线程池执行
	 */
	public void execute(Runnable run) {
		threadPool.submit(run);
	}
}
