package com.dedaodemo.model.impl;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.database.AppDatabase;
import com.dedaodemo.database.AppDatabaseHelper;
import com.dedaodemo.entity.ItemSongList;
import com.dedaodemo.model.ISheetModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class SheetModelImpl implements ISheetModel {

    @Override
    public Observable addSongs(SongList songList, ArrayList<Item> items) {
        return null;
    }

    @Override
    public Observable createSongList(final SongList songList, int size) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                AppDatabaseHelper helper = new AppDatabaseHelper();
                AppDatabase db = helper.getDatabase();
                db.songListDao().insertAll(songList);
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable removeSongList(final SongList songList) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                AppDatabaseHelper helper = new AppDatabaseHelper();
                AppDatabase db = helper.getDatabase();
                db.songListDao().delete(songList);
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable loadData() {
        return Observable.create(new ObservableOnSubscribe<List<SongList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SongList>> emitter) throws Exception {
                AppDatabaseHelper helper = new AppDatabaseHelper();
                AppDatabase db = helper.getDatabase();
                List<SongList> songLists = db.songListDao().queryAll();
                emitter.onNext(songLists);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable saveState(final List<Item> list, final Item item) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                SongList songList = new SongList();
                songList.setTitle(Constant.STATE_TABLE_NAME);
                AppDatabaseHelper helper = new AppDatabaseHelper();
                AppDatabase db = helper.getDatabase();

                db.songListDao().insertAll(songList);
                for (Item item : list) {
                    ItemSongList itemSongList = new ItemSongList();
                    itemSongList.setSong_name(item.getTitle());
                    itemSongList.setSheet_name(Constant.STATE_TABLE_NAME);
                    itemSongList.setAuthor(item.getAuthor());
                    db.itemSongListDao().insertAll(itemSongList);
                    db.itemDao().insertAll(item);
                }
                emitter.onNext(true);
                emitter.onComplete();
            }
        });
    }
}
