package com.dedaodemo.ViewModel.Contracts;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchContract {


    public interface Presenter {
        void searchSong(SearchBean bean, LifecycleOwner owner, Observer<ArrayList<Item>> observer);

        void addSong(SongList songList, Item item);

        void removeObserveSearchSongList(Observer<ArrayList<Item>> observer);

        void getSheetList(LifecycleOwner owner, Observer<List<SongList>> observer);

    }


}
