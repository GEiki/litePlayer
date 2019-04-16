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
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
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
import com.dedaodemo.service.MusicService;
import com.dedaodemo.ui.MainActivity;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by guoss on 2018/6/29.
 */

public class BaseViewModel extends ViewModel implements BaseContract.Presenter, LifecycleObserver {



    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data1 = msg.getData();
            String action = data1.getString(Constant.ACTION);
            switch (action) {
                case Constant.ACTION_N_PLAYING: {
                    Bundle data = msg.getData();
                    boolean p = data.getBoolean(Constant.IS_PLAYING, false);
                    isPlaying.setValue(p);
                    int index = data.getInt(Constant.CURRENT_SONG, 0);
                    if (curPlayList.getValue() == null) {
                        SongList curSonglist =new SongList();
                        curSonglist.setSongList((ArrayList<Item>)data.getSerializable(Constant.CURRENT_SONGLIST));
                        curSonglist.setTitle("播放列表");
                        curPlayList.setValue(curSonglist);
                    }
                    curPlaySong.setValue(curPlayList.getValue().getSongList().get(index));
                    postion.postValue(data.getInt(Constant.POSITION,0));
                    playMode.setValue(data.getString(Constant.CURRENT_MODE));
                    //保存播放状态
                    if (curPlayList.getValue() != null) {
                        currentPlayStateBean = new CurrentPlayStateBean();
                        currentPlayStateBean.setIndex(index);
                        currentPlayStateBean.setMode(playMode.getValue());
                        currentPlayStateBean.setPlayList(curPlayList.getValue().getSongList());
                    }

                    break;
                }
                case Constant.ACTION_N_POSITION: {
                    Bundle bundle = msg.getData();
                    postion.postValue(bundle.getInt(Constant.POSITION));
                    break;
                }
                case Constant.ACTION_N_RE_PLAY:{
                    isPlaying.setValue(true);
                    break;
                }
                case Constant.ACTION_N_PAUSE:{
                    isPlaying.setValue(false);
                    break;
                }
                default:
                    break;

            }
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
    private Context mContext = MyApplication.getMyApplicationContext();
    private boolean first_flags = true;
    private boolean curSongChangeFlags = false;
    private CurrentPlayStateBean currentPlayStateBean;
    private Messenger replyMessenger = new Messenger(new MessageHandler());

    public static final String CURRENT_LIST_DATA = "current_list_data";
    public static final String CURRENT_SONG_DATA = "current_song_data";
    public static final String ERROR_FLAGS_DATA = "error_flag_data";
    public static final String PLAY_MODE_DATA = "play_mode_data";
    public static final String IS_PLAYING_DATA = "is_playing_data";
    public static final String POSTION = "postion";


    @Override
    public void saveProgress() {
        if (currentPlayStateBean != null && postion.getValue() != null) {
           currentPlayStateBean.setProgress(postion.getValue());
            saveState(currentPlayStateBean);
        }
    }

    private void saveState(CurrentPlayStateBean bean) {
        bottomBarModel.saveState(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        Log.i("SaveState","success");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("SaveState", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void playSong(final SongList songList, final Item item) {

        isPlaying.setValue(true);
        if (!songList.getTitle().equals("播放列表")) {
           SongList curSonglist =new SongList();
           curSonglist.setSongList(songList.getSongList());
           curSonglist.setTitle("播放列表");
           curPlayList.setValue(curSonglist);
        }
        curPlaySong.setValue(item);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CURRENT_SONGLIST, (ArrayList)curPlayList.getValue().getSongList());
        bundle.putInt(Constant.CURRENT_SONG, curPlayList.getValue().getSongList().indexOf(item));
        Message message = new Message();
        bundle.putString(Constant.ACTION,Constant.ACTION_N_PLAY);
        message.setData(bundle);
        message.replyTo = replyMessenger;
        MusicServiceManager.getInstance().sendMessage(message);
    }

    public BaseViewModel() {
        super();
        liveDataMap.put(CURRENT_SONG_DATA, curPlaySong);
        liveDataMap.put(CURRENT_LIST_DATA, curPlayList);
        liveDataMap.put(ERROR_FLAGS_DATA, errorFlags);
        liveDataMap.put(PLAY_MODE_DATA, playMode);
        liveDataMap.put(IS_PLAYING_DATA, isPlaying);
        liveDataMap.put(POSTION, postion);




    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
    }

    @Override
    public void init(final boolean startFlags) {
        if (startFlags) {
            final Message message = new Message();
            message.replyTo =replyMessenger;
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ACTION,Constant.ACTION_N_ACTIVITY_START);
            message.setData(bundle);
            if (MusicServiceManager.getInstance().isBind()) {
                MusicServiceManager.getInstance().sendMessage(message);
            } else {
                MusicServiceManager.getInstance().setmOnMusicServideBind(new MusicServiceManager.onMusicServiceBind() {
                    @Override
                    public void onBind() {
                        MusicServiceManager.getInstance().sendMessage(message);
                    }
                });
            }
            return;
        }
        bottomBarModel.loadPlayList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new io.reactivex.Observer<CurrentPlayStateBean>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(CurrentPlayStateBean o) {
                    if (o != null) {
                        SongList songList = new SongList();
                        songList.setSongList((ArrayList<Item>) (o.getPlayList()));
                        songList.setTitle("播放列表");
                        final Message message = new Message();
                        message.replyTo = replyMessenger;
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ACTION,Constant.ACTION_N_INIT);
                        int index = o.getIndex();
                        int progress = o.getProgress();
                        if (o.getPlayList() != null) {
                            curPlayList.setValue(songList);
                            curPlaySong.setValue(o.getPlayList().get(index));
                            postion.setValue(o.getProgress());
                        }
                        Log.i("Test",String.valueOf(o.getPlayList().size()));
                        bundle.putSerializable(Constant.CURRENT_SONGLIST, (ArrayList)songList.getSongList());
                        bundle.putInt(Constant.CURRENT_SONG, index);
                        bundle.putInt(Constant.POSITION,progress);
                        message.setData(bundle);
                        if (MusicServiceManager.getInstance().isBind()) {
                            MusicServiceManager.getInstance().sendMessage(message);
                        } else {
                            MusicServiceManager.getInstance().setmOnMusicServideBind(new MusicServiceManager.onMusicServiceBind() {
                                @Override
                                public void onBind() {
                                    MusicServiceManager.getInstance().sendMessage(message);
                                }
                            });
                        }
                    }



                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });
//        final Message  message = new Message();
//        Bundle bundle = new Bundle();
//        bundle.putString(Constant.ACTION,Constant.ACTION_N_INIT);
//        bundle.putSerializable(Constant.CURRENT_SONGLIST,curPlayList.getValue());
//        int index = curPlayList.getValue().getSongList().indexOf(curPlaySong.getValue());
//        bundle.putInt(Constant.CURRENT_SONG,index);
//        bundle.putInt(Constant.POSITION,postion.getValue());
//        message.setData(bundle);
//        message.replyTo = replyMessenger;
//        if (MusicServiceManager.getInstance().isBind()) {
//            MusicServiceManager.getInstance().sendMessage(message);
//        } else {
//            MusicServiceManager.getInstance().setmOnMusicServideBind(new MusicServiceManager.onMusicServiceBind() {
//                @Override
//                public void onBind() {
//                    MusicServiceManager.getInstance().sendMessage(message);
//                }
//            });
//        }

    }

    @Override
    public void initBottomBar() {
        for (String k : liveDataMap.keySet()) {
            if (k != POSTION) {
                liveDataMap.get(k).setValue(liveDataMap.get(k).getValue());
            }

        }
    }




    @Override
    public void addSongToPlaylist(ArrayList<Item> items) {
        if (curPlayList.getValue() != null) {
            curPlayList.getValue().getSongList().addAll(items);
        } else {
            SongList songList = new SongList();
            songList.setTitle("播放列表");
            songList.getSongList().addAll(items);
            curPlayList.setValue(songList);
            playSong(songList,items.get(0));
        }

    }

    @Override
    public void removeSongFromPlaylist(int index) {
      Item item = curPlayList.getValue().getSongList().get(index);
      int size = curPlayList.getValue().getSongList().size();
      if (item  == curPlaySong.getValue()) {
          if (index < size-1) {
              curPlaySong.setValue(curPlayList.getValue().getSongList().get(index+1));
              curPlayList.getValue().getSongList().remove(index);
              if (isPlaying.getValue()) {
                  playSong(curPlayList.getValue(),curPlaySong.getValue());
              } else {
                  curSongChangeFlags = true;
              }
          } else if (index > 0){
              curPlaySong.setValue(curPlayList.getValue().getSongList().get(index-1));
              curPlayList.getValue().getSongList().remove(index);
              if (isPlaying.getValue()) {
                  playSong(curPlayList.getValue(),curPlaySong.getValue());
              } else {
                  curSongChangeFlags = true;
              }
          } else {
              curPlayList.getValue().getSongList().remove(index);
              curPlaySong.setValue(null);
              pause();
          }
      } else {
          curPlayList.getValue().getSongList().remove(index);
      }
    }

    @Override
    public void removeAllSongFromPlaylist() {
        curPlayList.getValue().getSongList().clear();
        curPlaySong.setValue(null);
    }

    @Override
    public SongList getPlaylist() {
        return curPlayList.getValue();
    }

    @Override
    public MutableLiveData<Item> getCurPlaySong() {
        return curPlaySong;
    }

    @Override
    public void seekTo(int progress) {
        if(curPlaySong.getValue() == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, progress);
        Message message = new Message();
        message.replyTo = replyMessenger;
        bundle.putString(Constant.ACTION,Constant.ACTION_N_SEEK_TO);
        message.setData(bundle);
        MusicServiceManager.getInstance().sendMessage(message);
    }

    @Override
    public void nextSong() {
        if(curPlaySong.getValue() == null) {
            ToastUtil.showShort(mContext,"没有歌曲可以播放");
            return;
        }
        isPlaying.setValue(true);
        List<Item> items = curPlayList.getValue().getSongList();
        Item song = curPlaySong.getValue();
        int index = items.indexOf(song);
        if (index < items.size() - 1) {
            curPlaySong.setValue(items.get(index + 1));
        } else {
            ToastUtil.showShort(mContext, "已经是最后一首歌曲");
        }
        Message message = new Message();
        message.replyTo = replyMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ACTION,Constant.ACTION_N_NEXT);
        message.setData(bundle);
        MusicServiceManager.getInstance().sendMessage(message);
    }

    @Override
    public void preSong() {
        if(curPlaySong.getValue() == null) {
            ToastUtil.showShort(mContext,"没有歌曲可以播放");
            return;
        }
        isPlaying.setValue(true);
        List<Item> items = curPlayList.getValue().getSongList();
        Item song = curPlaySong.getValue();
        int index = items.indexOf(song);
        if (index > 0) {
            curPlaySong.setValue(items.get(index - 1));
        } else {
            ToastUtil.showShort(mContext, "已经是第一首歌曲");
        }
        Message message = new Message();
        message.replyTo = replyMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ACTION,Constant.ACTION_N_PRE);
        message.setData(bundle);
        MusicServiceManager.getInstance().sendMessage(message);
    }


    @Override
    public void pause() {
        isPlaying.setValue(false);
        Message message = new Message();
        message.replyTo = replyMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ACTION,Constant.ACTION_N_PAUSE);
        message.setData(bundle);
        MusicServiceManager.getInstance().sendMessage(message);
    }

    @Override
    public void rePlay() {
        if(curPlaySong.getValue() == null) {
            ToastUtil.showShort(mContext,"没有歌曲可以播放");
            isPlaying.setValue(false);
            return;
        }
        if (curSongChangeFlags) {
            playSong(curPlayList.getValue(),curPlaySong.getValue());
            curSongChangeFlags = false;
            return;
        }
        isPlaying.setValue(true);
        Message message = new Message();
        message.replyTo = replyMessenger;
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ACTION,Constant.ACTION_N_RE_PLAY);
        message.setData(bundle);
        MusicServiceManager.getInstance().sendMessage(message);
//        if (first_flags) {
//            SongList songList = new SongList();
//            songList.setSongList(curPlayList.getValue().getSongList());
//            songList.setTitle("播放列表");
//            playSong(songList, curPlaySong.getValue());
//            first_flags = false;
//        } else {
//            Intent intent = new Intent();
//            intent.setAction(Constant.ACTION_N_RE_PLAY);
//            mContext.sendBroadcast(intent);
//        }


    }

    @Override
    public void setPlayMode(String mode) {
        playMode.setValue(mode);
        Bundle data = new Bundle();
        data.putString(Constant.CURRENT_MODE,mode);
        Message message = new Message();
        data.putString(Constant.ACTION,Constant.ACTION_N_CHANGE_MOED);
        message.setData(data);
        message.replyTo = replyMessenger;
        MusicServiceManager.getInstance().sendMessage(message);
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
