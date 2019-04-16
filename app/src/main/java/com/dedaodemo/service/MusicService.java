package com.dedaodemo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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

    /**
     * 监听来自客户端的消息
     * */
    private  class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            handleReceiveMessage(msg);
        }
    }

    private static final String TAG = "MUSIC_SERVICE";
    private MusicPlayer mp;
    public static final int CHANNEL_ID = 2314;
    private Notification.Builder notificationBuilder;
    private NotificationReceiver notificationReceiver;
    private Notification notification;
    private ArrayList<Item> playlist;
    private int index;
    private int progress;
    private String playMode = Constant.MODE_LIST_RECYCLE;
    private boolean initFlag = false;
    private Timer timer = new Timer();
    private Messenger messenger = new Messenger(new MessengerHandler());
    private Messenger clientMessenger;

    public MusicService() {
    }

    /**
     * 广播监听
     */
    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.ACTION_N_RE_PLAY: {
                    replay();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_PAUSE:{
                    pause();
                    buildNotification();
                    break;
                }
                case Constant.ACTION_N_NEXT:{
                    next();
                    break;
                }
                case Constant.ACTION_N_PRE:{
                    previous();
                    break;
                }
                case Constant.ACTION_N_CLOSE:{
                    stopSelf();
                    break;
                }
                default:break;
            }
        }
    }



    @Override
    public IBinder onBind(final Intent intent) {
        return messenger.getBinder();
    }


    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
                mp.pause();
                if (clientMessenger != null) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.ACTION,Constant.ACTION_N_PAUSE);
                    message.setData(bundle);
                    clientMessenger.send(message);
                }
                timer.cancel();
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp = MusicPlayer.getInstance(MusicService.this);
        mp.setCompletionListener(this);
        mp.setPreparedListener(this);
        mp.setOnErrorListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_N_RE_PLAY);
        intentFilter.addAction(Constant.ACTION_N_PAUSE);
        intentFilter.addAction(Constant.ACTION_N_NEXT);
        intentFilter.addAction(Constant.ACTION_N_PRE);
        intentFilter.addAction(Constant.ACTION_N_CLOSE);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver,intentFilter);
        buildNotification();
        startForeground(CHANNEL_ID,notification);
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
        try {
                if (!initFlag) {//不是初始化
                    mediaPlayer.start();
                    buildNotification();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.CURRENT_SONG,index);
                    bundle.putBoolean(Constant.IS_PLAYING,mp.isPlaying());
                    bundle.putSerializable(Constant.CURRENT_SONGLIST,playlist);
                    bundle.putString(Constant.ACTION,Constant.ACTION_N_PLAYING);
                    bundle.putString(Constant.CURRENT_MODE,playMode);
                    msg.setData(bundle);
                    if (clientMessenger != null) {
                        clientMessenger.send(msg);
                        startSendProgress();
                    }
                    initFlag = false;
                } else {//初始化
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.CURRENT_SONG,index);
                    bundle.putBoolean(Constant.IS_PLAYING,false);
                    bundle.putSerializable(Constant.CURRENT_SONGLIST,playlist);
                    bundle.putInt(Constant.POSITION,progress);
                    bundle.putString(Constant.ACTION,Constant.ACTION_N_PLAYING);
                    bundle.putString(Constant.CURRENT_MODE,playMode);
                    msg.setData(bundle);
                    if (clientMessenger != null) {
                        clientMessenger.send(msg);
                    }
                    mediaPlayer.start();
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.pause();
                    buildNotification();
                    initFlag = false;
                }


        }catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }


    }

    private void startSendProgress() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (clientMessenger != null) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constant.POSITION,mp.getCurrentPosition());
                        bundle.putString(Constant.ACTION,Constant.ACTION_N_POSITION);
                        msg.setData(bundle);
                        clientMessenger.send(msg);
                    }
                }catch (Exception e) {
                    Log.e(TAG,e.getMessage());
                }

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
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleReceiveMessage(Message msg) {
        clientMessenger = msg.replyTo;
        String action = msg.getData().getString(Constant.ACTION);
        switch (action) {
            case Constant.ACTION_N_PLAY: {
                initFlag = false;
                Bundle bundle = msg.getData();
                playlist = (ArrayList<Item>) (bundle.getSerializable(Constant.CURRENT_SONGLIST));
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
                if (initFlag) {
                    return;
                }
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
                try {
                    clientMessenger = msg.replyTo;
                    initFlag = true;
                    if (mp.isPlaying() || mp.isPrepared()) {
                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putString(Constant.ACTION,Constant.ACTION_N_PLAYING);
                        data.putBoolean(Constant.IS_PLAYING,mp.isPlaying());
                        data.putInt(Constant.CURRENT_SONG,index);
                        data.putSerializable(Constant.CURRENT_SONGLIST,playlist);
                        data.putInt(Constant.POSITION,progress);
                        data.putString(Constant.CURRENT_MODE,playMode);
                        message.setData(data);
                        clientMessenger.send(message);
                        initFlag = false;
                        break;
                    }
                    Bundle bundle = msg.getData();
                    playlist = (ArrayList<Item>) bundle.getSerializable(Constant.CURRENT_SONGLIST);
                    index = bundle.getInt(Constant.CURRENT_SONG);
                    progress = bundle.getInt(Constant.POSITION);
                    play(index);

                }catch (Exception e) {
                    Log.e(TAG,e.getMessage());
                }
                break;
            }
            case Constant.ACTION_N_SEEK_TO: {
                Bundle data = msg.getData();
                int x = data.getInt(Constant.POSITION);
                mp.seekTo(x);
                break;
            }
            case Constant.ACTION_N_ACTIVITY_START: {
                try{
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString(Constant.ACTION,Constant.ACTION_N_PLAYING);
                    data.putBoolean(Constant.IS_PLAYING,mp.isPlaying());
                    data.putInt(Constant.CURRENT_SONG,index);
                    data.putSerializable(Constant.CURRENT_SONGLIST,playlist);
                    data.putInt(Constant.POSITION,progress);
                    data.putString(Constant.CURRENT_MODE,playMode);
                    message.setData(data);
                    clientMessenger.send(message);
                }catch (Exception e) {
                    Log.e(TAG,e.getMessage());
                }

                break;
            }
            case Constant.ACTION_N_CHANGE_MOED: {
                Bundle bundle = msg.getData();
                playMode = bundle.getString(Constant.CURRENT_MODE);
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
        try {
            if (mp != null && !mp.isPlaying()) {
                mp.rePlay();
                if (clientMessenger != null) {
                    startSendProgress();
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.ACTION,Constant.ACTION_N_RE_PLAY);
                    message.setData(bundle);
                    clientMessenger.send(message);
                }
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
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
        createNotificationChannel();
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
            ppAction = getNotificationAction(Constant.ACTION_N_RE_PLAY, "播放", R.drawable.ic_action_play_list);
        }
        Notification.Action nextAction = getNotificationAction(Constant.ACTION_N_NEXT, "下一首", R.drawable.ic_action_next);
        Notification.Action closeAction = getNotificationAction(Constant.ACTION_N_CLOSE, "关闭", R.drawable.ic_action_close);

        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
        mediaStyle.setShowActionsInCompactView(1, 2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this,String.valueOf(CHANNEL_ID))
                    .setContentTitle(title)
                    .setContentText(author)
                    .addAction(preAction)
                    .addAction(ppAction)
                    .addAction(nextAction)
                    .addAction(closeAction)
                    .setStyle(mediaStyle)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_action_play_list)
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(author)
                    .addAction(preAction)
                    .addAction(ppAction)
                    .addAction(nextAction)
                    .addAction(closeAction)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(mediaStyle)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_action_play_list)
                    .setContentIntent(pendingIntent);
        }

        notification = notificationBuilder.build();
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
        clientMessenger = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mp != null) {
            mp.release();
        }
        stopForeground(true);
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "音乐播放";
            String decription = "用于音乐播放";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(CHANNEL_ID),name,importance);
            channel.setDescription(decription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
