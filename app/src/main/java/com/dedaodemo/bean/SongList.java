package com.dedaodemo.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by guoss on 2018/4/15.
 */

@Entity
public class SongList implements Serializable {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "sheet_name")
    private String title;

    private String description;

    private String createDate;

    private int size;

    @Ignore
    private int color;

    @Ignore
    private ArrayList<Item> items = new ArrayList<>();

    public SongList(){
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Ignore
    public boolean containItem(Item item){

        for(Item i : items){
            if(i.getTitle().equals(item.getTitle()))
                return true;
        }
        return false;
    }

    @Ignore
    public int getColor() {
        return color;
    }

    @Ignore
    public void setColor(int color) {
        this.color = color;
    }

    @Ignore
    public String getTableName(){
        if(title != null){
            char[] chars=title.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<chars.length;i++){
                stringBuilder.append(String.valueOf((int)chars[i]));
            }
            Log.i("TABLENAME",stringBuilder.toString());
            return "songlist_"+stringBuilder.toString();
        }
        return null;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public int getSize(){
        return items.size();
    }

    @Ignore
    public ArrayList<Item> getSongList() {
        return items;
    }

    @Ignore
    public void setSongList(ArrayList<Item> songList) {
        this.items = songList;
    }

    @Ignore
    public boolean addSong(Item item){
        if(item == null){
            return false;
        }
        if (items.contains(item))
            return false;
        items.add(0, item);
        int count = size;
        size = ++count;
        return true;

    }

    @Ignore
    public boolean removeSong(Item item){
        if(item == null){
            return false;
        }
        if(!items.contains(item)){
            return false;
        }
        items.remove(item);
        int count = Integer.valueOf(size);
        size = --count;
        return  true;
    }

}
