package com.dedaodemo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.database.MyDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Guoss on 2018/6/27.
 */
@Deprecated
public class SongModel implements SheetListContract.Model, SongListContract.Model {
    private MyDatabaseHelper databaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);

    private SheetListContract.ViewModel sheetListViewModel;
    private SongListContract.ViewModel songListViewModel;

    public SongModel(SheetListContract.ViewModel viewModel) {
        sheetListViewModel = viewModel;
    }

    public SongModel(SongListContract.ViewModel viewModel) {
        songListViewModel = viewModel;
    }


    @Override
    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取歌单列表
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                Cursor cursor = db.query("song_lists", null, null, null, null, null, "id", null);
                ArrayList<SongList> sheetList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    SongList songList = new SongList();
                    songList.setTitle(cursor.getString(1));
                    songList.setCreateDate(cursor.getString(2));
                    songList.setSize(cursor.getInt(3));
                    sheetList.add(songList);
                }
                cursor.close();
                //根据歌单获取歌曲
                for (SongList songList : sheetList) {
                    Cursor cur = db.query(songList.getTableName(), null, null, null, null, null, "id", null);
                    while (cur.moveToNext()) {
                        Item a = new Item();
                        a.setTitle(cur.getString(1));//title
                        a.setAuthor(cur.getString(2));//Author
                        a.setTime(cur.getString(3));//time
                        a.setPath(cur.getString(4));//path
                        a.setSize(cur.getInt(5));//size
                        a.setType(cur.getInt(6));//type
                        songList.addSong(a);
                    }
                    cur.close();
                }

                db.close();
                sheetListViewModel.loadDataSuccess(sheetList);
            }
        }).start();
    }


    /**
     * 向歌单中添加歌曲操作
     */
    @Override
    public void addSongToSongList(final SongList songList, final ArrayList<Item> items) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                for (Item item : items) {
                    if (!songList.containItem(item)) {
                        songList.addSong(item);
                        ContentValues cv = new ContentValues();
                        cv.put("id", item.getId());
                        cv.put("title", item.getTitle());
                        cv.put("author", item.getAuthor());
                        cv.put("time", item.getTime());
                        cv.put("path", item.getPath());
                        cv.put("size", item.getSize());
                        cv.put("type", item.getType());
                        db.insertOrThrow(songList.getTableName(), null, cv);
                    }

                }
                songListViewModel.onAddSongSuccess(songList);
                db.close();
            }
        }).start();

    }

    /**
     * 扫描歌曲后将歌曲添加到数据库
     */
    @Override
    public void addSongs(final SongList songList, final ArrayList<Item> items) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                for (Item item : items) {
                    if (!songList.containItem(item)) {
                        songList.addSong(item);
                        ContentValues cv = new ContentValues();
                        cv.put("id", item.getId());
                        cv.put("title", item.getTitle());
                        cv.put("author", item.getAuthor());
                        cv.put("time", item.getTime());
                        cv.put("path", item.getPath());
                        cv.put("size", item.getSize());
                        cv.put("type", item.getType());
                        db.insertOrThrow(songList.getTableName(), null, cv);
                    }
                }
                loadData();
                db.close();
            }
        }).start();
    }

    /**
     * 移除歌曲操作
     */
    @Override
    public void removeSongFromSongList(final SongList songList, final ArrayList<Item> items) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                for (Item item : items) {
                    boolean isExist = songList.removeSong(item);//确保删除的歌曲存在
                    if (isExist) {
                        String[] args = {item.getTitle()};
                        db.delete(songList.getTableName(), "title=?", args);
                    }
                }
                songListViewModel.onRemoveSongSuccess(songList);
                db.close();
            }
        }).start();


    }

    /**
     * 移除歌单操作
     */
    @Override
    public void removeSongList(SongList songList) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] args = {songList.getTitle()};
            db.delete("song_lists", "title=?", args);
            String sql = "drop table " + songList.getTableName();
            db.execSQL(sql);
            db.close();
            SongManager.getInstance().getSheetList().remove(songList);
            sheetListViewModel.removeSongListSuccess(SongManager.getInstance().getSheetList());
        } catch (Exception e) {
            sheetListViewModel.removeSongListFail("data error");
            e.printStackTrace();
        }

    }

    /**
     * 创建歌单操作
     */
    @Override
    public void createSongList(SongList songList, int size) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String string = "create table if not exists " + songList.getTableName() + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,PRIMARY KEY(title))";
            db.execSQL(string);
            ContentValues cv = new ContentValues();
            cv.put("id", size + 1);
            cv.put("title", songList.getTitle());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年mm月dd日");
            Date date = new Date(System.currentTimeMillis());
            songList.setCreateDate(simpleDateFormat.format(date).toString());
            cv.put("time", simpleDateFormat.format(date).toString());
            cv.put("size", 0);
            db.insert("song_lists", null, cv);
            db.close();
            SongManager.getInstance().getSheetList().add(songList);
            sheetListViewModel.createSongListSuccess(SongManager.getInstance().getSheetList());
        } catch (Exception e) {
            sheetListViewModel.createSongListFail("database error");
            e.printStackTrace();
        }

    }
}
