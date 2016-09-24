package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.Feedback;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table FEEDBACK.
*/
public class FeedbackDao extends AbstractDao<Feedback, Long> {

    public static final String TABLENAME = "FEEDBACK";

    /**
     * Properties of entity Feedback.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Fid = new Property(0, long.class, "fid", true, "FID");
        public final static Property Dateline = new Property(1, String.class, "dateline", false, "DATELINE");
        public final static Property Msg = new Property(2, String.class, "msg", false, "MSG");
    };


    public FeedbackDao(DaoConfig config) {
        super(config);
    }
    
    public FeedbackDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FEEDBACK' (" + //
                "'FID' INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: fid
                "'DATELINE' TEXT," + // 1: dateline
                "'MSG' TEXT);"); // 2: msg
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FEEDBACK'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Feedback entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getFid());
 
        String dateline = entity.getDateline();
        if (dateline != null) {
            stmt.bindString(2, dateline);
        }
 
        String msg = entity.getMsg();
        if (msg != null) {
            stmt.bindString(3, msg);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Feedback readEntity(Cursor cursor, int offset) {
        Feedback entity = new Feedback( //
            cursor.getLong(offset + 0), // fid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // dateline
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // msg
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Feedback entity, int offset) {
        entity.setFid(cursor.getLong(offset + 0));
        entity.setDateline(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMsg(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Feedback entity, long rowId) {
        entity.setFid(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Feedback entity) {
        if(entity != null) {
            return entity.getFid();
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