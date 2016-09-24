package com.icloud.listenbook.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.icloud.listenbook.io.DaoManage;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.listenBook.greendao.ArticleChapterRecodeDao;
import com.listenBook.greendao.ChatPeopleInfoDao;
import com.listenBook.greendao.DaoMaster;
import com.listenBook.greendao.LessonInfoDao;
import com.listenBook.greendao.LessonMarksInfoDao;
import com.listenBook.greendao.MediaDao;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.internal.DaoConfig;

/*
 * 数据库的Helper类
 */
public class MigrationHelper {
    private static final String TAG="com.icloud.listenbook.base.MigrationHelper";
	public static void migrate(SQLiteDatabase db,
			Class<? extends AbstractDao<?, ?>>... daoClasses) {
		createNoexistTable(db);
		generateTempTables(db, daoClasses);
        dropAllTables(db, true, daoClasses);
        createAllTables(db, false, daoClasses);
        restoreData(db, daoClasses);
	}
	private static void createNoexistTable(SQLiteDatabase db){
		if (!DaoManage.instance().tabbleIsExist(db,"CHAT_PEOPLE_INFO")) {
			LogUtil.e(TAG, "ChatPeopleInfoDao.createTable\n");
			ChatPeopleInfoDao.dropTable(db, true);
			ChatPeopleInfoDao.createTable(db, false);
		}else{
			LogUtil.e(TAG, "ChatPeopleInfoDao.isexists\n");
		}
		if (!DaoManage.instance().tabbleIsExist(db,"LESSON_INFO")){
			LogUtil.e(TAG, "LessonInfoDao.createTable\n");
			LessonInfoDao.dropTable(db, true);
			LessonInfoDao.createTable(db, false);
		}else{
			LogUtil.e(TAG, "LessonInfoDao.isexists\n");
		}
		if (!DaoManage.instance().tabbleIsExist(db,"LESSON_MARKS_INFO")){
			LogUtil.e(TAG, "LessonMarksInfoDao.createTable\n");
			LessonMarksInfoDao.dropTable(db, true);
			LessonMarksInfoDao.createTable(db, false);
		}else{
			LogUtil.e(TAG, "LessonMarksInfoDao.isexists\n");
		}
		if (!DaoManage.instance().tabbleIsExist(db,"ARTICLE_CHAPTER_RECODE")){
			LogUtil.e(TAG, "ArticleChapterRecodeDao.createTable\n");
			ArticleChapterRecodeDao.dropTable(db, true);
			ArticleChapterRecodeDao.createTable(db, false);
		}else{
			LogUtil.e(TAG, "ArticleChapterRecodeDao.isexists\n");
		}
	}
	private static void generateTempTables(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
            insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
            db.execSQL(insertTableStringBuilder.toString());
        }
    }

    private static void dropAllTables(SQLiteDatabase db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
    }

    private static void createAllTables(SQLiteDatabase db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(SQLiteDatabase db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, SQLiteDatabase.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            // get all columns from tempTable, take careful to use the columns list
            List<String> columns = getColumns(db, tempTableName);
            ArrayList<String> properties = new ArrayList<String>(columns.size());
            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                if (columns.contains(columnName)) {
                    properties.add(columnName);
                }
            }
            if (properties.size() > 0) {
                final String columnSQL = TextUtils.join(",", properties);

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
            }
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    private static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<String>();
        }
        return columns;
    }
}
