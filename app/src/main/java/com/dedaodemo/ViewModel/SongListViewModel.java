package com.dedaodemo.ViewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.model.ISongModel;
import com.dedaodemo.model.impl.SheetModelImpl;
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

public class SongListViewModel extends ViewModel implements SongListContract.Presenter {

    private MutableLiveData<SongList> songListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SongList>> sheetList = new MutableLiveData<>();
    private ISongModel model = new SongModelImpl();
    private ISheetModel sheetModel = new SheetModelImpl();
    private BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            songListLiveData.setValue(songListLiveData.getValue());
        }
    };

    public SongListViewModel() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        MyApplication.getMyApplicationContext().registerReceiver(connectReceiver, intentFilter);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        MyApplication.getMyApplicationContext().unregisterReceiver(connectReceiver);
    }



    @Override
    public void loadSongData(final SongList songList) {
        model.loadSongData(songList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<List<Item>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Item> o) {
                        if (o != null) {
                            songList.setSongList((ArrayList<Item>) o);
                            songListLiveData.setValue(songList);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("LoadSongData", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void addSong(ArrayList<Item> items, SongList songList) {
        model.addSongs(songList, items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
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


    @Override
    public void removeSong(final ArrayList<Item> items) {
        model.removeSong(songListLiveData.getValue(), items)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        SongList songList = songListLiveData.getValue();
                        songList.getSongList().removeAll(items);
                        songListLiveData.setValue(songList);
                        ToastUtil.showShort(MyApplication.getMyApplicationContext(), "移除成功");
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
    public MutableLiveData<List<SongList>> getSheetListLiveData() {
        return sheetList;
    }

    @Override
    public void loadSheetList() {
        sheetModel.loadData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<List<SongList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<SongList> o) {
                        sheetList.setValue(o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("loadSheetList", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void observeSongList(LifecycleOwner owner, Observer<SongList> observer) {
        songListLiveData.observe(owner, observer);
    }

    @Override
    public void removeObserveSongList(Observer<SongList> observer) {
        songListLiveData.removeObserver(observer);
    }
}
