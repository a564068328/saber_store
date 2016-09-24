package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.Category;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CATEGORY.
*/
public class CategoryDao extends AbstractDao<Category, Long> {

    public static final String TABLENAME = "CATEGORY";

    /**
     * Properties of entity Category.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property CId = new Property(0, long.class, "cId", true, "C_ID");
        public final static Property CIcon = new Property(1, String.class, "cIcon", false, "C_ICON");
        public final static Property CName = new Property(2, String.class, "cName", false, "C_NAME");
        public final static Property CDesc = new Property(3, String.class, "cDesc", false, "C_DESC");
        public final static Property Media = new Property(4, long.class, "media", false, "MEDIA");
        public final static Property CSort = new Property(5, int.class, "cSort", false, "C_SORT");
    };


    public CategoryDao(DaoConfig config) {
        super(config);
    }
    
    public CategoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CATEGORY' (" + //
                "'C_ID' INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: cId
                "'C_ICON' TEXT NOT NULL ," + // 1: cIcon
                "'C_NAME' TEXT NOT NULL ," + // 2: cName
                "'C_DESC' TEXT NOT NULL ," + // 3: cDesc
                "'MEDIA' INTEGER NOT NULL ," + // 4: media
                "'C_SORT' INTEGER NOT NULL );"); // 5: cSort
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CATEGORY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Category entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCId());
        stmt.bindString(2, entity.getCIcon());
        stmt.bindString(3, entity.getCName());
        stmt.bindString(4, entity.getCDesc());
        stmt.bindLong(5, entity.getMedia());
        stmt.bindLong(6, entity.getCSort());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Category readEntity(Cursor cursor, int offset) {
        Category entity = new Category( //
            cursor.getLong(offset + 0), // cId
            cursor.getString(offset + 1), // cIcon
            cursor.getString(offset + 2), // cName
            cursor.getString(offset + 3), // cDesc
            cursor.getLong(offset + 4), // media
            cursor.getInt(offset + 5) // cSort
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Category entity, int offset) {
        entity.setCId(cursor.getLong(offset + 0));
        entity.setCIcon(cursor.getString(offset + 1));
        entity.setCName(cursor.getString(offset + 2));
        entity.setCDesc(cursor.getString(offset + 3));
        entity.setMedia(cursor.getLong(offset + 4));
        entity.setCSort(cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Category entity, long rowId) {
        entity.setCId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Category entity) {
        if(entity != null) {
            return entity.getCId();
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
