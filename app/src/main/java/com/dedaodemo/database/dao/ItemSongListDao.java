package com.dedaodemo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dedaodemo.entity.ItemSongList;

import java.util.List;

/**
 * Created by 01377578 on 2018/7/27.
 */

@Dao
public interface ItemSongListDao {

    @Query("SELECT * FROM ItemSongList WHERE sheet_name = :sheet")
    List<ItemSongList> queryBySheetName(String sheet);

    @Query("SELECT * FROM ItemSongList")
    List<ItemSongList> queryAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ItemSongList... itemSongLists);

    @Delete
    void delete(ItemSongList itemSongList);

    @Update
    void update(ItemSongList...itemSongLists);
}
