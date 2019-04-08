package com.dedaodemo.ViewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.util.Log;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.model.ISongModel;
import com.dedaodemo.model.impl.SheetModelImpl;
import com.dedaodemo.model.impl.SongModelImpl;
import com.dedaodemo.util.ScanUtil;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by guoss on 2018/4/26.
 */

public class SheetListViewModel extends ViewModel implements LifecycleObserver, SheetListContract.Presenter {


    private MutableLiveData<ArrayList<SongList>> songListsLiveData;

    private ISheetModel model = new SheetModelImpl();
    private ISongModel songModel = new SongModelImpl();

    private Context mContext = MyApplication.getMyApplicationContext();

    public SheetListViewModel() {
        super();
        songListsLiveData = new MutableLiveData<>();
        songListsLiveData.setValue(new ArrayList<SongList>());

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(LifecycleOwner owner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {

    }

    @Override
    public void scanMusic() {
        ScanUtil.scanMusicFiles(MyApplication.getMyApplicationContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new io.reactivex.Observer<ArrayList<Item>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

                    @Override
                    public void onNext(@NonNull ArrayList<Item> o) {
                        songListsLiveData.setValue(songListsLiveData.getValue());
                        if (o != null) {
                            ToastUtil.showShort(mContext, "共扫描出" + String.valueOf(o.size()) + "首歌曲");
                            if (songListsLiveData.getValue() == null || songListsLiveData.getValue().size() == 0) {
                                createAllSongList(o);
                            } else if (songListsLiveData.getValue().size() > 0) {
                                for (SongList songList : songListsLiveData.getValue()) {
                                    if (mContext.getString(R.string.sheet_local_music).equals(songList.getTitle())) {
                                        checkAndAddSong(o, songList);
                                        break;
                                    } else if (songListsLiveData.getValue().indexOf(songList) == songListsLiveData.getValue().size() - 1) {
                                        createAllSongList(o);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("SCAN", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void createAllSongList(ArrayList<Item> list) {
        SongList songList = new SongList();
        songList.getSongList().addAll(list);
        songList.setTitle(mContext.getString(R.string.sheet_local_music));
        songList.setCreateDate(Util.getCurrentFormatTime());
        songList.setUid(System.currentTimeMillis());
        addSongList(songList);
        addSongs(songList, list);

    }

    @Override
    public void updateSongList(SongList songList) {
        model.updateSongList(songList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new io.reactivex.Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean save) {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void checkAndAddSong(ArrayList<Item> list, SongList songList) {
        ArrayList<Item> extraList = new ArrayList<>();
        for (Item item : list) {
            if (!songList.containItem(item)) {
                songList.addSong(item);
                extraList.add(item);//需要添加到数据库的item
            }
        }
        addSongs(songList, extraList);
    }

    private void addSongs(SongList songList, ArrayList<Item> list) {
        songModel.addSongs(songList, list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean o) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("AddSongs", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    /**
     * 移除歌单
     */
    @Override
    public void removeSongList(final SongList target) {
        if (target.getTitle().equals(mContext.getString(R.string.sheet_local_music))) {
            ToastUtil.showShort(mContext, "全部歌曲歌单不可移除");
            return;
        }
        model.removeSongList(target)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean o) {
                        ArrayList<SongList> list = songListsLiveData.getValue();
                        list.remove(target);
                        songListsLiveData.setValue(list);
                        ToastUtil.showShort(mContext, "已移除");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("removeSong", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 创建歌单
     */
    @Override
    public void addSongList(final SongList songList) {
        model.createSongList(songList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean o) {
                        ArrayList<SongList> list = songListsLiveData.getValue();
                        if (list == null) {
                            list = new ArrayList<SongList>();
                        }
                        list.add(songList);
                        songListsLiveData.setValue(list);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("CreateSongList", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 加载数据
     */
    @Override
    public void loadData() {
        model.loadData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<List<SongList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<SongList> o) {
                        songListsLiveData.setValue((ArrayList<SongList>) o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("LoadData", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    @Override
    public void observeSongLists(LifecycleOwner owner, Observer<ArrayList<SongList>> observer) {
        songListsLiveData.observe(owner, observer);
    }

    @Override
    public void removeObserveSongLists(Observer<ArrayList<SongList>> observer) {
        songListsLiveData.removeObserver(observer);
    }

    //    public void observeCurrentSong(LifecycleOwner owner,Observer<Item> observer) {
//        curSongLiveData.observe(owner,observer);
//    }
//    public void observeCurrentSongList(LifecycleOwner owner,Observer<SongList> observer) {
//        curSongListLiveData.observe(owner,observer);
//    }
//    public void observePlayState(LifecycleOwner owner,Observer<Boolean> observer) {
//        isPlayingLiveData.observe(owner,observer);
//    }
//    public void observePlayMode(LifecycleOwner owner,Observer<String> observer) {
//        playModeLiveData.observe(owner,observer);
//    }


}
