package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SongListContract {
    public interface Presenter {
        public void addSong(ArrayList<Item> items, SongList songList);

        public void removeSong(ArrayList<Item> items);

        public void setSongList(SongList songList);

        public void observeSongList(LifecycleOwner owner, Observer<SongList> observer);

        public void removeObserveSongList(Observer<SongList> observer);
    }

    public interface ViewModel {
        public void onAddSongSuccess(SongList songList);

        public void onRemoveSongSuccess(SongList songList);
    }

    public interface Model {
        public void addSongToSongList(SongList songList, ArrayList<Item> items);

        public void removeSongFromSongList(SongList songList, ArrayList<Item> items);
    }
}
