package com.dedaodemo.util;

import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.LrcBean;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.HttpUtil;
import com.dedaodemo.model.ISearchModel;
import com.dedaodemo.model.impl.SearchModelImpl;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.Telephony.TextBasedSmsColumns.BODY;
import static com.dedaodemo.common.Constant.OPENID;
import static com.dedaodemo.common.Constant.TRANSCODE;

public class LrcUtil {
    private static final String TAG = "GET_LRC";
    /**
     * 解析歌词文件
     * 该方法存在网络请求需要进行异步
     * */
    public static ArrayList<LrcBean> getLrc (Item song) {


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path+"/"+"liteplayer/lrc");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
                File lrcFile = new File(file.getPath()+"/"+song.getTitle());
                if (lrcFile.exists()) {
                    FileReader reader = new FileReader(lrcFile);
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    Log.i(TAG,lrcFile.getPath());
                    return buildLrc(sb.toString());
                } else {
                    Log.i(TAG,"搜索中");
                    SearchBean searchBean = new SearchBean();
                    searchBean.setKey(song.getTitle());
                    searchBean.setSearchType(Constant.TYPE_QQ);
                    String url = searchLrc(searchBean,song);
                    return downloadLrcFile(song,url);
                }

        }catch (Exception e) {
            Log.e("GET_LRC",e.getMessage());
        }

        return null;

    }
    /**
     * return:lrc url
     * */
    private static String searchLrc(SearchBean bean,Item music) {
        try {
            JSONObject params = new JSONObject();
            params.put(TRANSCODE, bean.getSearchType());
            params.put(OPENID, "Test");
            JSONObject body = new JSONObject();
            body.put(Constant.KEY, bean.getKey());
            params.put(Constant.BODY, body);
            Log.i("RequestJSON", params.toString());
            HttpUtil httpUtil = new HttpUtil();
            JSONObject response = httpUtil.SyncRequestByPOST(Constant.MUSIC_SEARCH_URL,params);
            Log.i("Key",bean.getKey());
            Log.i("SearchResult", response.toString());
            if (response != null && response.toString().contains("ResultCode")) {
                if (response.getInt("ResultCode") == 1) {
                    JSONArray jsonArray = response.getJSONArray("Body");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject song = jsonArray.getJSONObject(i);
                        if (song.get("title").equals(music.getTitle())
                                && music.getAuthor() == null) {
                            music.setLrc(song.getString("lrc"));
                            return music.getLrc();
                        } else if (song.get("title").equals(music.getTitle()) && song.get("author").equals(music.getAuthor())){
                            music.setLrc(song.getString("lrc"));
                            return music.getLrc();
                        }
                    }
                    Log.e(TAG,"歌词搜索失败");
                    return null;
                } else {
                    //搜索失败
                    Log.e(TAG,"歌词搜索失败");
                    return null;

                }
            } else {
                //请求失败
                Log.e(TAG,"歌词搜索失败");
                return null;

            }

        } catch (Exception e) {
            Log.e(TAG,"歌词搜索失败");
            return null;
        }
    }

    public static ArrayList<LrcBean> downloadLrcFile(Item song,String url) {
        try {
            HttpUtil httpUtil = new HttpUtil();
            String str = httpUtil.syncRequestByGet(url);
            if (str == null) {
                return null;
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.i(TAG,path);
            //检查文件路径是否存在，不存在则创建
            File dic = new File(path+"/"+"liteplayer/lrc");
            if (!dic.exists()) {
                if(!dic.mkdirs()) {
                    return null;
                }
            }
            Log.i(TAG,dic.getPath());
            //创建文件
            File file = new File(dic.getPath()+"/"+song.getTitle());
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return null;
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(str);
            fileWriter.flush();
            fileWriter.close();
            return buildLrc(str);

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param string 完整歌词字符串
     * */
    private static ArrayList<LrcBean> buildLrc(String string) {
        ArrayList<LrcBean> arrayList = new ArrayList<>();
        String[] strings = string.split("\n");
        LrcBean pre = null;
        for(String str:strings) {
            LrcBean bean = buildLrcBean(str,pre);
            if(pre != null && !TextUtils.isEmpty(pre.getLrc())) {
                arrayList.add(pre);
            }
            pre = bean;
        }
        pre.setEndTime(Long.MAX_VALUE);
        arrayList.add(pre);
        return arrayList;
    }

    /**
     * @param lrc 单句歌词字符串
     * */

    private static LrcBean buildLrcBean(String lrc,LrcBean pre) {
        String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(lrc);
        while (matcher.find()) {
            String min = matcher.group(1);
            String sec = matcher.group(2);
            String mill = matcher.group(3);
            LrcBean bean = new LrcBean();
            bean.setLrc(lrc.substring(matcher.end()));
            bean.setStartTime(getTime(min,sec,mill));
            if (pre != null) {
                pre.setEndTime(bean.getStartTime());
            }
            return bean;

        }

        return null;



    }

    private static long getTime(String min,String sec,String mill) {
        int m = Integer.valueOf(min);
        int s = Integer.valueOf(sec);
        int mi = Integer.valueOf(mill);

        long time = m * 60 * 1000 + s * 1000 + mi;
        return time;
    }
}
