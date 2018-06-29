package com.dedaodemo.model;

import android.util.Log;

import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
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
                                    item.setURL(song.getString("url"));
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
}
