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
}
