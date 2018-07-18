package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchContract {


    public interface Presenter {
        public void searchSong(SearchBean bean);

        public void addSong(SongList songList, Item item);
        public void observeSearchSongList(LifecycleOwner owner, Observer<ArrayList<Item>> observer);
    }

    public interface ViewModel {
        public void onSearchSuccess(ArrayList<Item> resultList);

        public void onSearchFail(String msg);
    }

    public interface Model {
        public void searchSongOnline(SearchBean bean);

        public void saveStateFromSearch(SongList songList);

        public SongList loadStateFromSearch();
    }
}
