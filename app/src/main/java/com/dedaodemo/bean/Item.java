package com.dedaodemo.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by guoss on 2017/10/27.
 */

public class Item implements Serializable
{
    public static final int INTERNET_MUSIC = 1;
    public static final int LOCAL_MUSIC = 2;

    private String author;
    private String title;
    private String time;
    private String content;
    private String path;
    private String size;
    private String URL;
    private String lrc;
    private String pic;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if(type != INTERNET_MUSIC && type != LOCAL_MUSIC){
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

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getId() {
        return title.hashCode();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
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


    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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
