package com.dedaodemo.ui;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.model.SongList;

import java.util.ArrayList;

/**
 * 包含bottomPlaybar的fragment
 */
public abstract class BaseBottomFragment extends Fragment {

    private ConstraintLayout bottom_layout_hide;
    private ConstraintLayout bottom_layout_expand;
    private ConstraintLayout bottom_play_bar;
    private TextView iv_circle;
    private TextView tv_title_hide;
    private TextView tv_artist_hide;
    private TextView tv_title_expand;
    private TextView tv_artist_expand;
    private ImageButton btn_play_hide;
    private ImageButton btn_pause_hide;
    private ImageButton btn_next_hide;
    public ImageButton btn_play_expand;
    public ImageButton btn_pause_expand;
    private ImageButton btn_next_expand;
    private ImageButton btn_pre_expand;
    private SeekBar seekBar;
    BottomSheetBehavior bottomSheetBehavior;
    private View.OnClickListener onClickListener;
    private boolean isHidding;


    public BaseBottomFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();
        initBottomPlayBar();
    }

    /**
     * 初始化底层播放栏
     * */
    private void initBottomPlayBar(){
        View v =LayoutInflater.from(getContext()).inflate(R.layout.bottom_play_bar,(ViewGroup)getView(),false);
        initOnClickListener();
        bottom_play_bar = (ConstraintLayout)v.findViewById(R.id.ll_bottom_play_bar);
        bottom_play_bar.setVisibility(View.GONE);
        bottom_layout_expand = (ConstraintLayout)v.findViewById(R.id.bottom_layout_expand);
        bottom_layout_hide = (ConstraintLayout)v.findViewById(R.id.bottom_layout_hide);
        iv_circle =(TextView) v.findViewById(R.id.iv_circle);
        tv_artist_expand = (TextView)v.findViewById(R.id.tv_artist_expand);
        tv_artist_hide = (TextView)v.findViewById(R.id.tv_artist_hide);
        tv_title_expand = (TextView)v.findViewById(R.id.tv_title_expand);
        tv_title_hide= (TextView)v.findViewById(R.id.tv_title_hide);
        btn_pause_hide = (ImageButton)v.findViewById(R.id.btn_pause);
        btn_pause_hide.setOnClickListener(onClickListener);
        btn_play_hide = (ImageButton)v.findViewById(R.id.btn_play);
        btn_play_hide.setOnClickListener(onClickListener);
        btn_next_hide = (ImageButton)v.findViewById(R.id.btn_next);
        btn_next_hide.setOnClickListener(onClickListener);
        btn_pause_expand = (ImageButton)v.findViewById(R.id.btn_pause_expand);
        btn_pause_expand.setOnClickListener(onClickListener);
        btn_play_expand = (ImageButton)v.findViewById(R.id.btn_play_expand);
        btn_play_expand.setOnClickListener(onClickListener);
        btn_next_expand = (ImageButton)v.findViewById(R.id.btn_next_expand);
        btn_next_expand.setOnClickListener(onClickListener);
        btn_pre_expand = (ImageButton)v.findViewById(R.id.btn_pre_expand);
        btn_pre_expand.setOnClickListener(onClickListener);
        seekBar = (SeekBar)v.findViewById(R.id.seekBar3);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onSeekBarProgressChange(seekBar,progress,fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bottom_play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        bottom_layout_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_play_bar);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    if(isHidding){
                        ObjectAnimator tranXAnimator = ObjectAnimator.ofFloat(iv_circle,"translationX",0);
                        tranXAnimator.setDuration(800);
                        tranXAnimator.start();
                        bottom_layout_expand.setVisibility(View.GONE);
                        bottom_layout_hide.setVisibility(View.VISIBLE);
                    }else {
                        ObjectAnimator tranXAnimator = ObjectAnimator.ofFloat(iv_circle,"translationX",100);
                        tranXAnimator.setDuration(800);
                        tranXAnimator.start();
                        bottom_layout_expand.setVisibility(View.VISIBLE);
                        bottom_layout_hide.setVisibility(View.GONE);
                    }
                }
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    bottom_layout_expand.setVisibility(View.VISIBLE);
                    bottom_layout_hide.setVisibility(View.GONE);
                    isHidding = true;


                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    bottom_layout_expand.setVisibility(View.GONE);
                    bottom_layout_hide.setVisibility(View.VISIBLE);
                    isHidding = false;

                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("Bottom",String.valueOf(slideOffset));
            }
        });
        ((ViewGroup)getView()).addView(v);
    }
    private void initOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_play:{
                        btn_play_hide.setVisibility(View.GONE);
                        btn_pause_hide.setVisibility(View.VISIBLE);
                        btn_play_expand.setVisibility(View.GONE);
                        btn_pause_expand.setVisibility(View.VISIBLE);
                        play();
                        break;
                    }
                    case R.id.btn_pause:{
                        pause();
                        btn_play_hide.setVisibility(View.VISIBLE);
                        btn_pause_hide.setVisibility(View.GONE);
                        btn_play_expand.setVisibility(View.VISIBLE);
                        btn_pause_expand.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.btn_pause_expand:{
                        pause();
                        btn_play_hide.setVisibility(View.VISIBLE);
                        btn_pause_hide.setVisibility(View.GONE);
                        btn_play_expand.setVisibility(View.VISIBLE);
                        btn_pause_expand.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.btn_play_expand:{

                            btn_play_hide.setVisibility(View.GONE);
                            btn_pause_hide.setVisibility(View.VISIBLE);
                            btn_play_expand.setVisibility(View.GONE);
                            btn_pause_expand.setVisibility(View.VISIBLE);
                            seekBar.setProgress(0);
                            play();
                        break;
                    }
                    case R.id.btn_next:{
                        next();
                        seekBar.setProgress(0);

                        break;
                    }
                    case R.id.btn_next_expand:{
                       next();
                       seekBar.setProgress(0);

                        break;
                    }
                    case R.id.btn_pre_expand:{
                        pre();
                        seekBar.setProgress(0);
                        break;
                    }
                    default:break;

                }
            }
        };
    }
    protected void setBottomBarVisibility(int visibility){
        bottom_play_bar.setVisibility(visibility);
    }
    protected abstract void play();
    protected abstract void pause();
    protected abstract void pre();
    protected abstract void next();
    protected abstract void onSeekBarProgressChange(SeekBar seekBar, int progress, boolean fromUser);


}
