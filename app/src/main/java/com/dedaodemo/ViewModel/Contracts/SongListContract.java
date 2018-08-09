package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SongListContract {
    public interface Presenter {
        void addSong(ArrayList<Item> items, SongList songList);

        void removeSong(ArrayList<Item> items);

        void loadSongData(SongList songList);

        void loadSheetList();

        MutableLiveData getSheetListLiveData();

        void observeSongList(LifecycleOwner owner, Observer<SongList> observer);

        void removeObserveSongList(Observer<SongList> observer);
    }

}
