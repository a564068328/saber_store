package com.icloud.wrzjh.base.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.ProjectConfig;
import com.icloud.wrzjh.base.utils.LogUtil;

public class FileUtils {
	/**
	 * 判断SD卡是否可�?
	 * */
	public static boolean isSDCardMounted() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		}
		return false;
	}

	/** 20130630 */
	public static int getCurrTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		String m = (month >= 10) ? (month + "") : ("0" + month);
		String d = (day >= 10) ? (day + "") : ("0" + day);

		String time = year + m + d;
		try {
			return Integer.parseInt(time);
		} catch (Exception e) {
			LogUtil.e("getCurrTime", e);
		}
		return year + month + day;
	}

	/**
	 * 获取SD根目�?
	 * */
	public static String getRoot() {
		String rootDir = null;
		if (isSDCardMounted()) {
			rootDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		return rootDir;
	}

	public static void createFile(String path, byte[] content) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(path);
			fos.write(content);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 当图标超过部分时清空 */
	public static void deleteIcons() {
		if (isSDCardMounted()) {
			try {
				File file = new File(ProjectConfig.ICON_CACHE);
				File[] icons = file.listFiles();
				int length = icons.length;
				if (icons != null && length > 300) {
					for (int i = 0; i < length; i++) {
						if (icons[i].isFile())
							icons[i].deleteOnExit();
					}
				}
			} catch (Exception e) {
				LogUtil.e("FileUtil deleteIcons", e);
			}
		}
	}

	/**
	 * 获取应用程序SD输出根目�?
	 * */
	public static String getRootCache(Context pContext) {
		String rootDir = null;
		if (isSDCardMounted()) {
			rootDir = getRoot() + "/wrzjh/" + pContext.getPackageName();
		}
		return rootDir;
	}

	public static String getUpdateDown() {
		String rootDir = null;
		if (isSDCardMounted()) {
			rootDir = getRoot() + "/wrzjh/";
		}
		return rootDir;
	}

	public static String getIconPath(String icon) {
		try {
			String cacheFile = ProjectConfig.ICON_CACHE
					+ (icon.substring(icon.lastIndexOf("/") + 1,
							icon.lastIndexOf("."))) + ".img";
			return cacheFile;
		} catch (Exception e) {
		}
		return "";
	}

	public static String getMarketPath(String icon) {
		try {
			String cacheFile = ProjectConfig.MARKET_CACHE
					+ (icon.substring(icon.lastIndexOf("/") + 1,
							icon.lastIndexOf("."))) + ".png";
			return cacheFile;
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 获取应用程序SD日志输出目录
	 * */
	public static String getLogPath(Context pContext) {
		String logPath = getRootCache(pContext) + File.separator + "/zhajinhua";
		try {
			createFile(logPath, "log.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logPath;
	}

	public static void clearLog(Context pContext) {
		if (!GameApp.DEBUG)
			return;

		String logPath = getRootCache(pContext) + File.separator + "/zhajinhua";
		try {
			File file = new File(logPath);
			if (file.exists() && file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					f.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 创建文件
	 * 
	 * @param pPath
	 *            路径名称SDROOT/log
	 * @param pName
	 *            文件名称error.txt
	 * */
	public static boolean createFile(String pPath, String pName)
			throws IOException {

		if (TextUtils.isEmpty(pPath) || TextUtils.isEmpty(pPath)) {
			return false;
		}
		File dir = new File(pPath);
		if (dir.isDirectory() || dir.mkdirs()) {
			String tFullPath = pPath + "/" + pName;
			File file = new File(tFullPath);
			if (file.exists() || file.createNewFile()) {
				return true;
			}
		}
		return false;
	}

	public static void createFile(String name) throws IOException {
		File file = new File(name);
		if (file.exists())
			return;
		file.createNewFile();
	}

	public static boolean writePrivateContent(String fileName, String content) {
		FileOutputStream fos = null;
		try {
			LogUtil.d("FileUtils writePrivateContent", content);
			fos = GameApp.instance().openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static String readPrivateContent(String fileName) {
		FileInputStream fis = null;
		try {
			fis = GameApp.instance().openFileInput(fileName);
			String res = IOUtils.toString(fis, "UTF-8");
			fis.close();
			LogUtil.d("FileUtils readPrivateContent", res);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void clearPrivateContent(String path) {
		try {
			File file = GameApp.instance().getFilesDir();
			File f = new File(file.getAbsolutePath() + "/" + path);
			if (f.exists())
				f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isExists(String path) {
		File file = new File(path);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

}
