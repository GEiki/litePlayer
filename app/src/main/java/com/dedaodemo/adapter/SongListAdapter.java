package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;

/**
 * Created by guoss on 2018/5/11.
 */

public class SongListAdapter extends com.dedaodemo.adapter.BaseAdapter<SongListAdapter.MViewHolder, SongList> {


    public interface onMenuSongItemClickListener {
        void onMenuItemClick(MenuItem item, int position);
    }


    private onMenuSongItemClickListener onMenuItemClickListener;

    public SongListAdapter(Context context) {
        setmContext(context);
    }

    public onMenuSongItemClickListener getOnMenuItemClickListener() {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(onMenuSongItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public SongListAdapter.MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getmContext()).inflate(R.layout.item_cardview_layout, parent, false);
        MViewHolder mViewHolder = new MViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(SongListAdapter.MViewHolder holder, final int position) {
        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getmContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (onMenuItemClickListener == null)
                            return false;
                        onMenuItemClickListener.onMenuItemClick(item, position);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.tv_title.setText(getmData().get(position).getTitle());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() == null) {
                    return;
                }
                getOnItemClickListener().onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (getmData() == null)
            return 0;
        return getmData().size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView btn_more;
        View layout;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.list_card_title);
            btn_more = itemView.findViewById(R.id.btn_more);
            layout = itemView;

        }
    }

}
