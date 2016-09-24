package com.listenBook.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.listenBook.greendao.MeritTableChildren;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MERIT_TABLE_CHILDREN.
*/
public class MeritTableChildrenDao extends AbstractDao<MeritTableChildren, Long> {

    public static final String TABLENAME = "MERIT_TABLE_CHILDREN";

    /**
     * Properties of entity MeritTableChildren.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uid = new Property(1, long.class, "uid", false, "UID");
        public final static Property Date = new Property(2, String.class, "date", false, "DATE");
        public final static Property Schedules = new Property(3, Integer.class, "schedules", false, "SCHEDULES");
        public final static Property Attitude = new Property(4, Integer.class, "attitude", false, "ATTITUDE");
        public final static Property Study = new Property(5, Integer.class, "study", false, "STUDY");
        public final static Property Love = new Property(6, Integer.class, "love", false, "LOVE");
        public final static Property Respect = new Property(7, Integer.class, "respect", false, "RESPECT");
        public final static Property Action = new Property(8, Integer.class, "action", false, "ACTION");
        public final static Property Other = new Property(9, Integer.class, "other", false, "OTHER");
        public final static Property Gong = new Property(10, Integer.class, "gong", false, "GONG");
        public final static Property Guo = new Property(11, Integer.class, "guo", false, "GUO");
        public final static Property Schedulesdes = new Property(12, String.class, "schedulesdes", false, "SCHEDULESDES");
        public final static Property Schedulesexc = new Property(13, String.class, "schedulesexc", false, "SCHEDULESEXC");
        public final static Property Attitudedes = new Property(14, String.class, "attitudedes", false, "ATTITUDEDES");
        public final static Property Attitudeexc = new Property(15, String.class, "attitudeexc", false, "ATTITUDEEXC");
        public final static Property Studydes = new Property(16, String.class, "studydes", false, "STUDYDES");
        public final static Property Studyexc = new Property(17, String.class, "studyexc", false, "STUDYEXC");
        public final static Property Lovedes = new Property(18, String.class, "lovedes", false, "LOVEDES");
        public final static Property Loveexc = new Property(19, String.class, "loveexc", false, "LOVEEXC");
        public final static Property Respectdes = new Property(20, String.class, "respectdes", false, "RESPECTDES");
        public final static Property Respectexc = new Property(21, String.class, "respectexc", false, "RESPECTEXC");
        public final static Property Actiondes = new Property(22, String.class, "actiondes", false, "ACTIONDES");
        public final static Property Actionexc = new Property(23, String.class, "actionexc", false, "ACTIONEXC");
        public final static Property Otherdes = new Property(24, String.class, "otherdes", false, "OTHERDES");
        public final static Property Otherexc = new Property(25, String.class, "otherexc", false, "OTHEREXC");
    };


    public MeritTableChildrenDao(DaoConfig config) {
        super(config);
    }
    
    public MeritTableChildrenDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MERIT_TABLE_CHILDREN' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'UID' INTEGER NOT NULL ," + // 1: uid
                "'DATE' TEXT NOT NULL ," + // 2: date
                "'SCHEDULES' INTEGER," + // 3: schedules
                "'ATTITUDE' INTEGER," + // 4: attitude
                "'STUDY' INTEGER," + // 5: study
                "'LOVE' INTEGER," + // 6: love
                "'RESPECT' INTEGER," + // 7: respect
                "'ACTION' INTEGER," + // 8: action
                "'OTHER' INTEGER," + // 9: other
                "'GONG' INTEGER," + // 10: gong
                "'GUO' INTEGER," + // 11: guo
                "'SCHEDULESDES' TEXT," + // 12: schedulesdes
                "'SCHEDULESEXC' TEXT," + // 13: schedulesexc
                "'ATTITUDEDES' TEXT," + // 14: attitudedes
                "'ATTITUDEEXC' TEXT," + // 15: attitudeexc
                "'STUDYDES' TEXT," + // 16: studydes
                "'STUDYEXC' TEXT," + // 17: studyexc
                "'LOVEDES' TEXT," + // 18: lovedes
                "'LOVEEXC' TEXT," + // 19: loveexc
                "'RESPECTDES' TEXT," + // 20: respectdes
                "'RESPECTEXC' TEXT," + // 21: respectexc
                "'ACTIONDES' TEXT," + // 22: actiondes
                "'ACTIONEXC' TEXT," + // 23: actionexc
                "'OTHERDES' TEXT," + // 24: otherdes
                "'OTHEREXC' TEXT);"); // 25: otherexc
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MERIT_TABLE_CHILDREN'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MeritTableChildren entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUid());
        stmt.bindString(3, entity.getDate());
 
        Integer schedules = entity.getSchedules();
        if (schedules != null) {
            stmt.bindLong(4, schedules);
        }
 
        Integer attitude = entity.getAttitude();
        if (attitude != null) {
            stmt.bindLong(5, attitude);
        }
 
        Integer study = entity.getStudy();
        if (study != null) {
            stmt.bindLong(6, study);
        }
 
        Integer love = entity.getLove();
        if (love != null) {
            stmt.bindLong(7, love);
        }
 
        Integer respect = entity.getRespect();
        if (respect != null) {
            stmt.bindLong(8, respect);
        }
 
        Integer action = entity.getAction();
        if (action != null) {
            stmt.bindLong(9, action);
        }
 
        Integer other = entity.getOther();
        if (other != null) {
            stmt.bindLong(10, other);
        }
 
        Integer gong = entity.getGong();
        if (gong != null) {
            stmt.bindLong(11, gong);
        }
 
        Integer guo = entity.getGuo();
        if (guo != null) {
            stmt.bindLong(12, guo);
        }
 
        String schedulesdes = entity.getSchedulesdes();
        if (schedulesdes != null) {
            stmt.bindString(13, schedulesdes);
        }
 
        String schedulesexc = entity.getSchedulesexc();
        if (schedulesexc != null) {
            stmt.bindString(14, schedulesexc);
        }
 
        String attitudedes = entity.getAttitudedes();
        if (attitudedes != null) {
            stmt.bindString(15, attitudedes);
        }
 
        String attitudeexc = entity.getAttitudeexc();
        if (attitudeexc != null) {
            stmt.bindString(16, attitudeexc);
        }
 
        String studydes = entity.getStudydes();
        if (studydes != null) {
            stmt.bindString(17, studydes);
        }
 
        String studyexc = entity.getStudyexc();
        if (studyexc != null) {
            stmt.bindString(18, studyexc);
        }
 
        String lovedes = entity.getLovedes();
        if (lovedes != null) {
            stmt.bindString(19, lovedes);
        }
 
        String loveexc = entity.getLoveexc();
        if (loveexc != null) {
            stmt.bindString(20, loveexc);
        }
 
        String respectdes = entity.getRespectdes();
        if (respectdes != null) {
            stmt.bindString(21, respectdes);
        }
 
        String respectexc = entity.getRespectexc();
        if (respectexc != null) {
            stmt.bindString(22, respectexc);
        }
 
        String actiondes = entity.getActiondes();
        if (actiondes != null) {
            stmt.bindString(23, actiondes);
        }
 
        String actionexc = entity.getActionexc();
        if (actionexc != null) {
            stmt.bindString(24, actionexc);
        }
 
        String otherdes = entity.getOtherdes();
        if (otherdes != null) {
            stmt.bindString(25, otherdes);
        }
 
        String otherexc = entity.getOtherexc();
        if (otherexc != null) {
            stmt.bindString(26, otherexc);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MeritTableChildren readEntity(Cursor cursor, int offset) {
        MeritTableChildren entity = new MeritTableChildren( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // uid
            cursor.getString(offset + 2), // date
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // schedules
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // attitude
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // study
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // love
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // respect
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // action
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // other
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // gong
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // guo
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // schedulesdes
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // schedulesexc
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // attitudedes
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // attitudeexc
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // studydes
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // studyexc
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // lovedes
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // loveexc
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // respectdes
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // respectexc
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // actiondes
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // actionexc
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // otherdes
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25) // otherexc
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MeritTableChildren entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUid(cursor.getLong(offset + 1));
        entity.setDate(cursor.getString(offset + 2));
        entity.setSchedules(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setAttitude(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStudy(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setLove(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setRespect(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setAction(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setOther(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setGong(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setGuo(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setSchedulesdes(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSchedulesexc(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setAttitudedes(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setAttitudeexc(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setStudydes(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setStudyexc(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setLovedes(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setLoveexc(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setRespectdes(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setRespectexc(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setActiondes(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setActionexc(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setOtherdes(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setOtherexc(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MeritTableChildren entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MeritTableChildren entity) {
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