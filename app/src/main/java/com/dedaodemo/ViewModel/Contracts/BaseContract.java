package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseContract {

    public interface Presenter {
        void initBottomBar();

        void playSong(SongList songList, Item item);

        void nextSong();

        void preSong();

        void pause();

        void rePlay();

        void setPlayMode(String mode);

        void seekTo(int progress);

        boolean observeData(String name, LifecycleOwner owner, Observer observer);

        void removeObserves(LifecycleOwner owner);

    }

    public interface ViewModel {
        void onLoadBottomBarStateSuccess(SongList songList, Item item);

        void onSaveBottomBarStateSuccess();
    }

    public interface Model {
        void loadBottomBarState();

        void saveBottomBarState(SongList songList, Item item);
    }
}
