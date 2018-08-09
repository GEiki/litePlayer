package com.dedaodemo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dedaodemo.bean.SongList;

import java.util.List;

/**
 * Created by 01377578 on 2018/7/27.
 */

@Dao
public interface SongListDao {

    @Query("SELECT * FROM SongList")
    List<SongList> queryAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SongList... songLists);

    @Delete
    void delete(SongList songList);

}
