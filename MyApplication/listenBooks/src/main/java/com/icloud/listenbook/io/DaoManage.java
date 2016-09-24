package com.icloud.listenbook.io;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.MySQLiteOpenHelper;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.DaoMaster;
import com.listenBook.greendao.DaoMaster.OpenHelper;
import com.listenBook.greendao.DaoSession;

public class DaoManage {
	static DaoManage instance;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	public SQLiteDatabase db;
	// 数据库名，表名是自动被创建的
	public static final String DB_NAME = "dbname2.db";
	public final String TAG = getClass().getName();

	public static DaoManage instance() {
		if (instance == null)
			instance = new DaoManage();
		return instance;
	}

	public DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context,
					DB_NAME, null);
			// OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME,
			// null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public SQLiteDatabase getSQLDatebase(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			db = daoMaster.getDatabase();
		}
		return db;
	}

	public void init(Context context) {

	}

	public boolean tabbleIsExist(SQLiteDatabase db, String tableName) {
		boolean result = false;
		if (tableName == null || db == null) {
			return false;
		}
		// Cursor cursor = null;
		// try {
		// String sql =
		// "select count(*) as c from sqlite_master where type ='table' and name ='"
		// + tableName.trim() + "' ";
		// cursor = db.rawQuery(sql, null);
		// if (cursor.moveToNext()) {
		// int count = cursor.getInt(0);
		// if (count > 0) {
		// result = true;
		// }
		// }
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// } finally {
		// cursor.close();
		// }
		List<String> allTableName = DaoManage.instance().getAllTableName(db);
		for (String str : allTableName) {
			if (str.equals(tableName)) {
				return true;
			}
		}
		return result;
	}

	public List<String> getAllTableName(SQLiteDatabase db) {
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.rawQuery(
				"select name from sqlite_master where type='table';", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				// 遍历出表名
				String name = cursor.getString(0);
				// LogUtil.e(TAG, "遍历出表名"+name);
				list.add(name);
			}
			cursor.close();
			cursor = null;
		}
		return list;
	}
}
