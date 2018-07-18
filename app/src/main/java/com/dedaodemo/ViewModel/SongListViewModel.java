package com.dedaodemo.ViewModel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.model.SongModel;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SongListViewModel extends BaseViewModel implements SongListContract.ViewModel, SongListContract.Presenter {

    private MutableLiveData<SongList> songListLiveData = new MutableLiveData<>();
    private SongListContract.Model model = new SongModel(this);


    @Override
    public void setSongList(SongList songList) {
        songListLiveData.setValue(songList);
    }

    @Override
    public void addSong(ArrayList<Item> items, SongList songList) {
        model.addSongToSongList(songList, items);
    }

    @Override
    public void requestProgress(SongManager.IProgressCallback callback) {
        SongManager.getInstance().requestProgress(callback);
    }

    @Override
    public void removeSong(ArrayList<Item> items) {
        model.removeSongFromSongList(songListLiveData.getValue(), items);
    }

    @Override
    public void seekTo(int progress) {
        SongManager.getInstance().seekTo(progress);
    }


    @Override
    public void onAddSongSuccess(SongList songList) {
        songListLiveData.postValue(songList);
    }

    @Override
    public void onRemoveSongSuccess(SongList songList) {
        songListLiveData.postValue(songList);
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
