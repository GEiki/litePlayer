package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;

/**
 * Created by 01377578 on 2018/7/17.
 */

public class ChooseSheetAdapter extends com.dedaodemo.adapter.BaseAdapter<ChooseSheetAdapter.MViewHolder, SongList> {

    public ChooseSheetAdapter(Context context) {
        setmContext(context);
    }

    @Override
    public ChooseSheetAdapter.MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getmContext()).inflate(R.layout.item_choose_sheet, null);
        ChooseSheetAdapter.MViewHolder viewHolder = new MViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChooseSheetAdapter.MViewHolder holder, final int position) {
        holder.tv_title.setText(getmData().get(position).getTitle());
        holder.holdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(v, position);
                }
            }
        });
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        View holdView;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            holdView = itemView;
        }
    }

    @Override
    public int getItemCount() {
        if (getmData() == null) {
            return 0;
        }
        return getmData().size();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }
}
