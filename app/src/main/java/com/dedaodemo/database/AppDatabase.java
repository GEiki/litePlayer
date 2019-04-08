package com.dedaodemo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.database.dao.ItemDao;
import com.dedaodemo.database.dao.ItemSongListDao;
import com.dedaodemo.database.dao.SongListDao;
import com.dedaodemo.entity.ItemSongList;

/**
 * Created by 01377578 on 2018/7/27.
 */

@Database(entities = {Item.class, SongList.class, ItemSongList.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();

    public abstract SongListDao songListDao();

    public abstract ItemSongListDao itemSongListDao();


}
