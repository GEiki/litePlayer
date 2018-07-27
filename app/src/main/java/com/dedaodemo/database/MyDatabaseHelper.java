package com.dedaodemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dedaodemo.common.Constant;
import com.dedaodemo.util.Util;

/**
 * Created by guoss on 2018/3/17.
 */

@Deprecated
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static String TAG="Database";
    public static final String SONG_DATABASE_NAME = "misc_db";
    public static final int VERSION = 1;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table misc_info(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,PRIMARY KEY(title))";
        String sql2="create table song_lists(id int,title varchar(20),time varchar(20),size int,PRIMARY KEY(id))";
        String sql3 = "create table if not exists " + Util.getTableName(Constant.ALL_SONG_TABLE_NAME) + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,pic varchar(50),lrc varchar(50),PRIMARY KEY(id))";
        String sql4 = "create table if not exists " + Util.getTableName(Constant.STATE_TABLE_NAME) + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,pic varchar(50),lrc varchar(50),PRIMARY KEY(id))";
        Log.i(TAG,"Create table-------------->");
        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
