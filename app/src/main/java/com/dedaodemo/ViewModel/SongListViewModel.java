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
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.model.ISongModel;
import com.dedaodemo.model.impl.SearchModelImpl;
import com.dedaodemo.model.impl.SheetModelImpl;
import com.dedaodemo.model.impl.SongModelImpl;
import com.dedaodemo.util.DatabaseUtil;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SongListViewModel extends ViewModel implements SongListContract.Presenter {

    private final static String TAG = "SongListViewModel";
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
        songListLiveData.setValue(songList);
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
                        Log.e(TAG,e.getMessage());
                        ToastUtil.showShort(MyApplication.getMyApplicationContext(), "出了点问题");
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
                        Log.e(TAG,e.getMessage());
                        ToastUtil.showShort(MyApplication.getMyApplicationContext(), "出了点问题");
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

    public void downloadPic(final ImageView imageView) {
        SongList songList = songListLiveData.getValue();
        if (songList != null && songList.getSongList().size() > 0) {
        Item item = songList.getSongList().get(0);
        Log.i(TAG,"start download img >>>"+item.getTitle());
        ISearchModel searchModel = new SearchModelImpl();
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(item.getTitle());
        searchBean.setSearchType(Constant.TYPE_WY);
        searchModel.searchSongOnline(searchBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<List<Item>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Item> list) {
                        if (list != null && list.size() > 0) {
                            Log.i(TAG,"download success");
                            Item song = list.get(0);
                            if (songListLiveData.getValue() != null) {
                                Item item1 =songListLiveData.getValue()
                                        .getSongList()
                                        .get(0);
                                item1.setPic(song.getPic());
                                DatabaseUtil.updateItem(item1);
                            }
                            setPic(song,imageView);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    }

    @Override
    public void setPic(Item item, final ImageView imageView) {

        final RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(25, 5))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true);
            String url = item.getPic();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(MyApplication.getMyApplicationContext())
                        .load(R.drawable.default_songlist_background)
                        .apply(requestOptions)
                        .into(imageView);
                Glide.with(MyApplication.getMyApplicationContext())
                        .asDrawable()
                        .apply(requestOptions)
                        .load(item.getPic()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
        } else {
               downloadPic(imageView);
            }
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
