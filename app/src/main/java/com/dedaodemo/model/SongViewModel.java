package com.dedaodemo.model;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.Observable;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.dedaodemo.common.Constant;
import com.dedaodemo.common.HttpUtil;
import com.dedaodemo.MyApplication;
import com.dedaodemo.MyDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by guoss on 2018/4/26.
 */

public class SongViewModel extends ViewModel implements LifecycleObserver{

    private static final int LOAD_FINISH = 1;
    private static final int ADD_SONG_FINISH =2;
    private static final int REMOVE_SONG_FINISH=3;
    private static final int REMOVE_SONG_LIST_FINISH=4;
    private static final int ADD_SONG_LIST_FINISH=5;
    private static final int CUR_SONG_CHANGE=6;
    private static final int CUR_SONG_LIST_CHANGE=7;

    private


    Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_FINISH:{//更新数据
                    Log.i("DATA","update data");
                    songListsLiveData.setValue(songLists);
                    curSongListLiveData.setValue(curSongList);
                    curSongLiveData.setValue(curSong);
                    break;

                }
                case ADD_SONG_FINISH:{//更新数据
                    break;
                }
                case REMOVE_SONG_FINISH:{
                    break;
                }
                case REMOVE_SONG_LIST_FINISH:{
                    songListsLiveData.setValue(songLists);
                    break;
                }
                case ADD_SONG_LIST_FINISH:{
                    songListsLiveData.setValue(songLists);
                    break;
                }
                case CUR_SONG_CHANGE:{
                    curSongLiveData.setValue(curSong);
                    break;
                }case CUR_SONG_LIST_CHANGE:{
                    curSongListLiveData.setValue(curSongList);
                    break;
                }
            }
            return true;
        }
    });

    private MutableLiveData<ArrayList<SongList>> songListsLiveData;
    private MutableLiveData<Integer> curSongLiveData;
    private MutableLiveData<Integer> curSongListLiveData;
    private MutableLiveData<ArrayList<Item>> searchSongListData;
    private ArrayList<SongList> songLists = new ArrayList<>();
    private int curSong;
    private int curSongList;


    private MyDatabaseHelper databaseHelper;
    private Context mContext = MyApplication.getMyApplicationContext();

    public SongViewModel() {
        super();
        songListsLiveData = new MutableLiveData<>();
        songListsLiveData.setValue(new ArrayList<SongList>());
        curSongListLiveData = new MutableLiveData<>();
        curSongLiveData = new MutableLiveData<>();
        databaseHelper = new MyDatabaseHelper(mContext,MyDatabaseHelper.SONG_DATABASE_NAME,null,1);
        searchSongListData = new MutableLiveData<>();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner){

        curSongListLiveData.setValue(new Integer(0));
        curSongLiveData.setValue(new Integer(0));

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataFromDB();
                Log.i("DATA","loadData");
                myHandler.sendEmptyMessage(LOAD_FINISH);

            }
        }).start();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MUSIC",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CUR_SONG",curSongLiveData.getValue());
        editor.putInt("CUR_SONG_LIST",curSongListLiveData.getValue());
        editor.commit();

    }

    /**
     * 搜索歌曲
     * 关键词
     * @param key
     * 搜索平台
     * @param type
     * 返回允许为null
     * 网络请求为同步请求
     * */

    public void searchSong(String key, String type, final HttpUtil.ResponseHandler handler){

            try {
                JSONObject params = new JSONObject();
                params.put("TransCode", type);
                params.put("OpenId", "Test");
                JSONObject body = new JSONObject();
                body.put("key", key);
                params.put("Body", body);
                Log.i("RequestJSON", params.toString());
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.AsyncRequestByPOST(Constant.MUSIC_SEARCH_URL, params, new HttpUtil.ResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        handler.onSuccess(statusCode,response);
                    }

                    @Override
                    public void onFailure(int statusCode, String erroMsg) {
                        handler.onFailure(statusCode,erroMsg);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    public void updateSearchList(ArrayList<Item> songs){
        searchSongListData.setValue(songs);
    }
    public ArrayList<Item> getSearchList(){
        return searchSongListData.getValue();
    }

    public ArrayList<SongList> getSongLists() {
        return songListsLiveData.getValue();
    }

    public int getCurSong() {
        return curSongListLiveData.getValue();
    }

    public int getCurSongList() {
        return curSongListLiveData.getValue();
    }

    public SongList getSongList(String title){
        for(SongList songList : songListsLiveData.getValue()){
            if(songList.getTitle().equals(title)){
                return songList;
            }
        }

        return null;
    }

    public void setCurSong(int curSong) {
        this.curSongLiveData.setValue(curSong);
        this.curSong = curSong;
    }

    public void setCurSongList(int curSongList) {
        this.curSongListLiveData.setValue(curSongList);
        this.curSongList = curSongList;
    }


    /***
     * 添加歌曲到指定歌单
     */
    public void addSongToSongList(@NonNull final List<Item> songs, @NonNull final SongList target){

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            for(Item item:songs){
                target.addSong(item);
                ContentValues cv = new ContentValues();
                cv.put("id", item.getId());
                cv.put("title",item.getTitle());
                cv.put("author",item.getAuthor());
                cv.put("time",item.getTime());
                cv.put("path",item.getPath());
                cv.put("size",item.getSize());
                cv.put("type",item.getType());
                db.insertOrThrow(target.getTableName(),null,cv);
            }
            db.close();
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    target.update();
                }
            });

    }

    /**
     * 移除歌单中指定的歌曲
     * */
    public boolean removeSongFromSongList(@NonNull ArrayList<Item> songs,@NonNull final SongList target){
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                for (Item item : songs) {
                    boolean isExist = target.removeSong(item);//确保删除的歌曲存在
                    if(isExist){
                        String[] args = {item.getTitle()};
                        db.delete(target.getTableName(), "title=?", args);
                    }else {
                        db.close();
                        return  false;
                    }

                }
                db.close();
            if(songLists.contains(target)){
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        target.update();
                    }
                });

                myHandler.sendEmptyMessage(REMOVE_SONG_FINISH);
                return true;
            }else {
                return  false;
            }

    }

    /**
     * 移除歌单
     * */
    public boolean removeSongList(@NonNull SongList target){
            if(target == songLists.get(0)){
                return false;
            }
                if(songLists.get(curSongList) == target){
                    curSongList = 0;
                    curSong = 0;
                    myHandler.sendEmptyMessage(CUR_SONG_CHANGE);
                    myHandler.sendEmptyMessage(CUR_SONG_LIST_CHANGE);
                }
                songLists.remove(target);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                String[] args = {target.getTitle()};
                db.delete("song_lists","title=?",args);
                String sql = "drop table "+target.getTableName();
                db.execSQL(sql);
                db.close();
                myHandler.sendEmptyMessage(REMOVE_SONG_LIST_FINISH);
                return true;


    }
    /**
     * 创建歌单
     * */
    public void addSongList(@NonNull final SongList songList){
        if(TextUtils.isEmpty(songList.getTitle())){
            return;
        }
        songLists.add(songList);
        //将歌单信息添加到歌单信息表中，同时创建一个歌单table
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String string ="create table if not exists "+songList.getTableName()+"(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,PRIMARY KEY(id))";
        db.execSQL(string);
        ContentValues cv =new ContentValues();
        cv.put("id",songLists.size()-1);
        cv.put("title",songList.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年mm月dd日");
        Date date = new Date(System.currentTimeMillis());
        songList.setCreateDate(simpleDateFormat.format(date).toString());
        cv.put("time",simpleDateFormat.format(date).toString());
        cv.put("size",0);
        db.insert("song_lists",null,cv);
        db.close();
        myHandler.sendEmptyMessage(ADD_SONG_LIST_FINISH);


    }


    private void addSongListFromLocal(SongList songList){
        songLists.add(songList);

    }

    private void addSongToSongListFromLocal(final ArrayList<Item> arrayList,final SongList songList){
        for(Item item:arrayList){
            songList.addSong(item);
        }
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                songList.update();
            }
        });
    }

    private void loadDataFromDB(){
        if(databaseHelper!=null&&songListsLiveData.getValue().isEmpty()){
            SQLiteDatabase db=databaseHelper.getReadableDatabase();
            Cursor cursor=db.query("song_lists",null,null,null,null,null,"id",null);
            songLists = new ArrayList<>();
            while (cursor.moveToNext()){
                SongList songList = new SongList();
                songList.setTitle(cursor.getString(1));
                songList.setCreateDate(cursor.getString(2));
                songList.setSize(cursor.getString(3));
                songLists.add(songList);
            }
            cursor.close();

            //查询歌单table中的歌曲并添加到歌单中
            for(SongList list:songLists){
                Cursor cur = db.query(list.getTableName(),null,null,null,null,null,"id",null);
                ArrayList<Item> arrayList = new ArrayList<>();
                while (cur.moveToNext()){
                    Item a=new Item();
                    a.setTitle(cur.getString(1));//title
                    a.setAuthor(cur.getString(2));//Author
                    a.setTime(cur.getString(3));//time
                    a.setPath(cur.getString(4));//path
                    a.setSize(String.valueOf(cur.getInt(5)));//size
                    a.setType(cur.getInt(6));//type
                    arrayList.add(a);
                }
                Log.i("DATA",list.getTitle()+":"+String.valueOf(arrayList.size()));
                addSongToSongListFromLocal(arrayList,list);
                cur.close();
            }
            db.close();
        }
        if(!songListsLiveData.getValue().isEmpty()) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("MUSIC", MODE_PRIVATE);
            curSongListLiveData.setValue(sharedPreferences.getInt("CUR_SONG",0));
            curSongListLiveData.setValue(sharedPreferences.getInt("CUR_SONG_LIST",0));


        }
        myHandler.sendEmptyMessage(LOAD_FINISH);
    }
    public void observeSong(LifecycleOwner owner, Observer<ArrayList<Item>> observer,SongList target){
        target.getSongList().observe(owner,observer);
    }
    public void observeSongLists(LifecycleOwner owner,Observer<ArrayList<SongList>> observer){
        songListsLiveData.observe(owner,observer);
    }
    public void observeCurrentSong(LifecycleOwner owner,Observer<Integer> observer){
        curSongLiveData.observe(owner,observer);
    }
    public void observeCurrentSongList(LifecycleOwner owner,Observer<Integer> observer){
        curSongLiveData.observe(owner,observer);
    }


}
