package com.dedaodemo.ViewModel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.model.SearchModel;
import com.dedaodemo.model.SongModel;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchViewModel extends BaseViewModel implements SearchContract.ViewModel, SearchContract.Presenter, SongListContract.ViewModel {

    private SearchContract.Model model = new SearchModel(this);
    private SongModel songModel = new SongModel(this);
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
    public void removeObserveSearchSongList(Observer<ArrayList<Item>> observer) {
        searchSongList.removeObserver(observer);
    }

    @Override
    public void searchSong(SearchBean bean) {
        model.searchSongOnline(bean);
    }

    @Override
    public void addSong(SongList songList, Item item) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        songModel.addSongToSongList(songList, items);
    }


    @Override
    public void onAddSongSuccess(SongList songList) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(MyApplication.getMyApplicationContext(), "添加成功");
            }
        });
    }

    @Override
    public void onRemoveSongSuccess(SongList songList) {

    }
}
