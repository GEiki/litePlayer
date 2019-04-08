package com.dedaodemo.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.dedaodemo.bean.Item;
import com.dedaodemo.common.Constant;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by guoss on 2018/2/23.
 */

public class ScanUtil {
    private static final long TIME_TILTER = 60;
    private static final String TAG = "Scan";

    public interface ScanCallback {
        public void scanFinished(ArrayList<Item> list);
    }



    /**
     * 遍历手机文件
     * */
    public static Observable scanMusicFiles(Context context) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Item>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Item>> emitter) throws Exception {
                ArrayList<Item> list = new ArrayList<>();
                File src = Environment.getExternalStorageDirectory();
                File storage = src.getParentFile().getParentFile();
                Log.i("ScanUtil", String.valueOf(src.getParentFile().isDirectory()));
                //扫描外置存储卡
                for (File f : storage.listFiles()) {//考虑多张外置内存卡的情况
                    String n = f.getName();
                    if (!n.equals("emulated") && !n.equals("self")) {
                        startScan(list, f);
                    }
                }
                startScan(list, src);//扫描内置存储卡
                emitter.onNext(list);
                emitter.onComplete();
            }
        });
    }
    private static void startScan(ArrayList<Item> list,File file){
        if(!file.isDirectory()) {
            String name = file.getName();
            String title = name;
            String sub = name.substring(name.lastIndexOf(".")+1);
            if("mp3".equals(sub)){
                MediaMetadataRetriever mmr = null;
                try{
                    mmr=new MediaMetadataRetriever();
                    mmr.setDataSource(file.getAbsolutePath());
                    String src = file.getAbsolutePath();//音频路径
                    String size = String.valueOf(file.length());//音频大小
                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//作者
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长
                    String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);//专辑名
                    //过虑长度小于60s的音频文件
                    long second = Long.valueOf(duration) / 1000;
                    if(second < 60){
                        return;
                    }
                    Item item = new Item();
                    item.setPath("file://"+src);
                    item.setType(Constant.LOCAL_MUSIC);
                    String[] strings = title.split("\\.");
                    item.setTitle(Util.getPureSongName(strings[0]));
                    item.setSize(Long.valueOf(size));
                    item.setAuthor(artist);
                    item.setTime(duration);
                    item.setAlbum(album);
                    list.add(item);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(mmr != null){
                        mmr.release();
                    }
                }

            }
            return;
        }
        //递归扫描文件
        File[] files = file.listFiles();
        if(files != null && files.length != 0) {
            for (File f : files) {
                startScan(list, f);
            }
        }
    }


}
