package com.dedaodemo.ViewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.CurrentPlayStateBean;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.MusicServiceManager;
import com.dedaodemo.model.ISheetModel;
import com.dedaodemo.model.impl.SheetModelImpl;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseViewModel extends ViewModel implements BaseContract.Presenter, LifecycleObserver {

    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }
    }

    private Map<String, MutableLiveData> liveDataMap = new HashMap<>();
    private MutableLiveData<Boolean> errorFlags = new MutableLiveData<>();
    private MutableLiveData<SongList> curPlayList = new MutableLiveData<>();
    private MutableLiveData<Item> curPlaySong = new MutableLiveData<>();
    private MutableLiveData<String> playMode = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private MutableLiveData<Integer> postion = new MutableLiveData<>();

    private ISheetModel bottomBarModel = new SheetModelImpl();
    private MusicReceiver receiver = new MusicReceiver();
    private Context mContext = MyApplication.getMyApplicationContext();
    private boolean first_flags = true;

    public static final String CURRENT_LIST_DATA = "current_list_data";
    public static final String CURRENT_SONG_DATA = "current_song_data";
    public static final String ERROR_FLAGS_DATA = "error_flag_data";
    public static final String PLAY_MODE_DATA = "play_mode_data";
    public static final String IS_PLAYING_DATA = "is_playing_data";
    public static final String POSTION = "postion";



    @Override
    public void playSong(final SongList songList, final Item item) {
        //保存播放状态
        CurrentPlayStateBean bean = new CurrentPlayStateBean();
        int index = songList.getSongList().indexOf(item);
        String mode = Constant.MODE_RANDOM;
        bean.setIndex(index);
        bean.setMode(mode);
        bean.setPlayList(songList.getSongList());
        bottomBarModel.saveState(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("SaveState", e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

        isPlaying.setValue(true);
        curPlayList.setValue(songList);
        curPlaySong.setValue(item);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONGLIST, curPlayList.getValue().getSongList());
        bundle.putInt(Constant.CURRENT_SONG, curPlayList.getValue().getSongList().indexOf(item));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setAction(Constant.ACTION_N_PLAY);
        mContext.sendBroadcast(intent);
    }

    public BaseViewModel() {
        super();
        liveDataMap.put(CURRENT_SONG_DATA, curPlaySong);
        liveDataMap.put(CURRENT_LIST_DATA, curPlayList);
        liveDataMap.put(ERROR_FLAGS_DATA, errorFlags);
        liveDataMap.put(PLAY_MODE_DATA, playMode);
        liveDataMap.put(IS_PLAYING_DATA, isPlaying);
        liveDataMap.put(POSTION, postion);

        MusicServiceManager.getInstance().setOnPlayListener(new MusicServiceManager.OnMusicListener() {
            @Override
            public void onMusicCallBack(int msg) {
                switch (msg) {
                    case Constant.ACTION_RE_PLAY: {
                        isPlaying.setValue(true);
                        break;
                    }
                    case Constant.ACTION_ERROR: {
                        break;
                    }
                    case Constant.ACTION_NEXT_SONG: {
                        nextSong();
                        break;
                    }
                    case Constant.ACTION_PAUSE: {
                        pause();
                        break;
                    }
                    case Constant.ACTION_PRE_SONG: {
                        preSong();
                        break;
                    }
                    default:
                        break;
            }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_N_PLAYING);
        intentFilter.addAction(Constant.ACTION_N_POSITION);
        mContext.registerReceiver(receiver, intentFilter);


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mContext.unregisterReceiver(receiver);
    }

    @Override
    public void init(final boolean startFlags) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_ACTIVITY_START);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void initBottomBar() {
        for (String k : liveDataMap.keySet()) {
            if (k != POSTION) {
                liveDataMap.get(k).setValue(liveDataMap.get(k).getValue());
            }

        }
    }


    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {
            case Constant.ACTION_N_PLAYING: {
                boolean p = intent.getBooleanExtra(Constant.IS_PLAYING, false);
                isPlaying.setValue(p);
                int index = intent.getIntExtra(Constant.CURRENT_SONG, 0);
                ArrayList<Item> list = (ArrayList<Item>) (intent.getSerializableExtra(Constant.CURRENT_SONGLIST));
                curPlaySong.setValue(list.get(index));
                SongList songList = new SongList();
                songList.setTitle("播放列表");
                songList.setSongList(list);
                curPlayList.setValue(songList);
                break;
            }
            case Constant.ACTION_N_POSITION: {
                Bundle bundle = intent.getExtras();
                postion.postValue(bundle.getInt(Constant.POSITION));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public MutableLiveData<Item> getCurPlaySong() {
        return curPlaySong;
    }

    @Override
    public void seekTo(int progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, progress);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_SEEK_TO);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void nextSong() {
        isPlaying.setValue(true);
        ArrayList<Item> items = curPlayList.getValue().getSongList();
        Item song = curPlaySong.getValue();
        int index = items.indexOf(song);
        if (index < items.size() - 1) {
            curPlaySong.setValue(items.get(index + 1));
        } else {
            ToastUtil.showShort(mContext, "已经是最后一首歌曲");
        }
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_NEXT);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void preSong() {
        isPlaying.setValue(true);
        ArrayList<Item> items = curPlayList.getValue().getSongList();
        Item song = curPlaySong.getValue();
        int index = items.indexOf(song);
        if (index > 0) {
            curPlaySong.setValue(items.get(index - 1));
        } else {
            ToastUtil.showShort(mContext, "已经是第一首歌曲");
        }
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_PRE);
        mContext.sendBroadcast(intent);
    }


    @Override
    public void pause() {
        isPlaying.setValue(false);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_PAUSE);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void rePlay() {
        isPlaying.setValue(true);
        if (first_flags) {
            SongList songList = new SongList();
            songList.setSongList(curPlayList.getValue().getSongList());
            songList.setTitle("播放列表");
            playSong(songList, curPlaySong.getValue());
            first_flags = false;
        } else {
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_N_RE_PLAY);
            mContext.sendBroadcast(intent);
        }


    }

    @Override
    public void setPlayMode(String mode) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CURRENT_MODE, mode);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_NEXT);
        intent.putExtras(intent);
        mContext.sendBroadcast(intent);

    }


    @Override
    public boolean observeData(String name, LifecycleOwner owner, Observer observer) {
        if (!liveDataMap.containsKey(name)) {
            return false;
        }
        liveDataMap.get(name).observe(owner, observer);
        return true;
    }

    @Override
    public void removeObserves(LifecycleOwner owner) {
        for (String key : liveDataMap.keySet()) {
            liveDataMap.get(key).removeObservers(owner);
        }
    }

}
