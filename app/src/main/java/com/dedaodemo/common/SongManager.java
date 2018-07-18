package com.dedaodemo.common;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.model.SearchModel;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/6/28.
 */

public class SongManager {

    public interface OnPlayListener {
        public void onPlay();

        public void onError();
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

    private MutableLiveData<SongList> curSongListLiveData = new MutableLiveData<>();
    private MutableLiveData<Item> curSongLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> playStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> playModeLiveData = new MutableLiveData<>();

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
                    curSongLiveData.postValue(currentSong);
                    break;
                }
            }
            return;
        }

        for (SongList songList : sheetList) {
            if (songList.getTitle().equals(songListName)) {
                currentSongList = songList;
                curSongListLiveData.postValue(currentSongList);
                break;
            }
        }
        if (currentSongList == null) {
            currentSongList = new SongList();
            currentSongList.setTitle("全部歌曲");
            curSongListLiveData.postValue(currentSongList);
        }

        Item defaultItem = new Item();
        defaultItem.setTitle("没有歌曲");
        currentSong = defaultItem;
        curSongLiveData.postValue(currentSong);
        if (currentSongList.getSongList().size() != 0) {
            for (Item item : currentSongList.getSongList()) {
                if (item.getTitle().equals(songName)) {
                    currentSong = item;
                    curSongLiveData.postValue(currentSong);
                    break;
                }
            }
        }

        if (currentSong == null) {
            currentSong = defaultItem;
            curSongLiveData.postValue(currentSong);
        }

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

    public void onError() {
        if (onPlayListener != null) {
            onPlayListener.onError();
        }
    }

    public void play(SongList songList, Item item, OnPlayListener playListener) {
        this.onPlayListener = playListener;
        play(songList, item);
    }

    public void play(SongList songList, Item item) {
        currentSongList = songList;
        currentSong = item;
        curSongLiveData.postValue(currentSong);
        curSongListLiveData.postValue(currentSongList);
        if (currentSongList.getSize() == 0) {
            return;
        }
        isPlaying = true;
        playStateLiveData.postValue(isPlaying());
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONGLIST, currentSongList);
        bundle.putSerializable(Constant.CURRENT_SONG, currentSong);
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_PLAY);
    }

    public void pause() {
        isPlaying = false;
        playStateLiveData.postValue(isPlaying());
        Bundle bundle = new Bundle();
        MusicServiceManager.getInstance().sendMessage(bundle, Constant.ACTION_PAUSE);
    }

    public void rePlay(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
        isPlaying = true;
        playStateLiveData.postValue(isPlaying());
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
        curSongLiveData.postValue(currentSong);
        onPlayListener = null;
        play(currentSongList, currentSong);
        return true;
    }

    /**
     * 根据播放模式切换下一首歌曲
     */
    public void nextAccordingToMode() {
        onPlayListener = null;
        if (playMode == Constant.ORDER) {
            next();
        } else if (playMode == Constant.RANDOM) {
            int index = (int) (Math.random() * (currentSongList.getSize() - 1));
            currentSong = currentSongList.getSongList().get(index);
            curSongLiveData.postValue(currentSong);
            play(currentSongList, currentSong);
        } else if (playMode == Constant.LIST_RECYCLE) {
            if (!next()) {
                currentSong = currentSongList.getSongList().get(0);
                curSongLiveData.postValue(currentSong);
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
        curSongLiveData.postValue(currentSong);
        onPlayListener = null;
        play(currentSongList, currentSong);
        return true;
    }

    public void changePlayMode(String mode) {
        playMode = mode;
        playStateLiveData.postValue(isPlaying());
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
        new SearchModel(null).saveStateFromSearch(songList);
    }

    /**
     * 获取上次播放的搜索列表
     */

    public void getSearchSongListFromLocal() {

        currentSongList = new SearchModel(null).loadStateFromSearch();
        curSongListLiveData.postValue(currentSongList);
    }

    public MutableLiveData<SongList> getCurSongListLiveData() {
        return curSongListLiveData;
    }

    public MutableLiveData<Item> getCurSongLiveData() {
        return curSongLiveData;
    }

    public MutableLiveData<Boolean> getPlayStateLiveData() {
        return playStateLiveData;
    }

    public MutableLiveData<String> getPlayModeLiveData() {
        return playModeLiveData;
    }

    public void notifyChange() {
        curSongListLiveData.postValue(currentSongList);
        curSongLiveData.postValue(currentSong);
        playStateLiveData.postValue(isPlaying);
        playModeLiveData.postValue(playMode);
    }
}
