package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseContract {

    public interface Presenter {
        public void initBottomBar();

        public void playSong(SongList songList, Item item);

        public void nextSong();

        public void preSong();

        public void pause();

        public void rePlay();

        public void setPlayMode(String mode);

        public void requestProgress(SongManager.IProgressCallback callback);

        public void seekTo(int progress);

        public void observeCurrentSong(LifecycleOwner owner, Observer<Item> observer);

        public void observeCurrentSongList(LifecycleOwner owner, Observer<SongList> observer);

        public void observePlayState(LifecycleOwner owner, Observer<Boolean> observer);

        public void observePlayMode(LifecycleOwner owner, Observer<String> observer);
    }
}
