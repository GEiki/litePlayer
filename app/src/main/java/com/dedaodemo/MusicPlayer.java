package com.dedaodemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.dedaodemo.bean.Item;
import com.dedaodemo.common.Constant;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/3/10.
 */

public class MusicPlayer {
    public interface OnChangeListener{
        public void onPlay();
    }

    public final static int ORDER=0;
    public final static int RANDOM=1;

    private MediaPlayer mPlayer;
    private static MusicPlayer instance;
    private int mode = ORDER;
    private int index;
    private Context mContext;
    private OnChangeListener listener;
    private ArrayList<Item> list;
    private boolean isPrepared;
    private MediaPlayer.OnCompletionListener completionListener;
    private MediaPlayer.OnPreparedListener preparedListener;
    /**
     * 在线音乐标志
     */
    private boolean INTERNET_MUSIC_FLAG = false;

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

    public void play(ArrayList<Item> list, Item item) {

        try {
            this.list = list;
            if (mPlayer != null) {
                    mPlayer.release();

            }
            initPlayer(list, item, completionListener, preparedListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCompletionListener(MediaPlayer.OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setPreparedListener(MediaPlayer.OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void initPlayer(ArrayList<Item> list, Item tmp, MediaPlayer.OnCompletionListener completionListener, MediaPlayer.OnPreparedListener preparedListener) {
        try {
            mPlayer = new MediaPlayer();
            String path = tmp.getPath();
            if (tmp.getType() == Constant.INTERNET_MUSIC) {//读取在线音乐
                path = MyApplication.getProxyServer().getProxyUrl(path);//请求重定向至代理服务器
                mPlayer.setDataSource(mContext, Uri.parse(path));
                INTERNET_MUSIC_FLAG = true;
            } else {//读取本地音乐
                INTERNET_MUSIC_FLAG = false;
                mPlayer.setDataSource(mContext, Uri.parse(path));
            }
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            isPrepared = false;
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(preparedListener);


            mPlayer.setOnCompletionListener(completionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener errorListener) {
        if (mPlayer != null) {
            mPlayer.setOnErrorListener(errorListener);
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

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
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
        if (mPlayer == null) {
            return false;
        }
            return mPlayer.isPlaying();
    }


    public long getDuration(){
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getDuration();
    }
    public void seekTo(int x){
        if (mPlayer == null)
            return;
        mPlayer.seekTo(x);
    }
    public int getCurrentPosition(){
        if (mPlayer == null)
            return 0;
        return mPlayer.getCurrentPosition();
    }
    public void release(){
        if(mPlayer != null)
            mPlayer.release();
            mPlayer = null;
    }
    public boolean isPrepared(){
        return isPrepared;
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

}
