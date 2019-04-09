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

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class MusicServiceManager {

    public interface onMusicServiceBind {
        void  onBind();
    }



    private static MusicServiceManager instance;
    private boolean isServiceConnecting = false;
    private Context context = MyApplication.getMyApplicationContext();
    private Messenger messenger;
    private ArrayList<onMusicServiceBind> mOnMusicServiceBind = new ArrayList<>();
    private static final String TAG = "MUSIC_SERVICE_MANAGER";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "服务绑定成功");
            messenger = new Messenger(service);
            isServiceConnecting = true;
            //连接建立后初始化播放器
            if (mOnMusicServiceBind != null) {
                for(onMusicServiceBind i:mOnMusicServiceBind) {
                    i.onBind();
                }
                mOnMusicServiceBind.clear();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "服务解除绑定");
            messenger = null;
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


    public void setmOnMusicServideBind(onMusicServiceBind mOnMusicServiceBind) {
        this.mOnMusicServiceBind.add(mOnMusicServiceBind);
    }

    public void init() {
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    public void sendMessage(Message message) {
        try {
            if (message != null) {
                messenger.send(message);
            } else {
                Log.e(TAG,"播放服务没有绑定");
            }

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
            isServiceConnecting = false;
        }
    }

    public boolean isBind() {
        return isServiceConnecting;
    }

}
