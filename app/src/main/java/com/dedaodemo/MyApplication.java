package com.dedaodemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.danikula.videocache.HttpProxyCacheServer;
import com.dedaodemo.bean.CurrentPlayStateBean;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.cache.MyFileNameGenerator;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.MusicServiceManager;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.model.impl.SheetModelImpl;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by guoss on 2018/4/26.
 */

public class MyApplication extends Application {
        private static Context mContext;
        private static HttpProxyCacheServer server;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //初始化播放服务
        MusicServiceManager.getInstance().init();

        ISheetModel model = new SheetModelImpl();
        model.loadPlayList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<CurrentPlayStateBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(CurrentPlayStateBean o) {
                        SongList songList = new SongList();
                        songList.setSongList((ArrayList<Item>) (o.getPlayList()));
                        songList.setTitle("播放列表");
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        int index = o.getIndex();
                        bundle.putSerializable(Constant.CURRENT_SONGLIST, songList.getSongList());
                        bundle.putInt(Constant.CURRENT_SONG, index);
                        intent.putExtras(bundle);
                        intent.setAction(Constant.ACTION_N_INIT);
                        mContext.sendBroadcast(intent);

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static HttpProxyCacheServer getProxyServer(){
        if(mContext != null){
            return  server == null ? (newProxy()): server;
        }
        return null;
    }

    /**
     * 初始化音频缓存代理服务器
     * */
    private static HttpProxyCacheServer newProxy(){
        server = new HttpProxyCacheServer.Builder(mContext)
                .maxCacheSize(1024 * 1024 * 100)//缓存大小为100M
                .fileNameGenerator(new MyFileNameGenerator())//设置缓存文件命名方式
                .build();
        return server;
    }

    public static Context getMyApplicationContext(){
        return mContext;
    }

}
