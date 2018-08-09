package com.dedaodemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.danikula.videocache.HttpProxyCacheServer;
import com.dedaodemo.cache.MyFileNameGenerator;
import com.dedaodemo.common.MusicServiceManager;

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
