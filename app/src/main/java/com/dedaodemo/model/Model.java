package com.dedaodemo.model;

import android.content.Context;
import android.content.res.Resources;

import com.dedaodemo.R;

import java.util.ArrayList;

/**
 * Created by guoss on 2017/10/29.
 */

public class Model
{
    public static String SRC="http://kungfuspeed.com/testonline.mp3";
    public static ArrayList<Item> getItemData(Context context){
        ArrayList<Item> items=new ArrayList<>();
        Resources resources=context.getResources();
        Item i1=new Item();
        i1.setAuthor(resources.getString(R.string.author1));
        i1.setTime(resources.getString(R.string.time1));
        i1.setContent(resources.getString(R.string.content1));
        i1.setTitle(resources.getString(R.string.title1));
        items.add(0,i1);

        Item i2=new Item();
        i2.setAuthor(resources.getString(R.string.author2));
        i2.setTime(resources.getString(R.string.time2));
        i2.setContent(resources.getString(R.string.content2));
        i2.setTitle(resources.getString(R.string.title2));
        items.add(1,i2);

        Item i3=new Item();
        i3.setAuthor(resources.getString(R.string.author3));
        i3.setTime(resources.getString(R.string.time3));
        i3.setContent(resources.getString(R.string.content3));
        i3.setTitle(resources.getString(R.string.title3));
        items.add(2,i3);

        return items;


    }

}
