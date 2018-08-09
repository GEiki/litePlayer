package com.dedaodemo.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dedaodemo.common.Constant;

import java.io.Serializable;

/**
 * Created by guoss on 2017/10/27.
 */

@Entity(primaryKeys = {"song_name", "author_name"})
public class Item implements Serializable
{
    @NonNull
    @ColumnInfo(name = "author_name")
    private String author;
    @NonNull
    @ColumnInfo(name = "song_name")
    private String title;
    private String time;
    private String path;
    private long size;
    private String lrc;
    private String pic;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (type != Constant.INTERNET_MUSIC && type != Constant.LOCAL_MUSIC) {
            throw new IllegalArgumentException("invaild type");
        }else {
            this.type = type;
        }

    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }


    public int getId() {
        return title.hashCode();
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor()
    {
        if(TextUtils.isEmpty(author))
            return "未知歌手";
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }



    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
}
