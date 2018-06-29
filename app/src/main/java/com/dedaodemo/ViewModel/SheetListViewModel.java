package com.dedaodemo.ViewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Handler;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.model.SongModel;
import com.dedaodemo.util.ScanUtil;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;


/**
 * Created by guoss on 2018/4/26.
 */

public class SheetListViewModel extends BaseViewModel implements LifecycleObserver, SheetListContract.Presenter, SheetListContract.ViewModel {


    private MutableLiveData<ArrayList<SongList>> songListsLiveData;
    //    protected MutableLiveData<Item> curSongLiveData;
//    protected MutableLiveData<SongList> curSongListLiveData;
//    protected MutableLiveData<Boolean> isPlayingLiveData;
//    protected MutableLiveData<String> playModeLiveData;
    private SheetListContract.Model model = new SongModel(this);
    private Handler handler = new Handler();

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
        ToastUtil.showShort("后台扫描中");
        ScanUtil.scanMusicFiles(MyApplication.getMyApplicationContext(), new ScanUtil.ScanCallback() {
            @Override
            public void scanFinished(ArrayList<Item> list) {
                model.addSongs(SongManager.getInstance().getSheetList().get(0), list);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort("扫描完毕");
                    }
                });

            }
        });
    }


    @Override
    public void loadDataSuccess(ArrayList<SongList> sheetList) {
        SongManager.getInstance().setSheetList(sheetList);
        songListsLiveData.postValue(sheetList);
        SongManager.getInstance().init();
        updateCurrentSongList(SongManager.getInstance().getCurrentSongList());
        updateCurrentSong(SongManager.getInstance().getCurrentSong());
    }

    @Override
    public void loadFail(String msg) {

    }


    @Override
    public void createSongListSuccess(ArrayList<SongList> sheetList) {
        songListsLiveData.postValue(sheetList);
    }

    @Override
    public void removeSongListSuccess(ArrayList<SongList> sheetList) {
        songListsLiveData.postValue(sheetList);
    }

    @Override
    public void createSongListFail(String msg) {

    }

    @Override
    public void removeSongListFail(String msg) {

    }


    /**
     * 移除歌单
     */
    @Override
    public void removeSongList(SongList target) {
        model.removeSongList(target);
    }

    /**
     * 创建歌单
     */
    @Override
    public void addSongList(SongList songList) {
        model.createSongList(songList, SongManager.getInstance().getSheetList().size());

    }

    /**
     * 加载数据
     */
    @Override
    public void loadData() {
        model.loadData();

    }


    @Override
    public void observeSongLists(LifecycleOwner owner, Observer<ArrayList<SongList>> observer) {
        songListsLiveData.observe(owner, observer);
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
