package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseContract {

    public interface Presenter {


        void addSongToPlaylist(ArrayList<Item> items);

        SongList getPlaylist();

        void removeAllSongFromPlaylist();

        void removeSongFromPlaylist(int index);

        void saveProgress();

        void initBottomBar();

        void init(boolean startFlags);

        void playSong(SongList songList, Item item);

        void nextSong();

        void preSong();

        void pause();

        void rePlay();

        void setPlayMode(String mode);

        void seekTo(int progress);

        boolean observeData(String name, LifecycleOwner owner, Observer observer);

        void removeObserves(LifecycleOwner owner);

        MutableLiveData<Item> getCurPlaySong();

    }


}
