package com.dedaodemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dedaodemo.MusicPlayer;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.ui.MainActivity;

import java.util.ArrayList;


public class MusicService extends Service {
    public MusicService() {
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.ACTION_N_NEXT: {
                    Log.i("ACTION", "NEXT");
                    SongManager.getInstance().next();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_PAUSE: {
                    Log.i("ACTION", "PAUSE");
                    SongManager.getInstance().pause();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_PRE: {
                    Log.i("ACTION", "PRE");
                    SongManager.getInstance().pre();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_PLAY: {
                    Log.i("ACTION", "PLAY");
                    SongManager.getInstance().rePlay(null);
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_CLOSE: {
                    mp.pause();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(CHANNEL_ID);
                    break;

                }
            }
        }
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
                    buildNotification();
                    break;
                }
                case Constant.ACTION_PAUSE: {//暂停
                    pause();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_RE_PLAY: {//重新播放
                    rePlay(msg.replyTo);
                    buildNotification();
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
    public static final int CHANNEL_ID = 2314;
    private Notification.Builder notificationBuilder;
    private NotificationReceiver notificationReceiver;



    @Override
    public IBinder onBind(final Intent intent) {
        mp=MusicPlayer.getInstance(MusicService.this);
        buildNotification();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_N_PAUSE);
        intentFilter.addAction(Constant.ACTION_N_PRE);
        intentFilter.addAction(Constant.ACTION_N_NEXT);
        intentFilter.addAction(Constant.ACTION_N_PLAY);
        intentFilter.addAction(Constant.ACTION_N_CLOSE);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, intentFilter);
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
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.setPrepared(true);
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
                try {
                    Log.e("MediaPlayer", "错误码：" + String.valueOf(what));
                    Message msg = new Message();
                    msg.arg1 = Constant.ACTION_ERROR;
                    replyMessenger.send(msg);
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        if (mp != null && isPrepared()) {
            return mp.getDuration();
        } else {
            return 0;
        }

    }
    public void seekTo(int misc){
        if (mp != null && isPrepared())
            mp.seekTo(misc);
    }
    public boolean isPrepared(){
        return mp.isPrepared();
    }
    public int getCurrentPosition(){
        if (mp != null && isPrepared()) {
            return mp.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void onDestroy() {
        if (mp != null && notificationReceiver != null) {
            mp.release();
            unregisterReceiver(notificationReceiver);
        }

        super.onDestroy();
    }

    /**
     * 构建通知栏播放器
     */
    private void buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent preIntent = new Intent();
        preIntent.setAction(Constant.ACTION_N_PRE);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 1, preIntent, 0);
        Notification.Action preAction = new Notification.Action(R.drawable.ic_action_pre, "上一首", prePIntent);

        Intent ppIntent = new Intent();
        Notification.Action ppAction;
        if (SongManager.getInstance().isPlaying()) {
            ppIntent.setAction(Constant.ACTION_N_PAUSE);
            PendingIntent pppIntent = PendingIntent.getBroadcast(this, 0, ppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            ppAction = new Notification.Action(R.drawable.ic_action_pause, "暂停", pppIntent);
        } else {
            ppIntent.setAction(Constant.ACTION_N_PLAY);
            PendingIntent pppIntent = PendingIntent.getBroadcast(this, 0, ppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            ppAction = new Notification.Action(R.drawable.ic_action_play, "播放", pppIntent);
        }

        Intent nextIntent = new Intent();
        nextIntent.setAction(Constant.ACTION_N_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 2, nextIntent, 0);
        Notification.Action nextAction = new Notification.Action(R.drawable.ic_action_next, "下一首", nextPendingIntent);

        Intent closeIntent = new Intent();
        closeIntent.setAction(Constant.ACTION_N_CLOSE);
        PendingIntent closePIntent = PendingIntent.getBroadcast(this, 2, closeIntent, 0);
        Notification.Action closeAction = new Notification.Action(R.drawable.ic_action_close, "关闭", closePIntent);


        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
        mediaStyle.setShowActionsInCompactView(1, 2);
        notificationBuilder = new Notification.Builder(this)
                .setContentTitle(SongManager.getInstance().getCurrentSong().getTitle())
                .setContentText(SongManager.getInstance().getCurrentSong().getAuthor())
                .addAction(preAction)
                .addAction(ppAction)
                .addAction(nextAction)
                .addAction(closeAction)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_action_play)
                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManagerCompat = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(CHANNEL_ID, notification);
    }


}
