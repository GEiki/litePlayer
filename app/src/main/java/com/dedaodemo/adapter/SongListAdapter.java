package com.dedaodemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

/**
 * Created by guoss on 2018/5/11.
 */

public class SongListAdapter extends BaseAdapter {

    public interface onMenuSongItemClickListener {
        public void onMenuItemClick(MenuItem item, int position);
    }

    private Context mContext;
    private ArrayList<SongList> data;
    private onMenuSongItemClickListener onMenuItemClickListener;

    public SongListAdapter(Context context, onMenuSongItemClickListener onMenuItemClickListener) {
        mContext = context;
        this.onMenuItemClickListener = onMenuItemClickListener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cardview_layout,parent,false);
        TextView tv_title = v.findViewById(R.id.list_card_title);
        tv_title.setText(data.get(position).getTitle());
        ImageView btn_more = (ImageView) v.findViewById(R.id.btn_more);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        onMenuItemClickListener.onMenuItemClick(item, position);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return v;
    }
}
