package com.dedaodemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.LrcBean;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.common.Constant;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.impl.SearchModelImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by guoss on 2018/7/12.
 */

public class Util {




    /**
     * 歌曲标题去除歌手
     * */
    public static String getPureSongName(String title) {
        if (!title.contains("-")) {
            return title;
        }
        String[] strs = title.split("-");
        String t2 = strs[1];
        String[] strs2 = t2.split("\\[");
        return strs2[0].trim();
    }

    /**
     * byte转换为MB
     * */
    public static double bytes2megaBytes(double size) {

        double res = size/1024/1024;
        BigDecimal b = new BigDecimal(res);
        res = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

        return res;
    }


    /**
     * dip转换为px
     */
    public static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    /**
     * duration转换为分秒
     */
    public static String durationToformat(long duration) {
        int second = (int) duration / 1000;
        int min = second / 60;
        int sec = second % 60;
        StringBuilder sb = new StringBuilder();
        sb.append(min).append(":").append(sec);
        return sb.toString();
    }

    /**
     * 转换表名
     */
    public static String getTableName(String title) {
        if (title != null) {
            char[] chars = title.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                stringBuilder.append(String.valueOf((int) chars[i]));
            }
            Log.i("TABLENAME", stringBuilder.toString());
            return "songlist_" + stringBuilder.toString();
        }
        return null;
    }

    /**
     * 沉浸式状态栏
     * */
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.transparent,null));
        }
    }

    /**
     * 设置状态栏颜色
     * */
    public static void setStatusBarColor(Activity activity, int color) {
        // 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            View view = new View(activity);
            ViewGroup.LayoutParams params = new ViewGroup
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
            view.setLayoutParams(params);
            view.setBackgroundColor(color);

            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(view);

            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelOffset(statusBarHeightId);
    }



    /**
     * 获取当前时间
     */
    public static String getCurrentFormatTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 根据时长计算进度
     * */
    public static int calculateProgress(int position, long duration, int max) {
        double t = (double) position / (double) duration;
        return (int) (max * t);
    }

    /**
     * 进度转换
     */
    public static int progressToposition(int progress, long duration, int max) {
        double t = (double) progress / (double) max;
        return (int) (duration * t);
    }

    /**
     * 判断网络状态
     */
    public static boolean NetWorkState() {
        boolean isConnected = false;
        Context context = MyApplication.getMyApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo network : networkInfos) {
                if (network.getState() == NetworkInfo.State.CONNECTED) {
                    isConnected = true;
                }
            }
        } else {
            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    isConnected = true;
                }
            }
        }

        return isConnected;
    }

    /**
     * 异步设置图片到imageView
     * */
    public static void setSongImgToImageView(Item item,Context context,ImageView imageView){
        setPic(null,imageView,context);
        downloadPic(item,imageView,context);
    }

    public static void setPic(String url, final ImageView imageView,Context context) {
        final RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(context)
                .load(R.drawable.default_songlist_background2)
                .apply(requestOptions)
                .into(imageView);
        if (url != null && !url.isEmpty()) {
                Glide.with(context)
                        .asDrawable()
                        .apply(requestOptions)
                        .load(url).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
        }
    }

    private static void downloadPic(final Item item, final ImageView imageView, final Context context) {
        ISearchModel searchModel = new SearchModelImpl();
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(item.getTitle());
        searchBean.setSearchType(Constant.TYPE_QQ);
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
                            Item song = list.get(0);
                            setPic(song.getPic(),imageView,context);
                            item.setPic(song.getPic());
                            DatabaseUtil.updateItem(item);
                        } else {
                            setPic(null,imageView,context);
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
