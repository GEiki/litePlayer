package com.dedaodemo.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.dedaodemo.MyApplication;
import com.dedaodemo.MyDatabaseHelper;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/6/28.
 */

public class SongManager {

    public interface OnPlayListener {
        public void onPlay();
    }

    public interface IProgressCallback {
        public void onResponse(int position, long duration);
    }

    private static SongManager instance;
    private static ArrayList<SongList> sheetList = new ArrayList<>();
    private static SongList currentSongList = new SongList();
    private static Item currentSong = new Item();
    private static IProgressCallback callback;
    private static String playMode = Constant.ORDER;
    private static boolean isPlaying = false;
    private Context context = MyApplication.getMyApplicationContext();
    private OnPlayListener onPlayListener;

    private SongManager() {
    }

    public static SongManager getInstance() {
        if (instance == null) {
            synchronized (SongManager.class) {
                if (instance == null) {
                    instance = new SongManager();
                }
            }
        }
        return instance;
    }

    public void init() {
        setPlayStateFromLocal();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONGLIST, currentSongList);
        bundle.putSerializable(Constant.CURRENT_SONG, currentSong);
        if (currentSongList.getSize() == 0) {
            return;
        }
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_INIT);
    }

    public void setSheetList(ArrayList<SongList> list) {
        sheetList = list;
    }

    public ArrayList<SongList> getSheetList() {
        return sheetList;
    }

    private void setPlayStateFromLocal() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE);
        String songListName = sharedPreferences.getString(Constant.CURRENT_SONGLIST, "全部歌曲");
        String songName = sharedPreferences.getString(Constant.CURRENT_SONG, context.getResources().getString(R.string.default_song_name));

        if (songListName.equals(Constant.SEARCH_SONG_LIST)) {
            getSearchSongListFromLocal();
            for (Item item : currentSongList.getSongList()) {
                if (item.getTitle().equals(songName)) {
                    currentSong = item;
                    break;
                }
            }
            return;
        }

        for (SongList songList : sheetList) {
            if (songList.getTitle().equals(songListName)) {
                currentSongList = songList;
                break;
            }
        }
        if (currentSongList == null) {
            currentSongList = new SongList();
            currentSongList.setTitle("全部歌曲");
        }

        Item defaultItem = new Item();
        defaultItem.setTitle("没有歌曲");
        currentSong = defaultItem;
        if (currentSongList.getSongList().size() != 0) {
            for (Item item : currentSongList.getSongList()) {
                if (item.getTitle().equals(songName)) {
                    currentSong = item;
                    break;
                }
            }
        }

        if (currentSong == null)
            currentSong = defaultItem;
    }

    public void savePlayState() {
        if (currentSongList.getTitle().equals(Constant.SEARCH_SONG_LIST)) {
            saveSearchListSongList(currentSongList);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.CURRENT_SONGLIST, currentSongList.getTitle())
                .putString(Constant.CURRENT_SONG, currentSong.getTitle())
                .commit();
    }

    public void onPlay() {
        if (onPlayListener != null) {
            onPlayListener.onPlay();
        }
    }

    public void play(SongList songList, Item item, OnPlayListener playListener) {
        this.onPlayListener = playListener;
        play(songList, item);
    }

    public void play(SongList songList, Item item) {
        if (currentSongList.getSize() == 0) {
            return;
        }
        isPlaying = true;
        currentSongList = songList;
        currentSong = item;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONGLIST, currentSongList);
        bundle.putSerializable(Constant.CURRENT_SONG, currentSong);
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_PLAY);
    }

    public void pause() {
        isPlaying = false;
        Bundle bundle = new Bundle();
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_PAUSE);
    }

    public void rePlay(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
        isPlaying = true;
        Bundle bundle = new Bundle();
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_RE_PLAY);
    }

    public boolean next() {
        ArrayList<Item> items = currentSongList.getSongList();
        if (items.size() == 0) {
            return false;
        }
        int index = items.indexOf(currentSong);
        if (index == items.size() - 1)
            return false;
        currentSong = items.get(index + 1);
        play(currentSongList, currentSong);
        return true;
    }

    /**
     * 根据播放模式切换下一首歌曲
     */
    public void nextAccordingToMode() {
        if (playMode == Constant.ORDER) {
            next();
        } else if (playMode == Constant.RANDOM) {
            int index = (int) (Math.random() * (currentSongList.getSize() - 1));
            currentSong = currentSongList.getSongList().get(index);
            play(currentSongList, currentSong);
        } else if (playMode == Constant.LIST_RECYCLE) {
            if (!next()) {
                currentSong = currentSongList.getSongList().get(0);
                play(currentSongList, currentSong);
            }
        } else if (playMode == Constant.SINGLE_RECYCLE) {
            play(currentSongList, currentSong);
        }
    }

    public boolean pre() {
        ArrayList<Item> items = currentSongList.getSongList();
        if (items.size() == 0)
            return false;
        int index = items.indexOf(currentSong);
        if (index == 0)
            return false;
        currentSong = items.get(index - 1);
        play(currentSongList, currentSong);
        return true;
    }

    public void changePlayMode(String mode) {
        playMode = mode;
    }

    public String getPlayMode() {
        return playMode;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public SongList getCurrentSongList() {
        return currentSongList;
    }

    public Item getCurrentSong() {
        return currentSong;
    }


    public void requestProgress(IProgressCallback callback) {
        this.callback = callback;
        Bundle bundle = new Bundle();
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_REQUEST_DURATION);
    }

    public void updateProgress(int position, long duration) {
        callback.onResponse(position, duration);
    }

    public void seekTo(int progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, progress);
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_SEEK_TO);
    }

    /**
     * 用于存储当前播放的搜索列表
     */
    public void saveSearchListSongList(SongList songList) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        String sql = "drop table " + songList.getTableName();
        db.execSQL(sql);
        String string = "create table if not exists " + songList.getTableName() + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,PRIMARY KEY(title))";
        db.execSQL(string);
        ArrayList<Item> items = songList.getSongList();
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
        db.close();
    }

    /**
     * 获取上次播放的搜索列表
     */

    public void getSearchSongListFromLocal() {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        SongList songList = new SongList();
        songList.setTitle(Constant.SEARCH_SONG_LIST);
        Cursor cur = db.query(Constant.SEARCH_SONG_LIST, null, null, null, null, null, "id", null);
        while (cur.moveToNext()) {
            Item a = new Item();
            a.setTitle(cur.getString(1));//title
            a.setAuthor(cur.getString(2));//Author
            a.setTime(cur.getString(3));//time
            a.setPath(cur.getString(4));//path
            a.setSize(String.valueOf(cur.getInt(5)));//size
            a.setType(cur.getInt(6));//type
            songList.addSong(a);
        }
        cur.close();
        currentSongList = songList;
    }
}
