package com.dedaodemo.entity;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * 歌单与歌的关系表
 * Created by 01377578 on 2018/7/27.
 */
@Entity(primaryKeys = {"uid","song_name"})
public class ItemSongList {

    @NonNull
    private long uid;
    @NonNull
    private String sheet_name;
    @NonNull
    private String song_name;
    private String author;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getSheet_name() {
        return sheet_name;
    }

    public void setSheet_name(String sheet_name) {
        this.sheet_name = sheet_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
