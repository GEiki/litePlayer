package com.dedaodemo.ViewModel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.ISongModel;
import com.dedaodemo.model.impl.SearchModelImpl;
import com.dedaodemo.model.impl.SongModelImpl;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchViewModel extends ViewModel implements SearchContract.Presenter {

    private ISearchModel model = new SearchModelImpl();
    private ISongModel songModel = new SongModelImpl();
    private MutableLiveData<ArrayList<Item>> searchSongList = new MutableLiveData<>();





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
        model.searchSongOnline(bean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<List<Item>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Item> o) {
                        searchSongList.setValue((ArrayList<Item>) o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void addSong(SongList songList, Item item) {
        songModel.addSong(songList, item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

            @Override
            public void onNext(@NonNull Boolean o) {
                ToastUtil.showShort(MyApplication.getMyApplicationContext(), "添加成功");
            }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
        });
    }


}
