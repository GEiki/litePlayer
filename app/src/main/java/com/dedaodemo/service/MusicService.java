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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dedaodemo.MusicPlayer;
import com.dedaodemo.R;
import com.dedaodemo.bean.CurrentPlayStateBean;
import com.dedaodemo.bean.Item;
import com.dedaodemo.common.Constant;
import com.dedaodemo.ui.MainActivity;

import java.util.ArrayList;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MusicPlayer.OnChangeListener,
        MediaPlayer.OnErrorListener,
        IMusicPlayer {

    private static final String TAG = "MUSIC_SERVICE";
    private MusicPlayer mp;
    public static final int CHANNEL_ID = 2314;
    private Notification.Builder notificationBuilder;
    private NotificationReceiver notificationReceiver;
    private ArrayList<Item> playlist;
    private int index;
    private String playMode = Constant.MODE_ORDER;
    private boolean initFlag = false;

    public MusicService() {
    }

    /**
     * 广播监听
     */
    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleReceiveIntent(intent);
        }
    }


    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }


    /**
     * 暂停
     */
    @Override
    public void pause() {
        mp.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (!initFlag) {
            mediaPlayer.start();
            buildNotification();
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_N_PLAYING);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.CURRENT_SONG, index);
            intent.putExtras(bundle);
            sendBroadcast(intent);
            initFlag = false;
        }

    }

    @Override
    public void onPlay() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        try {

            mp.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp = MusicPlayer.getInstance(MusicService.this);
        mp.setCompletionListener(this);
        mp.setPreparedListener(this);
        mp.setOnErrorListener(this);
        buildNotification();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_N_PAUSE);
        intentFilter.addAction(Constant.ACTION_N_PRE);
        intentFilter.addAction(Constant.ACTION_N_NEXT);
        intentFilter.addAction(Constant.ACTION_N_PLAY);
        intentFilter.addAction(Constant.ACTION_N_CLOSE);
        intentFilter.addAction(Constant.ACTION_N_RE_PLAY);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleReceiveIntent(Intent intent) {
        switch (intent.getAction()) {
            case Constant.ACTION_N_PLAY: {
                Bundle bundle = intent.getExtras();
                playlist = (ArrayList<Item>) bundle.getSerializable(Constant.CURRENT_SONGLIST);
                index = bundle.getInt(Constant.CURRENT_SONG);
                play(index);
                break;
            }
            case Constant.ACTION_N_NEXT: {
                next();
                break;
            }
            case Constant.ACTION_N_PRE: {
                previous();
                break;
            }
            case Constant.ACTION_N_PAUSE: {
                pause();
                buildNotification();
                break;
            }
            case Constant.ACTION_N_RE_PLAY: {
                replay();
                buildNotification();
                break;
            }
            case Constant.ACTION_N_CLOSE: {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(CHANNEL_ID);
                break;
            }
            case Constant.ACTION_N_INIT: {
                initFlag = true;
                Bundle bundle = intent.getExtras();
                playlist = (ArrayList<Item>) bundle.getSerializable(Constant.CURRENT_SONGLIST);
                index = bundle.getInt(Constant.CURRENT_SONG);
                play(index);
                break;
            }
            default:
                break;
        }
    }


    /**
     * 播放
     */
    @Override
    public void play(int index) {
        if (playlist != null && playlist.size() > 0) {
            if (index > 0 && index < playlist.size() && mp != null) {
                mp.play(playlist, playlist.get(index));
                this.index = index;
            }
        }
    }

    /**
     * 重新播放
     */
    @Override
    public void replay() {
        if (mp != null && !mp.isPlaying()) {
            mp.rePlay();
        }

    }
    /**
     * 下一首
     * */
    @Override
    public void next() {
        if (index < playlist.size() - 1) {
            play(index + 1);
        }
    }
    /**
     * 上一首
     * */
    @Override
    public void previous() {
        if (index > 0) {
            play(index - 1);
        }
    }
    /**
     * 切换播放模式
     * */
    @Override
    public void changMode(String mode) {
        playMode = mode;
    }


    @Override
    public void onDestroy() {
        Log.i("Service", "destroy");
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
        String title;
        String author;
        if (playlist != null && playlist.size() != 0) {
            title = playlist.get(index).getTitle();
            author = playlist.get(index).getAuthor();
        } else {
            title = "未知歌曲";
            author = "未知歌手";
        }
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
        if (mp.isPlaying()) {
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
                .setContentTitle(title)
                .setContentText(author)
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

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(CHANNEL_ID);
        if (mp != null) {
            mp.release();
        }
        super.onTaskRemoved(rootIntent);
    }

    private void update(CurrentPlayStateBean currentPlayStateBean) {
        try {

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
