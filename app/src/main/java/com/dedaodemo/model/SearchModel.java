package com.dedaodemo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dedaodemo.MyApplication;
import com.dedaodemo.MyDatabaseHelper;
import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/27.
 */

public class SearchModel implements SearchContract.Model {
    private static final String TRANSCODE = "TransCode";
    private static final String OPENID = "OpenId";
    private static final String KEY = "key";
    private static final String BODY = "Body";

    public static final String SEARCH_FAIL = "search_fail";
    public static final String REQUEST_FAIL = "request_fail";
    public static final String SERVICE_ERRO = "service_erro";

    private SearchContract.ViewModel viewModel;

    public SearchModel(SearchContract.ViewModel viewModel) {
        this.viewModel = viewModel;

    }


    /**
     * 搜索歌曲
     * 异步请求
     */
    @Override
    public void searchSongOnline(SearchBean bean) {
        try {
            JSONObject params = new JSONObject();
            params.put(TRANSCODE, bean.getSearchType());
            params.put(OPENID, "Test");
            JSONObject body = new JSONObject();
            body.put(KEY, bean.getKey());
            params.put(BODY, body);
            Log.i("RequestJSON", params.toString());
            HttpUtil httpUtil = new HttpUtil();

            httpUtil.AsyncRequestByPOST(Constant.MUSIC_SEARCH_URL, params, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(int statusCode, JSONObject response) {
                    try {
                        if (viewModel == null) {
                            Log.e("SearchModel", "viewModel can not be NULL");
                            return;
                        }
                        Log.i("SearchResult", response.toString());
                        if (response != null && response.toString().contains("ResultCode")) {
                            if (response.getInt("ResultCode") == 1) {
                                ArrayList<Item> itemList = new ArrayList<>();
                                JSONArray jsonArray = response.getJSONArray("Body");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject song = jsonArray.getJSONObject(i);
                                    Item item = new Item();
                                    item.setTitle(song.getString("title"));
                                    item.setAuthor(song.getString("author"));
                                    item.setPath(song.getString("url"));
                                    item.setLrc(song.getString("lrc"));
                                    item.setPic(song.getString("pic"));
                                    item.setType(Item.INTERNET_MUSIC);
                                    itemList.add(item);
                                }
                                viewModel.onSearchSuccess(itemList);
                            } else {
                                viewModel.onSearchFail(SEARCH_FAIL);
                                //搜索失败
                            }
                        } else {
                            viewModel.onSearchFail(REQUEST_FAIL);
                            //请求失败
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, String erroMsg) {
                    viewModel.onSearchFail(SERVICE_ERRO);
                    //服务器出错
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveStateFromSearch(SongList songList) {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

        String sql = "drop table if exists " + songList.getTableName();
        db.execSQL(sql);
        String string = "create table if not exists " + songList.getTableName() + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,PRIMARY KEY(id))";
        db.execSQL(string);
        ArrayList<Item> items = songList.getSongList();
        int i = 0;
        for (Item item : items) {
            ContentValues cv = new ContentValues();
            cv.put("id", i);
            cv.put("title", item.getTitle());
            cv.put("author", item.getAuthor());
            cv.put("time", item.getTime());
            cv.put("path", item.getPath());
            cv.put("size", item.getSize());
            cv.put("type", item.getType());
            db.insertOrThrow(songList.getTableName(), null, cv);
            i++;
        }
        db.close();
    }

    @Override
    public SongList loadStateFromSearch() {
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        SongList songList = new SongList();
        songList.setTitle(Constant.SEARCH_SONG_LIST);
        Cursor cur = db.query(songList.getTableName(), null, null, null, null, null, "id", null);
        while (cur.moveToNext()) {
            Item a = new Item();
            a.setTitle(cur.getString(1));//title
            a.setAuthor(cur.getString(2));//Author
            a.setTime(cur.getString(3));//time
            a.setPath(cur.getString(4));//path
            a.setSize(String.valueOf(cur.getInt(5)));//size
            a.setType(cur.getInt(6));//type
            songList.addSong(a);
        }
        cur.close();
        db.close();

        return songList;
    }
}
