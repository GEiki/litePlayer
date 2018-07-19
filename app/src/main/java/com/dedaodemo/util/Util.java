package com.dedaodemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

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
}
