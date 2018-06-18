package com.dedaodemo.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dedaodemo.model.Item;
import com.dedaodemo.R;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/4/17.
 */

public class ChooseAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<Item> data;
    private ArrayList<Item> chooseItems = new ArrayList<>();
    private ArrayList<Integer> chooseIndexs = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    public ChooseAdapter(Context context, ArrayList<Item> data){
        this.data=data;
        mContext=context;
    }


    public ArrayList<Item> getChooseItems() {
        return chooseItems;
    }
    public ArrayList<Integer>getChooseIndexs(){
        return chooseIndexs;
    }

    @Override
    public int getCount(){
        return data.size();
    }
    @Override
    public Item getItem(int position){
        return data.get(position);
    }
    @Override
    public long getItemId(int pos){
        return pos;
    }
    @Override
    public View getView(final int pos, View convertView, ViewGroup parent){
        View v= LayoutInflater.from(mContext).inflate(R.layout.choose_music_item,parent,false);

        TextView tv_titile=(TextView)v.findViewById(R.id.tv_title);
        TextView tv_artist=(TextView)v.findViewById(R.id.tv_artist);
        final CheckBox radioButton = (CheckBox) v.findViewById(R.id.radio_Button);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton.setChecked(true);
            }
        });

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chooseItems.add(data.get(pos));
                    chooseIndexs.add(pos);
                }else {
                    chooseItems.remove(data.get(pos));
                    chooseIndexs.remove(pos);
                }
            }
        });

        tv_titile.setText(data.get(pos).getTitle());
        tv_artist.setText(data.get(pos).getAuthor());

        return v;
    }
}
