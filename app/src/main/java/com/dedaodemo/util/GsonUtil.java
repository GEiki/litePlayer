package com.dedaodemo.util;

import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by 01377578 on 2018/8/7.
 */

public class GsonUtil {
    private static Gson gson = new Gson();

    public static String bean2json(Object o, Class clazz) {
        if (o == null) {
            return null;
        }
        return gson.toJson(o);
    }

    public static Object json2Bean(String json, Class clazz) {
        if (json == null || TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }
}
