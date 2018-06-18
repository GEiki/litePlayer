package com.dedaodemo.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.dedaodemo.R;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.BlurTransformation;
public class PlayActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton btn_pre;
    private ImageButton btn_next;
    private ImageButton btn_pause;
    private ImageButton btn_play;
    private ImageView iv_cover;
    private ImageView iv_blur;
    private RelativeLayout cl_play;
    private SeekBar mSeekBar;
    private boolean isChanging=false;
    private boolean isMoving=false;
    private float y;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        btn_pause=(ImageButton)findViewById(R.id.btn_pause_c);
        btn_play=(ImageButton)findViewById(R.id.btn_play_c);
        btn_next=(ImageButton)findViewById(R.id.btn_next);
        btn_pre=(ImageButton)findViewById(R.id.btn_pre);
        iv_cover=(ImageView)findViewById(R.id.iv_cover);
        iv_blur=(ImageView)findViewById(R.id.iv_blur);
        cl_play=(RelativeLayout)findViewById(R.id.cl_play);
        Glide.with(this).load(R.drawable.song_image)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(150)))
                .into(iv_blur);
        Glide.with(this).load(R.drawable.song_image)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_cover);
        mSeekBar=(SeekBar)findViewById(R.id.seekBar);
//        if(MainActivity.mMediaPlayer==null){
//            Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT);
//            finish();
//        }
//        mSeekBar.setMax(MainActivity.mMediaPlayer.getDuration());
        //进度条线程
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
//                    mSeekBar.setProgress(MainActivity.mMediaPlayer.getCurrentPosition());
                    try
                    {
                        Thread.sleep(1000);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //监听进度条滑动事件
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                    isChanging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
//                MainActivity.mMediaPlayer.seekTo(seekBar.getProgress());
                isChanging=false;
            }
        });

        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        iv_blur.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        y=event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:isMoving=true;break;
                    case MotionEvent.ACTION_UP:{
                        if (isMoving&&(event.getY()-y>0)){
                            onBackPressed();
                            isMoving=false;
                        }
                        break;
                    }
                    default:break;
                }
                return true;
            }
        });

        /*
        * 设置过渡动画
        * */
        initTransition();

    }

    private void initTransition(){
        Slide slide = new Slide();
        slide.setDuration(500L);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_next:
                Toast.makeText(this,"没有更多歌曲",Toast.LENGTH_SHORT).show();break;
            case R.id.btn_pre:
                Toast.makeText(this,"没有更多歌曲",Toast.LENGTH_SHORT).show();break;
            case R.id.btn_play_c:{
//                if (MainActivity.mMediaPlayer!=null){
//                    MainActivity.mMediaPlayer.start();
//                    btn_play.setVisibility(View.GONE);
//                    btn_pause.setVisibility(View.VISIBLE);
//
//                }else {
//                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();break;
//                }
                break;
            }
            case R.id.btn_pause_c:{
//                if (MainActivity.mMediaPlayer!=null){
//                    MainActivity.mMediaPlayer.pause();
//                    btn_pause.setVisibility(View.GONE);
//                    btn_play.setVisibility(View.VISIBLE);
//
//                }else {
//                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();break;
//                }
                break;
            }
        }
    }

    @Override
    protected void onResume()
    {
//        if(MainActivity.mMediaPlayer!=null){
//            if(!MainActivity.mMediaPlayer.isPlaying()){
//                btn_play.setVisibility(View.VISIBLE);
//                btn_pause.setVisibility(View.GONE);
//            }else {
//                btn_play.setVisibility(View.GONE);
//                btn_pause.setVisibility(View.VISIBLE);
//            }
//        }
        super.onResume();
    }
    @Override
    public void finish(){
        overridePendingTransition(0,R.anim.bottom_out);
        super.finish();
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(0,R.anim.bottom_out);
        super.onPause();
    }
}
