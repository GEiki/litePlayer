package com.dedaodemo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.impl.SearchModelImpl;
import com.dedaodemo.util.DatabaseUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by guoss on 2018/5/11.
 */

public class SongListAdapter extends com.dedaodemo.adapter.BaseAdapter<SongListAdapter.MViewHolder, SongList> {

    private final String TAG = "SongListAdapter";
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
    public void onBindViewHolder(final SongListAdapter.MViewHolder holder, final int position) {

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
        SongList songList = getmData().get(position);
        if (songList.getSongList() != null && songList.getSongList().size() > 0) {
            holder.tv_count.setText(String.valueOf(songList.getSongList().size())+"首");
            Item item = songList.getSongList().get(0);
            if (!TextUtils.isEmpty(item.getPic())) {
                setPic(item.getPic(),holder.iv_background);
            } else {
                downloadPic(item,holder.iv_background);
            }
        } else if (songList.getSongList() != null && songList.getSongList().size() == 0) {
            final RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(true);

            Glide.with(getmContext())
                    .load(R.drawable.default_songlist_background)
                    .apply(requestOptions)
                    .into(holder.iv_background);
        }




    }

    @Override
    public int getItemCount() {
        if (getmData() == null)
            return 0;
        return getmData().size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_count;
        View layout;
        ImageView iv_background;

        public MViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_count = itemView.findViewById(R.id.tv_count);
            iv_background = itemView.findViewById(R.id.iv_pic);
            layout = itemView;

        }
    }

    private void setPic(String url, final ImageView imageView) {
        final RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true);
        Glide.with(getmContext())
                .asDrawable()
                .apply(requestOptions)
                .load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Glide.with(getmContext())
                        .load(R.drawable.default_songlist_background)
                        .apply(requestOptions)
                        .into(imageView);
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    private void downloadPic(final Item item, final ImageView imageView) {
        Log.i(TAG,"start download img >>>"+item.getTitle());
        ISearchModel searchModel = new SearchModelImpl();
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(item.getTitle());
        searchBean.setSearchType(Constant.TYPE_WY);
        searchModel.searchSongOnline(searchBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Item>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Item> list) {
                if (list != null && list.size() > 0) {
                    Log.i(TAG,"download success");
                    Item song = list.get(0);
                    setPic(song.getPic(),imageView);
                    item.setPic(song.getPic());
                    DatabaseUtil.updateItem(item);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
