package com.dedaodemo.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.dedaodemo.MyApplication;
import com.dedaodemo.service.MusicService;

/**
 * Created by Guoss on 2018/6/28.
 */

public class MusicServiceManager {
    public interface OnMusicListener {
        void onMusicCallBack(int msg);
    }

    public interface OnProgressListener {
        void progress(int position, long duration);
    }

    private OnMusicListener onPlayListener;
    private OnProgressListener onProgressListener;

    private class MessengerHandler extends android.os.Handler {


        public MessengerHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == Constant.ACTION_REQUEST_DURATION && onProgressListener != null) {
                Bundle bundle = msg.getData();
                onProgressListener.progress(bundle.getInt(Constant.POSITION), bundle.getLong(Constant.DURATION));
            } else if (onPlayListener != null) {
                onPlayListener.onMusicCallBack(msg.arg1);
            }

        }
    }


    private static MusicServiceManager instance;
    private boolean isServiceConnecting = false;
    private Context context = MyApplication.getMyApplicationContext();
    private Messenger messenger;
    private Messenger replyMessenger = new Messenger(new MessengerHandler());

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ServiceConnect", "Connect Success");
            messenger = new Messenger(service);
            isServiceConnecting = true;
            //连接建立后初始化播放器
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnecting = false;
        }
    };


    private MusicServiceManager() {

    }


    public static MusicServiceManager getInstance() {
        if (instance == null) {
            synchronized (MusicServiceManager.class) {
                if (instance == null) {
                    instance = new MusicServiceManager();
                }
            }
        }
        return instance;
    }


    public void setOnPlayListener(OnMusicListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void init() {
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    public void sendMessage(Bundle bundle, int arg) {
        Message message = new Message();
        message.replyTo = replyMessenger;
        message.arg1 = arg;
        message.setData(bundle);
        try {
            messenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindService(Intent intent) {
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindMusicService() {
        if (isServiceConnecting) {
            context.unbindService(serviceConnection);
        }
    }

}
