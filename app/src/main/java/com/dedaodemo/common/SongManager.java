package com.dedaodemo.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/6/28.
 */

public class SongManager {

    public interface IProgressCallback {
        public void onResponse(int position, long duration);
    }

    private static SongManager instance;
    private static ArrayList<SongList> sheetList = new ArrayList<>();
    private static SongList currentSongList;
    private static Item currentSong;
    private static IProgressCallback callback;
    private static String playMode = Constant.ORDER;
    private static boolean isPlaying = false;
    private Context context = MyApplication.getMyApplicationContext();

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

        for (SongList songList : sheetList) {
            if (songList.getTitle().equals(songListName)) {
                currentSongList = songList;
                break;
            }
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(Constant.CURRENT_SONGLIST, currentSongList.getTitle())
                .putString(Constant.CURRENT_SONG, currentSong.getTitle())
                .commit();
    }

    public void play(SongList songList, Item item) {
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

    public void rePlay() {
        isPlaying = true;
        Bundle bundle = new Bundle();
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_RE_PLAY);
    }

    public boolean next() {
        ArrayList<Item> items = currentSongList.getSongList();
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
}
