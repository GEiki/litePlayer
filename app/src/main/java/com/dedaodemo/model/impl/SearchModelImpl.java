package com.dedaodemo.model.impl;

import android.util.Log;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.HttpUtil;
import com.dedaodemo.model.ISearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by 01377578 on 2018/7/27.
 */

public class SearchModelImpl implements ISearchModel {

    private static final String TRANSCODE = "TransCode";
    private static final String OPENID = "OpenId";
    private static final String KEY = "key";
    private static final String BODY = "Body";

    public static final String SEARCH_FAIL = "search_fail";
    public static final String REQUEST_FAIL = "request_fail";
    public static final String SERVICE_ERRO = "service_erro";

    @Override
    public Observable<List<Item>> searchSongOnline(final SearchBean bean) {
        return Observable.create(new ObservableOnSubscribe<List<Item>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<List<Item>> emitter) throws Exception {
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

                                Log.i("SearchResult", response.toString());
                                if (response != null && response.toString().contains("ResultCode")) {
                                    if (response.getInt("ResultCode") == 1 && !response.getString("Body").equals("null")) {
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
                                            item.setType(Constant.INTERNET_MUSIC);
                                            long time = song.getInt("time");
                                            time *= 1000;
                                            item.setTime(String.valueOf(time));
                                            itemList.add(item);
                                        }
                                        emitter.onNext(itemList);
                                        emitter.onComplete();
                                    } else {
                                        //搜索失败
                                        emitter.onError(new Exception("search fail"));

                                    }
                                } else {
                                    //请求失败
                                    emitter.onError(new Exception("request fail"));

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, String erroMsg) {
                            //服务器出错
                            emitter.onError(new Exception("server problem"));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
