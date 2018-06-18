package com.dedaodemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.dedaodemo.model.Item;

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
            instance = new MusicPlayer(context);
        }
        return instance;
    }

    public void play(final ArrayList<Item> list,
                     final int index,
                     int mode,
                     @Nullable MediaPlayer.OnPreparedListener onPreparedListener){

        try{
            this.mode=mode;
            this.index=index;
            this.list=list;
            if(mPlayer != null){
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    mPlayer.release();
                }
                mPlayer = new MediaPlayer();
                Item tmp = list.get(index);
                String path=tmp.getPath();
                if(tmp.getType() == Item.INTERNET_MUSIC){//读取在线音乐
                    path = MyApplication.getProxyServer().getProxyUrl(path);//请求导向代理服务器
                    mPlayer.setDataSource(mContext, Uri.parse(path));
                }else {//读取本地音乐
                    mPlayer.setDataSource(mContext, Uri.parse(path));
                }

                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync();
                if(onPreparedListener==null){
                    MediaPlayer.OnPreparedListener mListener=new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    };
                    mPlayer.setOnPreparedListener(mListener);
                    this.onPreparedListener=mListener;
                }else {
                    mPlayer.setOnPreparedListener(onPreparedListener);
                    this.onPreparedListener=onPreparedListener;
                }



                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next();
                    }
                });
            }else {
                mPlayer = new MediaPlayer();
                Item tmp = list.get(index);
                String path=tmp.getPath();
                if(tmp.getType() == Item.INTERNET_MUSIC){//读取在线音乐
                    path = MyApplication.getProxyServer().getProxyUrl(path);//请求重定向至代理服务器
                    mPlayer.setDataSource(mContext, Uri.parse(path));
                }else {//读取本地音乐
                    mPlayer.setDataSource(mContext, Uri.parse(path));
                }
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync();
                if(onPreparedListener==null){
                    MediaPlayer.OnPreparedListener mListener=new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    };
                    mPlayer.setOnPreparedListener(mListener);
                    this.onPreparedListener=mListener;
                }else {
                    mPlayer.setOnPreparedListener(onPreparedListener);
                    this.onPreparedListener=onPreparedListener;
                }


                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next();

                    }
                });
            }
        }catch (Exception e){
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

    public boolean next(){
        if(list==null)
            return false;
        if(listener == null)
            return false;

        switch (mode){
            case ORDER:{
                if(index>=list.size()-1||index<0){
                    listener.onFinish();
                    return false;
                }else {
                    if(index<list.size()-1)
                        index++;
                    else
                        index=0;
                    play(list,index,mode,null);
                    listener.onNext(index);
                    return true;
                }
            }
            case RANDOM:{
                index=(int)Math.random()*(list.size()-1);
                play(list,index,mode,null);
                listener.onNext(index);
                return true;
            }
            default:break;
        }
        return false;
    }

    public boolean pre(){
        if(list==null)
            return false;
        if(index == 0) {
            listener.onFinish();
            return false;
        }
        --index;
        play(list,index,mode,null);
        listener.onPre(index);
        return true;
    }

    public void changeMode(int mode){
        this.mode=mode;
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
