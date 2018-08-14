package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.Util;
import com.tubb.smrv.SwipeHorizontalMenuLayout;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends com.dedaodemo.adapter.BaseAdapter<MListAdapter.MViewHolder, Item>
{
    public interface OnMenuItemOnClickListener {
        public void onMenuItemClick(View view, int position);

    }

    private OnMenuItemOnClickListener onMenuItemOnClickListener;
    private int menuId;

    public MListAdapter(Context mContext) {
        setmContext(mContext);

    }

    public OnMenuItemOnClickListener getOnItemAddClickListener() {
        return onMenuItemOnClickListener;
    }

    public void setOnItemAddClickListener(OnMenuItemOnClickListener onItemAddClickListener) {
        this.onMenuItemOnClickListener = onItemAddClickListener;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getmContext()).inflate(R.layout.item_swipe, parent, false);
        MViewHolder mViewHolder = new MViewHolder(view);
        return mViewHolder;
    }


    @Override
    public void onBindViewHolder(MViewHolder holder, final int position) {
        Item item = getmData().get(position);
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
        holder.tv_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuItemOnClickListener != null) {
                    onMenuItemOnClickListener.onMenuItemClick(v, position);
                }
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuItemOnClickListener != null) {
                    onMenuItemOnClickListener.onMenuItemClick(v, position);
                }
            }
        });

        SwipeHorizontalMenuLayout swipeLayout = (SwipeHorizontalMenuLayout) holder.layout;
        swipeLayout.smoothCloseMenu(500);

        if (!Util.NetWorkState() && item.getType() == Constant.INTERNET_MUSIC && !MyApplication.getProxyServer().isCached(item.getPath())) {
            holder.tv_title.setTextColor(getmContext().getResources().getColor(android.R.color.darker_gray, null));
            holder.tv_artist.setTextColor(getmContext().getResources().getColor(android.R.color.darker_gray, null));
            holder.layout.setEnabled(false);
        }


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
        View layout;
        TextView tv_delete;
        TextView tv_move;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_title = itemView.findViewById(R.id.tv_title);
            layout = itemView;
            tv_delete = itemView.findViewById(R.id.tv_delete);
            tv_move = itemView.findViewById(R.id.tv_move);
        }
    }


}
