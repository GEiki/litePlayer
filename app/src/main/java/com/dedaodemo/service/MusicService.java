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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


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
    private String playMode = Constant.MODE_LIST_RECYCLE;
    private boolean initFlag = true;
    private Timer timer = new Timer();

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
        timer.cancel();
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_N_PLAYING);
        intent.putExtra(Constant.IS_PLAYING, mp.isPlaying());
        intent.putExtra(Constant.CURRENT_SONG, index);
        intent.putExtra(Constant.CURRENT_SONGLIST, playlist);
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            int mIndex = getIndexByMode(playMode);
            if (mIndex != -1) {
                play(mIndex);
            }
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
            intent.putExtra(Constant.CURRENT_SONG, index);
            intent.putExtra(Constant.IS_PLAYING, mp.isPlaying());
            intent.putExtra(Constant.CURRENT_SONGLIST, playlist);
            sendBroadcast(intent);
            initFlag = false;
            startSendProgress();
        } else {
            buildNotification();
        }

    }

    private void startSendProgress() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(Constant.ACTION_N_POSITION);
                intent.putExtra(Constant.POSITION, mp.getCurrentPosition());
                sendBroadcast(intent);
            }
        };
        timer.schedule(timerTask, 0, 1000);

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
        intentFilter.addAction(Constant.ACTION_N_SEEK_TO);
        intentFilter.addAction(Constant.ACTION_N_INIT);
        intentFilter.addAction(Constant.ACTION_N_ACTIVITY_START);
        intentFilter.addAction(Constant.ACTION_N_CHANGE_MOED);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleReceiveIntent(Intent intent) {
        switch (intent.getAction()) {
            case Constant.ACTION_N_PLAY: {
                initFlag = false;
                Bundle bundle = intent.getExtras();
                playlist = (ArrayList<Item>) bundle.getSerializable(Constant.CURRENT_SONGLIST);
                index = bundle.getInt(Constant.CURRENT_SONG);
                play(index);
                break;
            }
            case Constant.ACTION_N_NEXT: {
                initFlag = false;
                next();
                break;
            }
            case Constant.ACTION_N_PRE: {
                initFlag = false;
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
                Bundle bundle = intent.getExtras();
                playlist = (ArrayList<Item>) bundle.getSerializable(Constant.CURRENT_SONGLIST);
                index = bundle.getInt(Constant.CURRENT_SONG);
                play(index);
                break;
            }
            case Constant.ACTION_N_SEEK_TO: {
                Bundle data = intent.getExtras();
                int x = data.getInt(Constant.POSITION);
                mp.seekTo(x);
                break;
            }
            case Constant.ACTION_N_ACTIVITY_START: {
                Intent back = new Intent();
                back.setAction(Constant.ACTION_N_PLAYING);
                back.putExtra(Constant.IS_PLAYING, mp.isPlaying());
                back.putExtra(Constant.CURRENT_SONG, index);
                back.putExtra(Constant.CURRENT_SONGLIST, playlist);
                sendBroadcast(back);
                break;
            }
            case Constant.ACTION_N_CHANGE_MOED: {
                playMode = intent.getStringExtra(Constant.CURRENT_MODE);
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
            if (index >= 0 && index < playlist.size() && mp != null) {
                timer.cancel();
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
            startSendProgress();
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_N_PLAYING);
            intent.putExtra(Constant.CURRENT_SONG, index);
            intent.putExtra(Constant.IS_PLAYING, mp.isPlaying());
            intent.putExtra(Constant.CURRENT_SONGLIST, playlist);
            sendBroadcast(intent);
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
     * 根据播放模式获取下一首的index
     * */
    @Override
    public int getIndexByMode(String mode) {
        int mIndex = index;
        switch (mode) {
            case Constant.MODE_ORDER: {
                if (mIndex < playlist.size() - 1) {
                    return mIndex + 1;
                } else {
                    return -1;
                }
            }
            case Constant.MODE_LIST_RECYCLE: {
                if (mIndex < playlist.size() - 1) {
                    return mIndex + 1;
                } else {
                    return 0;
                }
            }
            case Constant.MODE_RANDOM: {
                Random random = new Random();
                random.setSeed(System.currentTimeMillis());
                return random.nextInt(playlist.size());
            }
            case Constant.MOED_SINGLE_RECYCLE: {
                return mIndex;
            }
            default:
                break;
        }
        return 0;
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
        intent.setAction(Constant.ACTION_N_FROM_SERVICE);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action preAction = getNotificationAction(Constant.ACTION_N_PRE, "上一首", R.drawable.ic_action_pre);
        Notification.Action ppAction;
        if (mp.isPlaying()) {
            ppAction = getNotificationAction(Constant.ACTION_N_PAUSE, "暂停", R.drawable.ic_action_pause);
        } else {
            ppAction = getNotificationAction(Constant.ACTION_N_RE_PLAY, "播放", R.drawable.ic_action_play);
        }
        Notification.Action nextAction = getNotificationAction(Constant.ACTION_N_NEXT, "下一首", R.drawable.ic_action_next);
        Notification.Action closeAction = getNotificationAction(Constant.ACTION_N_CLOSE, "关闭", R.drawable.ic_action_close);

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

    private Notification.Action getNotificationAction(String action, String title, int icon) {
        Intent closeIntent = new Intent();
        closeIntent.setAction(action);
        PendingIntent closePIntent = PendingIntent.getBroadcast(this, 2, closeIntent, 0);
        Notification.Action closeAction = new Notification.Action(icon, title, closePIntent);
        return closeAction;
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
