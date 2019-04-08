package com.dedaodemo.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.dedaodemo.MyApplication;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class AppDatabaseHelper {
    private static AppDatabase appDatabase;
    private static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE item ADD COLUMN album VARCHAR");
        }
    };

    public AppDatabase getDatabase() {
        if (appDatabase == null) {
            synchronized (this) {
                if (appDatabase == null) {
                    appDatabase = Room
                            .databaseBuilder(MyApplication.getMyApplicationContext(), AppDatabase.class, "app_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return appDatabase;
    }
}
