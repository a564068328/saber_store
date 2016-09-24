package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.Collect;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COLLECT.
*/
public class CollectDao extends AbstractDao<Collect, String> {

    public static final String TABLENAME = "COLLECT";

    /**
     * Properties of entity Collect.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Cid = new Property(0, String.class, "cid", true, "CID");
        public final static Property Aid = new Property(1, long.class, "aid", false, "AID");
        public final static Property Uid = new Property(2, long.class, "uid", false, "UID");
    };


    public CollectDao(DaoConfig config) {
        super(config);
    }
    
    public CollectDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COLLECT' (" + //
                "'CID' TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: cid
                "'AID' INTEGER NOT NULL ," + // 1: aid
                "'UID' INTEGER NOT NULL );"); // 2: uid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COLLECT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Collect entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getCid());
        stmt.bindLong(2, entity.getAid());
        stmt.bindLong(3, entity.getUid());
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Collect readEntity(Cursor cursor, int offset) {
        Collect entity = new Collect( //
            cursor.getString(offset + 0), // cid
            cursor.getLong(offset + 1), // aid
            cursor.getLong(offset + 2) // uid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Collect entity, int offset) {
        entity.setCid(cursor.getString(offset + 0));
        entity.setAid(cursor.getLong(offset + 1));
        entity.setUid(cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Collect entity, long rowId) {
        return entity.getCid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Collect entity) {
        if(entity != null) {
            return entity.getCid();
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