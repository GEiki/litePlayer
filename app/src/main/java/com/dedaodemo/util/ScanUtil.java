package com.dedaodemo.util;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dedaodemo.bean.Item;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by guoss on 2018/2/23.
 */

public class ScanUtil {
    private static final long TIME_TILTER = 60;

    //传入ApplicationContext防止内存泄漏
    public static ArrayList<Item> scanMusics(Context context){
        ArrayList<Item> musics=new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ARTIST
        };
        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                                                            projection,
                                                            null,
                                                            null,
                                                            null);
        if(cursor != null) {
            cursor.moveToNext();
            while (cursor.moveToNext()) {
                String src = cursor.getString(2);//音频路径
                String name = cursor.getString(0);//音频名称不包括后缀名
                String size = cursor.getString(1);//音频大小
                String artist = cursor.getString(4);//作者
                String duration = cursor.getString(3);//时长
                Item item = new Item();
                item.setPath("file://"+src);
                item.setType(Item.LOCAL_MUSIC);
                item.setTitle(name);
                item.setSize(size);
                item.setAuthor(artist);
                item.setTime(duration);
                musics.add(item);
            }
            cursor.close();
        }
        Cursor cursor2=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
        if(cursor2 != null) {
            cursor2.moveToNext();
            while (cursor2.moveToNext()) {
                String src = cursor2.getString(2);//音频路径
                String name = cursor2.getString(0);//音频名称不包括后缀名
                String size = cursor2.getString(1);//音频大小
                String artist = cursor2.getString(4);//作者
                String duration = cursor2.getString(3);//时长
                Item item = new Item();
                item.setPath("file://"+src);
                item.setType(Item.LOCAL_MUSIC);
                item.setTitle(name);
                item.setSize(size);
                item.setAuthor(artist);
                item.setTime(duration);
                musics.add(item);
            }
            cursor2.close();
        }
        return musics;



    }

    /**
     * 遍历手机文件
     * */
    public static ArrayList<Item> scanMusicFiles(Context context){
        ArrayList<Item> list=new ArrayList<>();
        File src = Environment.getExternalStorageDirectory();
        File storage = src.getParentFile().getParentFile();
        Log.i("ScanUtil",String.valueOf(src.getParentFile().isDirectory()));
        //扫描外置存储卡
        for (File f:storage.listFiles()){//考虑多张外置内存卡的情况
            String n = f.getName();
            if(!n.equals("emulated") && !n.equals("self")){
                startScan(list,f);
            }
        }
        startScan(list,src);//扫描内置存储卡
        return list;

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
                    String size = String.valueOf(file.getTotalSpace());//音频大小
                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//作者
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长
                    //过虑长度小于60s的音频文件
                    long second = Long.valueOf(duration) / 1000;
                    if(second < 60){
                        return;
                    }
                    Item item = new Item();
                    item.setPath("file://"+src);
                    item.setType(Item.LOCAL_MUSIC);
                    String[] strings = title.split("\\.");
                    item.setTitle(strings[0]);
                    item.setSize(size);
                    item.setAuthor(artist);
                    item.setTime(duration);
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
