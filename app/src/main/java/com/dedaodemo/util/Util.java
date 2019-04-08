package com.dedaodemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dedaodemo.MyApplication;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.LrcBean;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.impl.SearchModelImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
}
