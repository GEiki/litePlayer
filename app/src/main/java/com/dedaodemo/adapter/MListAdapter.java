package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.Item;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends com.dedaodemo.adapter.BaseAdapter<MListAdapter.MViewHolder, Item>
{


    public MListAdapter(Context mContext) {
        setmContext(mContext);

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

        public MViewHolder(View itemView) {
            super(itemView);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_title = itemView.findViewById(R.id.tv_title);
            layout = itemView.findViewById(R.id.ll_item);
        }
    }
}
