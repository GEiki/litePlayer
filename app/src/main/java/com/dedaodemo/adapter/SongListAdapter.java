package com.dedaodemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/5/11.
 */

public class SongListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SongList> data;

    public SongListAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<SongList> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data != null)
            return data.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(data != null)
            return data.get(position);
        else
            return null;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cardview_layout,parent,false);
        TextView tv_title = v.findViewById(R.id.list_card_title);
        tv_title.setText(data.get(position).getTitle());

        return v;
    }
}
