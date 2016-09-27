package com.icloud.listenbook.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.AdsDao;
import com.listenBook.greendao.ArticleChapterInfoDao;
import com.listenBook.greendao.ArticleChapterRecodeDao;
import com.listenBook.greendao.ArticleDao;
import com.listenBook.greendao.ArticleFeedbackDao;
import com.listenBook.greendao.BaseuserDao;
import com.listenBook.greendao.CategoryDao;
import com.listenBook.greendao.ChatMsgDao;
import com.listenBook.greendao.ChatPeopleInfoDao;
import com.listenBook.greendao.ChipFeedbackDao;
import com.listenBook.greendao.CollectDao;
import com.listenBook.greendao.DaoMaster;
import com.listenBook.greendao.DownDao;
import com.listenBook.greendao.FeedbackDao;
import com.listenBook.greendao.FreshPushDao;
import com.listenBook.greendao.LessonInfoDao;
import com.listenBook.greendao.LessonMarksInfoDao;
import com.listenBook.greendao.MediaDao;
import com.listenBook.greendao.RankDao;
import com.listenBook.greendao.ReadingTrackDao;
import com.listenBook.greendao.RecommendDao;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
	public MySQLiteOpenHelper(Context context, String name,
			SQLiteDatabase.CursorFactory factory) {
		super(context, name, factory);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		SharedPreferenceUtil.clear();// 清除SharedPreference
	}
	// MediaDao.class ArticleChapterInfoDao.class, RecommendDao.class,
	// RankDao.class,AdsDao.class, DownDao.class, CollectDao.class,
	// ReadingTrackDao.class, FeedbackDao.class,
	// ChipFeedbackDao.class,
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MigrationHelper.migrate(db, BaseuserDao.class, ArticleDao.class,
				CategoryDao.class, ArticleFeedbackDao.class,
				ChatMsgDao.class, FreshPushDao.class,
				ChatPeopleInfoDao.class, ReadingTrackDao.class,
				LessonInfoDao.class, LessonMarksInfoDao.class,ArticleChapterRecodeDao.class);

	}
}
