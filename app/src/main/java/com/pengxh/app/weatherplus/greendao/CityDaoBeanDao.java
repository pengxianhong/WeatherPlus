package com.pengxh.app.weatherplus.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.pengxh.app.weatherplus.bean.CityDaoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CITY_DAO_BEAN".
*/
public class CityDaoBeanDao extends AbstractDao<CityDaoBean, Long> {

    public static final String TABLENAME = "CITY_DAO_BEAN";

    /**
     * Properties of entity CityDaoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Cityid = new Property(1, String.class, "cityid", false, "CITYID");
        public final static Property Parentid = new Property(2, String.class, "parentid", false, "PARENTID");
        public final static Property Citycode = new Property(3, String.class, "citycode", false, "CITYCODE");
        public final static Property City = new Property(4, String.class, "city", false, "CITY");
    }


    public CityDaoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public CityDaoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CITY_DAO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CITYID\" TEXT UNIQUE ," + // 1: cityid
                "\"PARENTID\" TEXT UNIQUE ," + // 2: parentid
                "\"CITYCODE\" TEXT UNIQUE ," + // 3: citycode
                "\"CITY\" TEXT UNIQUE );"); // 4: city
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CITY_DAO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CityDaoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cityid = entity.getCityid();
        if (cityid != null) {
            stmt.bindString(2, cityid);
        }
 
        String parentid = entity.getParentid();
        if (parentid != null) {
            stmt.bindString(3, parentid);
        }
 
        String citycode = entity.getCitycode();
        if (citycode != null) {
            stmt.bindString(4, citycode);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(5, city);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CityDaoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cityid = entity.getCityid();
        if (cityid != null) {
            stmt.bindString(2, cityid);
        }
 
        String parentid = entity.getParentid();
        if (parentid != null) {
            stmt.bindString(3, parentid);
        }
 
        String citycode = entity.getCitycode();
        if (citycode != null) {
            stmt.bindString(4, citycode);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(5, city);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CityDaoBean readEntity(Cursor cursor, int offset) {
        CityDaoBean entity = new CityDaoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cityid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // parentid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // citycode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // city
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CityDaoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCityid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setParentid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCitycode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CityDaoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CityDaoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CityDaoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
