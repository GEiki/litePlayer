package com.dedaodemo.ViewModel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.model.SearchModel;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchViewModel extends BaseViewModel implements SearchContract.ViewModel, SearchContract.Presenter {

    private SearchContract.Model model = new SearchModel(this);
    private MutableLiveData<ArrayList<Item>> searchSongList = new MutableLiveData<>();

    @Override
    public void seekTo(int progress) {
        SongManager.getInstance().seekTo(progress);
    }

    @Override
    public void requestProgress(SongManager.IProgressCallback callback) {
        SongManager.getInstance().requestProgress(callback);
    }

    @Override
    public void onSearchSuccess(ArrayList<Item> resultList) {
        searchSongList.postValue(resultList);
    }

    @Override
    public void onSearchFail(String msg) {

    }

    @Override
    public void observeSearchSongList(LifecycleOwner owner, Observer<ArrayList<Item>> observer) {
        searchSongList.observe(owner, observer);
    }

    @Override
    public void searchSong(SearchBean bean) {
        model.searchSongOnline(bean);
    }
}
