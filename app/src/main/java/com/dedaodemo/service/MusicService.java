package com.dedaodemo.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.dedaodemo.MusicPlayer;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;

import java.util.ArrayList;


public class MusicService extends Service {
    public MusicService() {
    }

    private class MessengerHandler extends android.os.Handler {
        public MessengerHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constant.ACTION_INIT: {//初始化
                    Bundle bundle = msg.getData();
                    SongList songList = (SongList) (bundle.getSerializable(Constant.CURRENT_SONGLIST));
                    initPlayer(songList.getSongList(), (Item) (bundle.getSerializable(Constant.CURRENT_SONG)), msg.replyTo);
                    break;
                }
                case Constant.ACTION_PLAY: {//播放
                    Bundle bundle = msg.getData();
                    SongList songList = (SongList) (bundle.getSerializable(Constant.CURRENT_SONGLIST));
                    play((Item) (bundle.getSerializable(Constant.CURRENT_SONG)), songList.getSongList(), msg.replyTo);
                    break;
                }
                case Constant.ACTION_PAUSE: {//暂停
                    pause();
                    break;
                }
                case Constant.ACTION_RE_PLAY: {//重新播放
                    rePlay(msg.replyTo);
                    break;
                }
                case Constant.ACTION_REQUEST_DURATION: {//获取时长
                    try {
                        Messenger replyMessenger = msg.replyTo;
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putLong(Constant.DURATION, getDuration());
                        bundle.putInt(Constant.POSITION, getCurrentPosition());
                        message.setData(bundle);
                        message.arg1 = Constant.ACTION_REQUEST_DURATION;
                        replyMessenger.send(message);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                case Constant.ACTION_SEEK_TO: {
                    Bundle bundle = msg.getData();
                    seekTo(bundle.getInt(Constant.POSITION));
                    break;

                }
                default:
                    break;
            }

        }
    }


    private Messenger messenger = new Messenger(new MessengerHandler());
    private MusicPlayer mp;
    private boolean isPasusing;
    @Override
    public IBinder onBind(final Intent intent) {
        mp=MusicPlayer.getInstance(MusicService.this);
        return messenger.getBinder();
    }

    public void initPlayer(ArrayList<Item> list, Item tmp, final Messenger replyMessenger) {
        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Message msg = new Message();
                    msg.arg1 = Constant.ACTION_COMPLETE;
                    replyMessenger.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        };
        mp.initPlayer(list, tmp, onCompletionListener, onPreparedListener);
    }

    public void play(Item item, ArrayList<Item> list, final Messenger replyMessenger) {
        mp.play(list, item, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    Message msg = new Message();
                    msg.arg1 = Constant.ACTION_COMPLETE;
                    replyMessenger.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mp.setListener(new MusicPlayer.OnChangeListener() {
            @Override
            public void onPlay() {
                try {
                    Message msg = new Message();
                    msg.arg1 = Constant.ACTION_RE_PLAY;
                    replyMessenger.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("MediaPlayer", "错误码：" + String.valueOf(what));
                mp.release();
                return true;
            }
        });
    }
    public void pause(){
            mp.pause();
            isPasusing=true;
    }

    public void rePlay(final Messenger replyMessenger) {
            mp.rePlay();
        try {
            Message msg = new Message();
            msg.arg1 = Constant.ACTION_RE_PLAY;
            replyMessenger.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isPasusing = false;
    }
    public boolean isPasusing(){
        return isPasusing;
    }
    public long getDuration(){
        return mp.getDuration();
    }
    public void seekTo(int misc){
        mp.seekTo(misc);
    }
    public boolean isPrepared(){
        return mp.isPrepared();
    }
    public int getCurrentPosition(){
        return mp.getCurrentPosition();
    }

    @Override
    public void onDestroy() {
        mp.release();
        super.onDestroy();
    }
}
