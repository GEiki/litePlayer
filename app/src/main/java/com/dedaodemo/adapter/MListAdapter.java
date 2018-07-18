package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.Item;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends com.dedaodemo.adapter.BaseAdapter<MListAdapter.MViewHolder, Item>
{
    public interface OnMenuItemOnClickListener {
        public void onMenuItemClick(MenuItem item, int position);

    }

    private OnMenuItemOnClickListener onMenuItemOnClickListener;

    public MListAdapter(Context mContext) {
        setmContext(mContext);

    }

    public OnMenuItemOnClickListener getOnItemAddClickListener() {
        return onMenuItemOnClickListener;
    }

    public void setOnItemAddClickListener(OnMenuItemOnClickListener onItemAddClickListener) {
        this.onMenuItemOnClickListener = onItemAddClickListener;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getmContext()).inflate(R.layout.misc_item, parent, false);
        MViewHolder mViewHolder = new MViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, final int position) {
        holder.tv_title.setText(getmData().get(position).getTitle());
        holder.tv_artist.setText(getmData().get(position).getAuthor());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() == null)
                    return;
                getOnItemClickListener().onItemClick(v, position);
            }
        });
        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getmContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.song_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (onMenuItemOnClickListener == null)
                            return false;
                        onMenuItemOnClickListener.onMenuItemClick(item, position);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (getmData() == null)
            return 0;
        return getmData().size();
    }

    @Override
    public long getItemId(int pos){
        return pos;
    }


    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        LinearLayout layout;
        ImageView iv_add;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_title = itemView.findViewById(R.id.tv_title);
            layout = itemView.findViewById(R.id.ll_item);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }


}
