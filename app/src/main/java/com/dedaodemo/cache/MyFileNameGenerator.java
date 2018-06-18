package com.dedaodemo.cache;

import android.net.Uri;

import com.danikula.videocache.file.FileNameGenerator;

/**
 * Created by guoss on 2018/5/6.
 */

public class MyFileNameGenerator implements FileNameGenerator {
    @Override
    public String generate(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter("id");
//        String name = id+".mp3";
        return id;
    }
}
