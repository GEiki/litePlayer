package com.dedaodemo.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;

import com.dedaodemo.R;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.MusicServiceManager;

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

        //初次启动初始化数据库
        if (launch_flags == FIRST_LAUNCH_FLAG) {

        }


        /**
         * 添加fragment
         * */
        final FragmentManager fm = getSupportFragmentManager();
        SheetListFragment sheetListFragment = (SheetListFragment) fm.findFragmentById(R.id.fragment_container);
        if (sheetListFragment == null) {
            sheetListFragment = SheetListFragment.newInstance();
            //activity是否从service启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (Constant.ACTION_N_FROM_SERVICE.equals(action)) {
                    Bundle b = new Bundle();
                    b.putBoolean(Constant.ACTION_N_FROM_SERVICE, true);
                    sheetListFragment.setArguments(b);
                }
            }
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
    protected void onResume() {
        super.onResume();
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
        super.onPause();
    }
}
