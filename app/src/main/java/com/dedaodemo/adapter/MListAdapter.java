package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.Util;
import com.tubb.smrv.SwipeHorizontalMenuLayout;
import com.tubb.smrv.SwipeMenuLayout;
import com.tubb.smrv.listener.SwipeSwitchListener;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends com.dedaodemo.adapter.BaseAdapter<MListAdapter.MViewHolder, Item>
{
    public interface OnMenuItemOnClickListener {
        public void onMenuItemClick(MenuItem item, int position);

    }

    private boolean isPlayList;
    private OnMenuItemOnClickListener onMenuItemOnClickListener;
    private int menuId;
    private int curSongIndex;
    private SwipeHorizontalMenuLayout openSwipeLayout;

    public MListAdapter(Context mContext) {
        setmContext(mContext);

    }

    public void setIsPlayList(boolean playList) {
        isPlayList = playList;
    }

    public OnMenuItemOnClickListener getOnItemAddClickListener() {
        return onMenuItemOnClickListener;
    }

    public void setCurSongIndex(int curSongIndex) {
        this.curSongIndex = curSongIndex;
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
        View view = LayoutInflater.from(getmContext()).inflate(R.layout.misc_item, parent, false);
        MViewHolder mViewHolder = new MViewHolder(view);
        return mViewHolder;
    }




    @Override
    public void onBindViewHolder(final MViewHolder holder, final int position) {
        Item item = getmData().get(position);
        holder.tv_title.setText(getmData().get(position).getTitle());
        holder.tv_artist.setText(getmData().get(position).getAuthor());
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() == null)
                    return;
                getOnItemClickListener().onItemClick(v, holder.getAdapterPosition());
            }
        });
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化更多菜单
                PopupMenu popupMenu = new PopupMenu(getmContext(),v);
                popupMenu.inflate(R.menu.song_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        onMenuItemOnClickListener.onMenuItemClick(item,position);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.iv_list_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() == null)
                    return;
                getOnItemClickListener().onItemClick(v, position);
            }
        });
        if (isPlayList) {
            holder.iv_more.setVisibility(View.GONE);
            holder.iv_list_delete.setVisibility(View.VISIBLE);
            if (curSongIndex == position) {
                holder.tv_title.setTextColor(getmContext().getColor(android.R.color.holo_green_light));
            } else {
                holder.tv_title.setTextColor(getmContext().getColor(android.R.color.black));
            }

        } else {
            holder.iv_more.setVisibility(View.VISIBLE);
            holder.iv_list_delete.setVisibility(View.GONE);
        }

        //若无网络且本地无缓存，item变为不可按
        if (!Util.NetWorkState() && item.getType() == Constant.INTERNET_MUSIC && !MyApplication.getProxyServer().isCached(item.getPath())) {
            holder.tv_title.setTextColor(getmContext().getResources().getColor(android.R.color.darker_gray, null));
            holder.tv_artist.setTextColor(getmContext().getResources().getColor(android.R.color.darker_gray, null));
            holder.ll_item.setEnabled(false);
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

    public void closeSwipeLayout() {
        if (openSwipeLayout != null) {
            openSwipeLayout.smoothCloseMenu(500);
        }
    }


    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        ImageView iv_more;
        ImageView iv_list_delete;
        LinearLayout ll_item;
        View layout;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_more = itemView.findViewById(R.id.iv_more);
            ll_item = itemView.findViewById(R.id.ll_item);
            iv_list_delete = itemView.findViewById(R.id.iv_list_delete);
            layout = itemView;
        }
    }


}
