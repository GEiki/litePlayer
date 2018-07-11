package com.dedaodemo.bean;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by guoss on 2018/4/15.
 */

public class SongList implements Serializable {
    private String title = "默认歌单";
    private String description;
    private String createDate;
    private String size = "0";
    private int color;
    private ArrayList<Item> items = new ArrayList<>();

    public SongList(){
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean containItem(Item item){

        for(Item i : items){
            if(i.getTitle().equals(item.getTitle()))
                return true;
        }
        return false;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

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

    public ArrayList<Item> getSongList() {
        return items;
    }

    public void setSongList(ArrayList<Item> songList) {
        this.items = songList;
    }

    public boolean addSong(Item item){
        if(item == null){
            return false;
        }
        if (items.contains(item))
            return false;
        items.add(item);
        int count = Integer.valueOf(size);
        size = String.valueOf(count++);
        return true;

    }
    public boolean removeSong(Item item){
        if(item == null){
            return false;
        }
        if(!items.contains(item)){
            return false;
        }
        items.remove(item);
        int count = Integer.valueOf(size);
        size = String.valueOf(count--);
        return  true;
    }

}
