package com.dedaodemo.model.impl;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.model.ISongModel;
import com.dedaodemo.util.DatabaseUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class SongModelImpl implements ISongModel {

    @Override
    public Observable addSong(final SongList songList, final Item item) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                DatabaseUtil.insertSongToSongList(songList, item);
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable removeSong(final SongList songList, final Item item) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                DatabaseUtil.deleteSongFromSongList(songList, item);
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable loadSongData(final SongList songList) {
        return Observable.create(new ObservableOnSubscribe<List<Item>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Item>> emitter) throws Exception {
                List<Item> list = DatabaseUtil.queryBySheet(songList.getTitle());
                emitter.onNext(list);
                emitter.onComplete();
            }
        });
    }
}
