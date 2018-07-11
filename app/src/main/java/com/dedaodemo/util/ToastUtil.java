package com.dedaodemo.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 01377578 on 2018/6/28.
 */

public class ToastUtil {
    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
