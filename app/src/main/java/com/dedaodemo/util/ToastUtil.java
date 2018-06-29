package com.dedaodemo.util;

import android.widget.Toast;

import com.dedaodemo.MyApplication;

/**
 * Created by 01377578 on 2018/6/28.
 */

public class ToastUtil {
    public static void showShort(String msg) {
        Toast.makeText(MyApplication.getMyApplicationContext(), msg, Toast.LENGTH_SHORT);
    }
}
