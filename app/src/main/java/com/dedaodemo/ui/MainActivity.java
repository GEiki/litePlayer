package com.dedaodemo.ui;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;

import com.dedaodemo.MyApplication;
import com.dedaodemo.MyDatabaseHelper;
import com.dedaodemo.R;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.MusicServiceManager;
import com.dedaodemo.common.SongManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private static final int FIRST_LAUNCH_FLAG = 0;
    private static final String LAUNCH_FLAG = "launch";
    private int launch_flags;



    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SP_KEY_LANUCH, MODE_PRIVATE);
        launch_flags = sharedPreferences.getInt(LAUNCH_FLAG, FIRST_LAUNCH_FLAG);
        sharedPreferences.edit().putInt(LAUNCH_FLAG, launch_flags + 1).commit();

        //初次启动加载数据库
        if (launch_flags == FIRST_LAUNCH_FLAG) {
            MyDatabaseHelper helper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, 1);
            SQLiteDatabase db = helper.getWritableDatabase();
            SongList songList = new SongList();
            songList.setTitle("全部歌曲");
            songList.setSongList(new ArrayList<Item>());
            String string = "create table if not exists " + songList.getTableName() + "(id int,title varchar(20),author varchar(10),time varchar(20),path varchar(50),size int,type int,PRIMARY KEY(id))";
            db.execSQL(string);
            ContentValues cv = new ContentValues();
            cv.put("id", 0);
            cv.put("title", songList.getTitle());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年mm月dd日");
            Date date = new Date(System.currentTimeMillis());
            songList.setCreateDate(simpleDateFormat.format(date).toString());
            cv.put("time", simpleDateFormat.format(date).toString());
            cv.put("size", 0);
            db.insert("song_lists", null, cv);
            db.close();
        }


        /**
         * 添加fragment
         * */
        final FragmentManager fm = getSupportFragmentManager();
        SheetListFragment sheetListFragment = (SheetListFragment) fm.findFragmentById(R.id.fragment_container);
        if (sheetListFragment == null) {
            sheetListFragment = SheetListFragment.newInstance();
            Explode explode = new Explode();
            explode.setDuration(800);
            sheetListFragment.setEnterTransition(explode);
            sheetListFragment.setReturnTransition(explode);
            sheetListFragment.setAllowEnterTransitionOverlap(true);
            sheetListFragment.setAllowReturnTransitionOverlap(true);
            fm.beginTransaction().add(R.id.fragment_container, sheetListFragment, SongListFragment.TAG_SONG_LIST_FRAGMENT).commit();
        }



        String[] permission={android.Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this,permission,1);


    }

    @Override
    public void onBackPressed() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment songFragment = fragmentManager.findFragmentByTag(SongFragment.SONG_FRAGMENT);
//        if(songFragment != null){
//            Fragment songListFragment = fragmentManager.findFragmentByTag(SongListFragment.TAG_SONG_LIST_FRAGMENT);
//            fragmentManager.beginTransaction().show(songListFragment).remove(songFragment).commit();
//            Log.i("Fragment","remove");
//            return;
//        }
        super.onBackPressed();
    }


    @Override
    public void onDestroy() {
        MusicServiceManager.getInstance().unBindMusicService();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SongManager.getInstance().savePlayState();
        super.onPause();
    }
}
