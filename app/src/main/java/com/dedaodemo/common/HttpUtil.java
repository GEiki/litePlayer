package com.dedaodemo.common;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by guoss on 2017/11/12.
 */

public class HttpUtil
{
    private static HttpUtil instance;
    private OkHttpClient mClient;

    public HttpUtil(){
        mClient=new OkHttpClient();
    }

    public static HttpUtil getInstance(){
        if(instance==null){
            instance=new HttpUtil();
        }
        return instance;
    }
    public interface ResponseHandler{
        public void onSuccess(int statusCode, JSONObject response);
        public void onFailure(int statusCode,String erroMsg);

    }
    /**
     * 异步POST请求
     * */
    public void AsyncRequestByPOST(final String url, final JSONObject params,final ResponseHandler handler ){
//        FormEncodingBuilder builder=new FormEncodingBuilder();
//        if(params!=null&&params.size()>0){
//            for(Map.Entry<String,String> entry:params.entrySet()){
//                builder.add(entry.getKey(),entry.getValue());
//            }
//        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON,params.toString());
        final Request request=new Request.Builder().url(url).post(requestBody).build();
        Call call=mClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                handler.onFailure(000,e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                    String str=response.body().string();

                try
                {
                    JSONObject jsonObject=new JSONObject(str);
                    handler.onSuccess(response.code(),jsonObject);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 同步POST请求
     * */
    public JSONObject SyncRequestByPOST(final String url, final JSONObject params){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON,params.toString());
        final Request request=new Request.Builder().url(url).post(requestBody).build();
        Call call=mClient.newCall(request);
        try{
            Response response =call.execute();
            String str=response.body().string();
            JSONObject jsonObject=new JSONObject(str);
            return jsonObject;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
