package com.dedaodemo.ui;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import android.support.v7.app.AppCompatActivity;


import com.dedaodemo.service.MusicService;
import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements SongListFragment.OnFragmentInteractionListener
{



    //使用静态内部类防止内存泄漏
    private static class MyServiceConnection implements ServiceConnection {
        public WeakReference<MainActivity> activityWeakReference;
        public MyServiceConnection(WeakReference<MainActivity> activity){
            activityWeakReference=activity;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder=(MusicService.MyBinder)service;
            activityWeakReference.get().musicService=myBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private class MusicReceiver extends BroadcastReceiver {
        public MusicReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("RECEIVER","onReceiver");

            if(intent.getAction().equals(MusicService.ACTION_NEXT)){
                int index = intent.getIntExtra("index",0);
//                curSong = index;

            }else if(intent.getAction().equals(MusicService.ACTION_FINISH)){


            }else if(intent.getAction().equals(MusicService.ACTION_PRE)){
                Log.i("RECEIVER","pre");
                int index = intent.getIntExtra("index",0);
//                curSong = index;
            }
        }
    }



    private MusicService musicService;
    private MusicReceiver musicReceiver;
    ServiceConnection conn=new MyServiceConnection(new WeakReference<MainActivity>(MainActivity.this));


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);

        /**
         * 添加fragment
         * */
        final FragmentManager fm = getSupportFragmentManager();
        SongListFragment songListFragment = (SongListFragment) fm.findFragmentById(R.id.fragment_container);
        if(songListFragment == null){
            songListFragment = SongListFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container,songListFragment,SongListFragment.TAG_SONG_LIST_FRAGMENT).commit();
        }

        /**
         * 注册广播
         * */
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_FINISH);
        intentFilter.addAction(MusicService.ACTION_NEXT);
        intentFilter.addAction(MusicService.ACTION_PRE);
        registerReceiver(musicReceiver,intentFilter);

        /**
         * 绑定服务
         * */
        Intent intent  = new Intent(MainActivity.this,MusicService.class);
        bindService(intent,conn,BIND_AUTO_CREATE);

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
    public void changeFragment(String TAG) {

    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        unregisterReceiver(musicReceiver);
        super.onDestroy();
    }

    @Override
    public void play(SongList list, int index) {
        musicService.play(index,list.getSongList().getValue());
    }

    @Override
    public boolean next() {

        return musicService.next();
    }

    @Override
    public boolean pre() {
        return musicService.pre();
    }

    @Override
    public void pause() {
        musicService.pause();

    }

    @Override
    public void replay() {
        musicService.rePlay();
    }

    @Override
    public int getProgress() {
        return musicService.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return musicService.getDuration();
    }

    @Override
    public void seekTo(int position) {
        musicService.seekTo(position);
    }
}
