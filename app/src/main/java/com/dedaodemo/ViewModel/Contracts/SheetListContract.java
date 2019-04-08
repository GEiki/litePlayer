package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/6/28.
 */

public class SheetListContract {

    public interface Presenter {
         void removeSongList(@NonNull SongList target);

         void addSongList(@NonNull final SongList songList);

         void updateSongList(SongList songList);

         void loadData();

         void scanMusic();

         void observeSongLists(LifecycleOwner owner, Observer<ArrayList<SongList>> observer);

        void removeObserveSongLists(Observer<ArrayList<SongList>> observer);
    }



}
