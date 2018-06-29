package com.dedaodemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.dedaodemo.bean.Item;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/3/10.
 */

public class MusicPlayer {
    public interface OnChangeListener{
        public void onNext(int index);
        public void onPre(int index);
        public void onFinish();
    }

    public final static int ORDER=0;
    public final static int RANDOM=1;

    private MediaPlayer mPlayer;
    private static MusicPlayer instance;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    private int mode = ORDER;
    private int index;
    private Context mContext;
    private OnChangeListener listener;
    private ArrayList<Item> list;
    private boolean isPrepared;

    private MusicPlayer(Context context){
        mPlayer = null;
        mContext=context;
    }

    public static MusicPlayer getInstance(Context context){
        if(instance == null){
            synchronized (MusicPlayer.class) {
                if (instance == null) {
                    instance = new MusicPlayer(context);
                }

            }

        }
        return instance;
    }

    public void play(ArrayList<Item> list, Item item, MediaPlayer.OnCompletionListener completionListener) {

        try {
            this.list = list;
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayer.release();
                }
                }
            initPlayer(list, item, completionListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayer(ArrayList<Item> list, Item tmp, MediaPlayer.OnCompletionListener completionListener) {
        try {
            mPlayer = new MediaPlayer();
            String path = tmp.getPath();
            if (tmp.getType() == Item.INTERNET_MUSIC) {//读取在线音乐
                path = MyApplication.getProxyServer().getProxyUrl(path);//请求重定向至代理服务器
                mPlayer.setDataSource(mContext, Uri.parse(path));
            } else {//读取本地音乐
                mPlayer.setDataSource(mContext, Uri.parse(path));
            }
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepareAsync();

            MediaPlayer.OnPreparedListener mListener = new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            };
            mPlayer.setOnPreparedListener(mListener);

            mPlayer.setOnCompletionListener(completionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pause(){
        try{
            if(mPlayer!=null){
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rePlay(){
        try{
            if(mPlayer!=null){
                if(!mPlayer.isPlaying()){
                    mPlayer.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isPlaying(){
            return mPlayer.isPlaying();
    }


    public long getDuration(){
        return mPlayer.getDuration();
    }
    public void seekTo(int x){
        mPlayer.seekTo(x);
    }
    public int getCurrentPosition(){
        return mPlayer.getCurrentPosition();
    }
    public void release(){
        if(mPlayer != null)
            mPlayer.release();
    }
    public boolean isPrepared(){
        return mPlayer == null?false:true;
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

}
