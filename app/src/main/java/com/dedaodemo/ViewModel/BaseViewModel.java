package com.dedaodemo.ViewModel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.util.ToastUtil;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseViewModel extends ViewModel implements BaseContract.Presenter {
    protected MutableLiveData<Item> curSongLiveData = new MutableLiveData<>();
    protected MutableLiveData<SongList> curSongListLiveData = new MutableLiveData<>();
    protected MutableLiveData<Boolean> isPlayingLiveData = new MutableLiveData<>();
    protected MutableLiveData<String> playModeLiveData = new MutableLiveData<>();

    @Override
    public void playSong(SongList songList, Item item) {
        SongManager.getInstance().play(songList, item);
        updateCurrentSong(item);
        updateCurrentSongList(songList);
    }

    public BaseViewModel() {
        super();
    }

    @Override
    public void requestProgress(SongManager.IProgressCallback callback) {
        SongManager.getInstance().requestProgress(callback);
    }

    @Override
    public void seekTo(int progress) {
        SongManager.getInstance().seekTo(progress);
    }

    @Override
    public void nextSong() {
        if (SongManager.getInstance().next()) {
            updateCurrentSong(SongManager.getInstance().getCurrentSong());
        } else {
            ToastUtil.showShort("没有更多歌曲了");
        }
    }

    @Override
    public void preSong() {
        if (SongManager.getInstance().pre()) {
            updateCurrentSong(SongManager.getInstance().getCurrentSong());
        } else {
            ToastUtil.showShort("这已经是第一首歌");
        }

    }

    @Override
    public void pause() {
        SongManager.getInstance().pause();
        updatePlayState(SongManager.getInstance().isPlaying());
    }

    @Override
    public void rePlay() {
        SongManager.getInstance().rePlay();
        updatePlayState(SongManager.getInstance().isPlaying());
    }

    @Override
    public void setPlayMode(String mode) {
        SongManager.getInstance().changePlayMode(mode);
        updatePlayMode(SongManager.getInstance().getPlayMode());
    }


    public void updateCurrentSong(Item item) {
        curSongLiveData.postValue(item);
    }

    public void updateCurrentSongList(SongList songList) {
        curSongListLiveData.postValue(songList);
    }

    public void updatePlayState(boolean isPlaying) {
        isPlayingLiveData.postValue(isPlaying);
    }

    public void updatePlayMode(String playMode) {
        playModeLiveData.postValue(playMode);
    }

    @Override
    public void observeCurrentSong(LifecycleOwner owner, Observer<Item> observer) {
        curSongLiveData.observe(owner, observer);
    }

    @Override
    public void observeCurrentSongList(LifecycleOwner owner, Observer<SongList> observer) {
        curSongListLiveData.observe(owner, observer);
    }

    @Override
    public void observePlayState(LifecycleOwner owner, Observer<Boolean> observer) {
        isPlayingLiveData.observe(owner, observer);
    }

    @Override
    public void observePlayMode(LifecycleOwner owner, Observer<String> observer) {
        playModeLiveData.observe(owner, observer);
    }
}
