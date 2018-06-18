package com.dedaodemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dedaodemo.model.Item;

import java.util.ArrayList;

public class MusicService extends Service {
    public static String ACTION_NEXT = "com.dedaodemo.action.next";
    public static String ACTION_FINISH = "com.dedaodemo.action.finish";
    public static String ACTION_PRE = "com.dedaodemo.action.pre";
    public MusicService() {
    }
    public class MyBinder extends Binder{
        public MyBinder() {
        }
        public MusicService getService(){
            return MusicService.this;
        }
    }

    private MyBinder myBinder=new MyBinder();
    private MusicPlayer mp;
    private boolean isPasusing;
    @Override
    public IBinder onBind(final Intent intent) {
        mp=MusicPlayer.getInstance(MusicService.this);
        mp.setListener(new MusicPlayer.OnChangeListener() {
            @Override
            public void onNext(int index) {
                Intent intent1 = new Intent(MusicService.ACTION_NEXT);
                intent1.putExtra("index",index);
                MusicService.this.sendBroadcast(intent1);
            }

            @Override
            public void onPre(int index) {

                Intent intent2 = new Intent(MusicService.ACTION_PRE);
                intent2.putExtra("index",index);
                MusicService.this.sendBroadcast(intent2);
            }

            @Override
            public void onFinish() {
                Intent intent1 = new Intent(MusicService.ACTION_FINISH);
                MusicService.this.sendBroadcast(intent1);
            }
        });
        return myBinder;
    }

    public void play(int index, ArrayList<Item> list){
            mp.play(list,index,MusicPlayer.ORDER,null);
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
    public boolean next(){return mp.next();}
    public boolean pre(){return mp.pre();}
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
