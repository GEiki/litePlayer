package com.dedaodemo.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

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
                    rePlay();
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
    }
    public void pause(){
            mp.pause();
            isPasusing=true;
    }
    public void rePlay(){
            mp.rePlay();
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
