package com.dedaodemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dedaodemo.bean.Item;
import com.dedaodemo.R;

import java.util.ArrayList;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<Item> itemData = null;
    private int ChooseItem;
    public MListAdapter(Context context){
        mContext=context;
    }

    public void setItemData(ArrayList<Item> data){
        itemData = data;
    }

    public void setChooseItem(int chooseItem) {
        ChooseItem = chooseItem;
    }

    public int getChooseItem() {
        return ChooseItem;
    }

    @Override
    public int getCount(){
       return itemData.size();
    }
    @Override
    public Item getItem(int position){
        return itemData.get(position);
    }
    @Override
    public long getItemId(int pos){
        return pos;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v= LayoutInflater.from(mContext).inflate(R.layout.misc_item,parent,false);
        TextView tv_titile=(TextView)v.findViewById(R.id.tv_title);
        TextView tv_artist=(TextView)v.findViewById(R.id.tv_artist);
        LinearLayout linearLayout=(LinearLayout)v.findViewById(R.id.ll_item);

        tv_titile.setText(itemData.get(pos).getTitle());
        tv_artist.setText(itemData.get(pos).getAuthor());

        return v;
    }
}
