package com.dedaodemo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dedaodemo.bean.Item;

import java.util.List;

/**
 * Created by 01377578 on 2018/7/27.
 */

@Dao
public interface ItemDao {

    @Query("SELECT * FROM Item")
    List<Item> queryAll();

    @Query("SELECT * FROM item WHERE title = song_name AND author = author_name")
    Item queryByName(String song_name, String author_name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Item... items);

    @Delete
    void delete(Item item);


}
