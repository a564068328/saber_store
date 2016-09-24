package com.icloud.wrzjh.base.utils;

import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {
	/**
	 * 实现文本复制功能 
	 * 
	 * @param content
	 */
	public static void copy(String str, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(str.trim());
	}

	/**
	 * 实现粘贴功能 
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}
}
