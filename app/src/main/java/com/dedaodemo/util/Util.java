package com.dedaodemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 01377578 on 2018/7/12.
 */

public class Util {
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
}
