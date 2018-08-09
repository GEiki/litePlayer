package com.dedaodemo.util;

import android.util.Log;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.database.AppDatabase;
import com.dedaodemo.database.AppDatabaseHelper;
import com.dedaodemo.database.dao.ItemDao;
import com.dedaodemo.database.dao.ItemSongListDao;
import com.dedaodemo.database.dao.SongListDao;
import com.dedaodemo.entity.ItemSongList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";

    public static void deleteSongFromSongList(SongList songList, Item item) {
        AppDatabaseHelper helper = new AppDatabaseHelper();
        AppDatabase db = helper.getDatabase();

        ItemSongList itemSongList = new ItemSongList();
        itemSongList.setAuthor(item.getAuthor());
        itemSongList.setSheet_name(songList.getTitle());
        itemSongList.setSong_name(item.getTitle());

        ItemSongListDao dao = db.itemSongListDao();
        dao.delete(itemSongList);
    }

    public static void insertSongToSongList(SongList songList, Item item) {
        AppDatabaseHelper helper = new AppDatabaseHelper();
        AppDatabase db = helper.getDatabase();

        ItemSongList itemSongList = new ItemSongList();
        itemSongList.setAuthor(item.getAuthor());
        itemSongList.setSheet_name(songList.getTitle());
        itemSongList.setSong_name(item.getTitle());

        ItemSongListDao dao = db.itemSongListDao();
        dao.insertAll(itemSongList);
        ItemDao itemDao = db.itemDao();
        itemDao.insertAll(item);
    }

    /**
     * 查询歌单中的歌曲
     */
    public static List<Item> queryBySheet(String sheetName) {
        AppDatabaseHelper helper = new AppDatabaseHelper();
        AppDatabase db = helper.getDatabase();

        List<ItemSongList> itemSongLists = new ArrayList<>();
        itemSongLists = db.itemSongListDao().queryBySheetName(sheetName);

        ItemDao itemDao = db.itemDao();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < itemSongLists.size(); i++) {
            String songName = itemSongLists.get(i).getSong_name();
            String author = itemSongLists.get(i).getAuthor();
            Item item = itemDao.queryByName(songName, author);
            items.add(item);
        }
        Log.i(TAG, "Query>>>>>>>>>>Size:" + String.valueOf(items.size()));
        return items;
    }

    public static List<Item> queryAll() {
        AppDatabaseHelper helper = new AppDatabaseHelper();
        AppDatabase db = helper.getDatabase();

        ItemDao itemDao = db.itemDao();
        List<Item> items = itemDao.queryAll();
        return items;
    }

    public static void deleteSongList(SongList songList) {
        AppDatabaseHelper helper = new AppDatabaseHelper();
        AppDatabase db = helper.getDatabase();

        SongListDao dao = db.songListDao();
        dao.delete(songList);
    }
}
