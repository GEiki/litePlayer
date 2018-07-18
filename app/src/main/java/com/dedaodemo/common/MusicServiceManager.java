package com.dedaodemo.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.dedaodemo.MyApplication;
import com.dedaodemo.service.MusicService;

/**
 * Created by Guoss on 2018/6/28.
 */

public class MusicServiceManager {

    private static class MessengerHandler extends android.os.Handler {
        public MessengerHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constant.ACTION_REQUEST_DURATION: {
                    Bundle bundle = msg.getData();
                    SongManager.getInstance().updateProgress(bundle.getInt(Constant.POSITION), bundle.getLong(Constant.DURATION));
                    break;
                }
                case Constant.ACTION_RE_PLAY: {
                    SongManager.getInstance().onPlay();
                    break;
                }
                case Constant.ACTION_ERROR: {
                    SongManager.getInstance().onError();
                    break;
                }
                default:
                    break;
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
            messenger = new Messenger(service);
            isServiceConnecting = true;
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

    public void init() {
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

    public void unBindMusicService() {
        if (isServiceConnecting) {
            context.unbindService(serviceConnection);
        }
    }

}
