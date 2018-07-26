package com.dedaodemo.ViewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseViewModel extends ViewModel implements BaseContract.Presenter, LifecycleObserver {

    private MutableLiveData<Boolean> errorFlags = new MutableLiveData<>();


    @Override
    public void playSong(final SongList songList, final Item item) {
        SongManager.getInstance().play(songList, item, new SongManager.OnPlayListener() {
            @Override
            public void onPlay() {
                SongManager.getInstance().getPlayStateLiveData().postValue(true);
            }

            @Override
            public void onError() {
                errorFlags.postValue(true);
            }
        });


    }

    public BaseViewModel() {
        super();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        SongManager.getInstance().notifyChange();
    }


    @Override
    public void initBottomBar() {
        SongManager.getInstance().notifyChange();
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
        SongManager.getInstance().next();
    }

    @Override
    public void preSong() {
        SongManager.getInstance().pre();


    }

    @Override
    public void pause() {
        SongManager.getInstance().pause();
    }

    @Override
    public void rePlay() {
        SongManager.getInstance().rePlay(new SongManager.OnPlayListener() {
            @Override
            public void onPlay() {
                SongManager.getInstance().getPlayStateLiveData().postValue(true);
            }

            @Override
            public void onError() {
                errorFlags.postValue(true);
            }
        });

    }

    @Override
    public void setPlayMode(String mode) {
        SongManager.getInstance().changePlayMode(mode);

    }



    @Override
    public void observeCurrentSong(LifecycleOwner owner, Observer<Item> observer) {
        SongManager.getInstance().getCurSongLiveData().observe(owner, observer);
    }

    @Override
    public void observeCurrentSongList(LifecycleOwner owner, Observer<SongList> observer) {
        SongManager.getInstance().getCurSongListLiveData().observe(owner, observer);
    }

    @Override
    public void observePlayState(LifecycleOwner owner, Observer<Boolean> observer) {
        SongManager.getInstance().getPlayStateLiveData().observe(owner, observer);
    }

    @Override
    public void observePlayMode(LifecycleOwner owner, Observer<String> observer) {
        SongManager.getInstance().getPlayModeLiveData().observe(owner, observer);
    }

    @Override
    public void observeErrorState(LifecycleOwner owner, Observer<Boolean> observer) {
        errorFlags.observe(owner, observer);
    }

    @Override
    public void removeObserves(LifecycleOwner owner) {
        SongManager.getInstance().getCurSongLiveData().removeObservers(owner);
        SongManager.getInstance().getCurSongListLiveData().removeObservers(owner);
        SongManager.getInstance().getPlayStateLiveData().removeObservers(owner);
        SongManager.getInstance().getPlayModeLiveData().removeObservers(owner);
        errorFlags.removeObservers(owner);
    }
}
