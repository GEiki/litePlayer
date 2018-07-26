package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.dedaodemo.bean.Item;
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

    public interface ViewModel {
        public void loadDataSuccess(ArrayList<SongList> sheetList);

        public void loadFail(String msg);

        public void createSongListSuccess(ArrayList<SongList> sheetList);

        public void removeSongListSuccess(ArrayList<SongList> sheetList);

        public void createSongListFail(String msg);

        public void removeSongListFail(String msg);
    }

    public interface Model {
        public void addSongs(SongList songList, ArrayList<Item> items);

        public void createSongList(SongList songList, int size);

        public void removeSongList(SongList songList);

        public void loadData();
    }

}
