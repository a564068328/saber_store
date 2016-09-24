package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.Ads;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ADS.
*/
public class AdsDao extends AbstractDao<Ads, Long> {

    public static final String TABLENAME = "ADS";

    /**
     * Properties of entity Ads.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Aid = new Property(0, long.class, "aid", true, "AID");
        public final static Property Icon = new Property(1, String.class, "icon", false, "ICON");
        public final static Property Type = new Property(2, int.class, "type", false, "TYPE");
        public final static Property Url = new Property(3, String.class, "url", false, "URL");
        public final static Property Media = new Property(4, Long.class, "media", false, "MEDIA");
        public final static Property Category = new Property(5, Integer.class, "category", false, "CATEGORY");
        public final static Property Article = new Property(6, Integer.class, "article", false, "ARTICLE");
    };


    public AdsDao(DaoConfig config) {
        super(config);
    }
    
    public AdsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ADS' (" + //
                "'AID' INTEGER PRIMARY KEY NOT NULL UNIQUE ," + // 0: aid
                "'ICON' TEXT NOT NULL ," + // 1: icon
                "'TYPE' INTEGER NOT NULL ," + // 2: type
                "'URL' TEXT," + // 3: url
                "'MEDIA' INTEGER," + // 4: media
                "'CATEGORY' INTEGER," + // 5: category
                "'ARTICLE' INTEGER);"); // 6: article
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ADS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Ads entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getAid());
        stmt.bindString(2, entity.getIcon());
        stmt.bindLong(3, entity.getType());
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(4, url);
        }
 
        Long media = entity.getMedia();
        if (media != null) {
            stmt.bindLong(5, media);
        }
 
        Integer category = entity.getCategory();
        if (category != null) {
            stmt.bindLong(6, category);
        }
 
        Integer article = entity.getArticle();
        if (article != null) {
            stmt.bindLong(7, article);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Ads readEntity(Cursor cursor, int offset) {
        Ads entity = new Ads( //
            cursor.getLong(offset + 0), // aid
            cursor.getString(offset + 1), // icon
            cursor.getInt(offset + 2), // type
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // url
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // media
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // category
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // article
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Ads entity, int offset) {
        entity.setAid(cursor.getLong(offset + 0));
        entity.setIcon(cursor.getString(offset + 1));
        entity.setType(cursor.getInt(offset + 2));
        entity.setUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMedia(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setCategory(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setArticle(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Ads entity, long rowId) {
        entity.setAid(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Ads entity) {
        if(entity != null) {
            return entity.getAid();
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
