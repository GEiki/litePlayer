package com.dedaodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 01377578 on 2018/7/12.
 */

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter<T> {

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private List<D> mData = new ArrayList<>();

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<D> getmData() {
        return mData;
    }

    public void setmData(List<D> mData) {
        this.mData = mData;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {

        return null;
    }


    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
