package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by 01377578 on 2018/6/28.
 */

public class SheetListContract {

    public interface Presenter {
        public void removeSongList(@NonNull SongList target);

        public void addSongList(@NonNull final SongList songList);

        public void loadData();

        public void scanMusic();

        public void observeSongLists(LifecycleOwner owner, Observer<ArrayList<SongList>> observer);

        void removeObserveSongLists(Observer<ArrayList<SongList>> observer);
    }



}
