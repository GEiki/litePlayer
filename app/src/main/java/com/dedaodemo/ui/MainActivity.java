package com.dedaodemo.ui;


import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dedaodemo.R;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.MusicServiceManager;
import com.dedaodemo.util.Util;


public class MainActivity extends BaseActivity
{




    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        //初始化服务
        MusicServiceManager.getInstance().init();

    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

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
