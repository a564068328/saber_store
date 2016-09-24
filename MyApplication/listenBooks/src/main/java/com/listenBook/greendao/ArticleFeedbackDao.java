package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.ArticleFeedback;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ARTICLE_FEEDBACK.
*/
public class ArticleFeedbackDao extends AbstractDao<ArticleFeedback, Long> {

    public static final String TABLENAME = "ARTICLE_FEEDBACK";

    /**
     * Properties of entity ArticleFeedback.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "ID");
        public final static Property Aid = new Property(1, long.class, "aid", false, "AID");
        public final static Property UserId = new Property(2, long.class, "userId", false, "USER_ID");
        public final static Property Icon = new Property(3, String.class, "icon", false, "ICON");
        public final static Property Nick = new Property(4, String.class, "nick", false, "NICK");
        public final static Property Account = new Property(5, String.class, "account", false, "ACCOUNT");
        public final static Property Msg = new Property(6, String.class, "msg", false, "MSG");
        public final static Property Stars = new Property(7, int.class, "stars", false, "STARS");
        public final static Property Dateline = new Property(8, String.class, "dateline", false, "DATELINE");
    };


    public ArticleFeedbackDao(DaoConfig config) {
        super(config);
    }
    
    public ArticleFeedbackDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ARTICLE_FEEDBACK' (" + //
                "'ID' INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: id
                "'AID' INTEGER NOT NULL ," + // 1: aid
                "'USER_ID' INTEGER NOT NULL ," + // 2: userId
                "'ICON' TEXT NOT NULL ," + // 3: icon
                "'NICK' TEXT NOT NULL ," + // 4: nick
                "'ACCOUNT' TEXT NOT NULL ," + // 5: account
                "'MSG' TEXT NOT NULL ," + // 6: msg
                "'STARS' INTEGER NOT NULL ," + // 7: stars
                "'DATELINE' TEXT NOT NULL );"); // 8: dateline
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ARTICLE_FEEDBACK'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ArticleFeedback entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getAid());
        stmt.bindLong(3, entity.getUserId());
        stmt.bindString(4, entity.getIcon());
        stmt.bindString(5, entity.getNick());
        stmt.bindString(6, entity.getAccount());
        stmt.bindString(7, entity.getMsg());
        stmt.bindLong(8, entity.getStars());
        stmt.bindString(9, entity.getDateline());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ArticleFeedback readEntity(Cursor cursor, int offset) {
        ArticleFeedback entity = new ArticleFeedback( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // aid
            cursor.getLong(offset + 2), // userId
            cursor.getString(offset + 3), // icon
            cursor.getString(offset + 4), // nick
            cursor.getString(offset + 5), // account
            cursor.getString(offset + 6), // msg
            cursor.getInt(offset + 7), // stars
            cursor.getString(offset + 8) // dateline
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ArticleFeedback entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setAid(cursor.getLong(offset + 1));
        entity.setUserId(cursor.getLong(offset + 2));
        entity.setIcon(cursor.getString(offset + 3));
        entity.setNick(cursor.getString(offset + 4));
        entity.setAccount(cursor.getString(offset + 5));
        entity.setMsg(cursor.getString(offset + 6));
        entity.setStars(cursor.getInt(offset + 7));
        entity.setDateline(cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ArticleFeedback entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ArticleFeedback entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
