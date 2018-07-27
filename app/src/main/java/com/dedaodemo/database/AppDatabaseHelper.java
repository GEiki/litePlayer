package com.dedaodemo.database;

import android.arch.persistence.room.Room;

import com.dedaodemo.MyApplication;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class AppDatabaseHelper {
    private static AppDatabase appDatabase;

    public AppDatabase getDatabase() {
        if (appDatabase == null) {
            synchronized (this) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(MyApplication.getMyApplicationContext(), AppDatabase.class, "app_database").build();
                }
            }
        }
        return appDatabase;
    }
}
