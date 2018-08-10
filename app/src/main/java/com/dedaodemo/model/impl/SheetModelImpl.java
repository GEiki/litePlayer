package com.dedaodemo.model.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.dedaodemo.MyApplication;
import com.dedaodemo.bean.CurrentPlayStateBean;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.database.AppDatabase;
import com.dedaodemo.database.AppDatabaseHelper;
import com.dedaodemo.database.dao.ItemSongListDao;
import com.dedaodemo.entity.ItemSongList;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.util.GsonUtil;

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

    public static final String TAG = "SheetModel";

    @Override
    public Observable addSongs(SongList songList, ArrayList<Item> items) {
        return null;
    }

    @Override
    public Observable createSongList(final SongList songList) {
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
                clear(db.itemSongListDao(), songList);
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
    public Observable saveState(final CurrentPlayStateBean currentPlayStateBean) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                SharedPreferences sharedPreferences = MyApplication.getMyApplicationContext().getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE);
                String json = GsonUtil.bean2json(currentPlayStateBean, CurrentPlayStateBean.class);
                sharedPreferences.edit()
                        .putString(Constant.CurrentPlayState.KEY_PLAY_LIST, json)
                        .commit();
                emitter.onNext(true);
                emitter.onComplete();
            }
        });


    }

    private void clear(ItemSongListDao dao, SongList songList) {
        for (Item item : songList.getSongList()) {
            ItemSongList itemSongList = new ItemSongList();
            itemSongList.setAuthor(item.getAuthor());
            itemSongList.setSheet_name(songList.getTitle());
            itemSongList.setSong_name(item.getTitle());
            dao.delete(itemSongList);
        }
    }

    @Override
    public Observable loadPlayList() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter emitter) throws Exception {
                SharedPreferences sp = MyApplication.getMyApplicationContext().getSharedPreferences(Constant.SP_KEY, Context.MODE_PRIVATE);
                String json = sp.getString(Constant.CurrentPlayState.KEY_PLAY_LIST, "");
                CurrentPlayStateBean bean = (CurrentPlayStateBean) GsonUtil.json2Bean(json, CurrentPlayStateBean.class);
                if (bean != null) {
                    emitter.onNext(bean);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("null bean"));
                }
            }
        });
    }

}
