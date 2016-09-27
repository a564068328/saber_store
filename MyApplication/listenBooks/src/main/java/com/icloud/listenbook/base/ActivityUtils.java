package com.icloud.listenbook.base;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityUtils {
	protected ArrayList<Activity> currentActs;
	protected ArrayList<ICommAct> icommActs;
	protected static ActivityUtils instance;

	public static ActivityUtils instance() {
		if (instance == null)
			instance = new ActivityUtils();
		return instance;
	}

	private ActivityUtils() {
		currentActs = new ArrayList<Activity>();
		icommActs = new ArrayList<ICommAct>();
	}

	public Activity getCurrAct() {
		int index = currentActs.size() - 1;
		return index >= 0 && index <= currentActs.size() ? currentActs
				.get(index) : null;
	}

	public synchronized void removeAct(android.app.Activity currentAct,
			ICommAct icommAct) {
		this.currentActs.remove(currentAct);
		this.icommActs.remove(icommAct);
	}

	public synchronized void addCurrentAct(Activity currentAct,
			ICommAct icommAct) {
		if (currentActs.contains(currentAct))
			this.currentActs.remove(currentAct);
		if (icommActs.contains(currentAct))
			this.icommActs.remove(currentAct);
		this.currentActs.add(currentAct);
		this.icommActs.add(icommAct);
	}

	public static void init() {
		instance();

	}
}
