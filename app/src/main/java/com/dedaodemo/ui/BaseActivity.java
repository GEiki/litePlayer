package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.behavior.FooterBehavior;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

public class BaseActivity extends AppCompatActivity {
    public static final int MAX_PROGRESS = 1000;

    private FrameLayout bottom_play_bar;
    private RelativeLayout ll_bottom_play_bar;
    private LinearLayout bottom_play;
    private LinearLayout ll_control_group;
    private ImageView iv_circle;
    private ImageView iv_play;
    private ImageView iv_pause;
    private ImageView iv_loop;
    private ImageView iv_single;
    private ImageView iv_random;
    private TextView tv_duration;
    private TextView tv_progress;
    private TextView tv_title_expand;
    private TextView tv_artist_expand;
    public ImageButton btn_play_expand;
    public ImageButton btn_pause_expand;
    private ImageButton btn_next_expand;
    private SeekBar seekBar;

    private View.OnClickListener onClickListener;
    private BaseContract.Presenter baseViewModel;
    private FooterBehavior behavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private boolean isBottomBarExpand;
    private int oldState = 4;





    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        initBottomPlayBar();
        initPlayDialog();
        observeLiveData();
        baseViewModel.initBottomBar();
    }

    /**
     * 初始化播放窗口
     * **/
    private void initPlayDialog() {
        ll_bottom_play_bar = bottom_play_bar.findViewById(R.id.ll_bottom_play_bar);
        bottom_play = bottom_play_bar.findViewById(R.id.bottom_play);
        bottom_play.findViewById(R.id.iv_next).setOnClickListener(onClickListener);
        bottom_play.findViewById(R.id.iv_pre).setOnClickListener(onClickListener);
        iv_loop = bottom_play.findViewById(R.id.iv_loop);
        iv_loop.setOnClickListener(onClickListener);
        iv_single = bottom_play.findViewById(R.id.iv_single);
        iv_single.setOnClickListener(onClickListener);
        iv_random = bottom_play.findViewById(R.id.iv_random);
        iv_random.setOnClickListener(onClickListener);
        iv_pause = bottom_play.findViewById(R.id.iv_pause);
        iv_pause.setOnClickListener(onClickListener);
        iv_play = bottom_play.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(onClickListener);
        tv_duration = bottom_play.findViewById(R.id.tv_duration);
        tv_progress = bottom_play.findViewById(R.id.tv_progress);
        seekBar = bottom_play.findViewById(R.id.seekBar);
        seekBar.setMax(MAX_PROGRESS);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String dur = baseViewModel.getCurPlaySong().getValue().getTime();
                    int pos = Util.progressToposition(progress, Long.valueOf(dur), MAX_PROGRESS);
                    baseViewModel.seekTo(pos);
                    tv_progress.setText(Util.durationToformat(pos));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isBottomBarExpand) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    private double getStatusBarHeight(Context context){
        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    /**
     * 初始化底层播放栏
     */
    private void initBottomPlayBar() {
        initOnClickListener();
        double statusHeight = getStatusBarHeight(this);
        bottom_play_bar = findViewById(R.id.bottom_bar);
        bottom_play_bar.setPadding(0,(int) statusHeight,0,0);
        bottom_play_bar.invalidate();

        //设置behavior响应滑动
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        behavior = new FooterBehavior();
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {//收起
                    ll_bottom_play_bar.setBackground(getResources().getDrawable(R.drawable.shape_rectangle_with_radius,null));
                    ll_control_group.setVisibility(View.VISIBLE);
                    tv_title_expand.requestFocus();
                    isBottomBarExpand = false;
                    oldState = newState;
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {//展现
                    ll_bottom_play_bar.setBackground(getResources().getDrawable(R.color.white,null));
                    isBottomBarExpand = true;
                    Item song = baseViewModel.getCurPlaySong().getValue();
                    ll_control_group.setVisibility(View.GONE);
                    oldState = newState;
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    if (oldState == BottomSheetBehavior.STATE_COLLAPSED) {//收起变为展现
                        ll_bottom_play_bar.setBackground(getResources().getDrawable(R.color.white,null));
                        ll_control_group.setVisibility(View.GONE);
                    } else if (oldState == BottomSheetBehavior.STATE_EXPANDED) {//展现变为收起
                        ll_bottom_play_bar.setBackground(getResources().getDrawable(R.drawable.shape_rectangle_with_radius,null));
                        ll_control_group.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };
        behavior.setBottomSheetCallback(bottomSheetCallback);
        behavior.setPeekHeight(Util.dip2px(this,105f));
        layoutParams.setBehavior(behavior);
        bottom_play_bar.setLayoutParams(layoutParams);

        iv_circle = bottom_play_bar.findViewById(R.id.iv_circle);
        tv_artist_expand = bottom_play_bar.findViewById(R.id.tv_artist_expand);
        tv_title_expand = bottom_play_bar.findViewById(R.id.tv_title_expand);
        btn_pause_expand = bottom_play_bar.findViewById(R.id.btn_pause_expand);
        btn_pause_expand.setOnClickListener(onClickListener);
        btn_play_expand = bottom_play_bar.findViewById(R.id.btn_play_expand);
        btn_play_expand.setOnClickListener(onClickListener);
        btn_next_expand = bottom_play_bar.findViewById(R.id.btn_next_expand);
        btn_next_expand.setOnClickListener(onClickListener);
        ll_control_group = bottom_play_bar.findViewById(R.id.ll_control_group);
        bottom_play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }





    /**
     * 添加观察者
     */
    private void observeLiveData() {
        //當前播放歌曲發生變化
        baseViewModel.observeData(BaseViewModel.CURRENT_SONG_DATA, this, new Observer<Item>() {//注册当前歌曲观察者
            @Override
            public void onChanged(@Nullable Item item) {
                if (item == null) {
                    return;
                }
                tv_title_expand.setText(item.getTitle());
                tv_artist_expand.setText(item.getAuthor());
                String time = baseViewModel.getCurPlaySong().getValue().getTime();

                long duration;
                if (time != null) {
                    duration = Long.valueOf(baseViewModel.getCurPlaySong().getValue().getTime());
                } else {
                    duration = 0;
                }

                tv_duration.setText(Util.durationToformat(duration));
            }
        });
        baseViewModel.observeData(BaseViewModel.CURRENT_LIST_DATA, this, new Observer<SongList>() {
            @Override
            public void onChanged(@Nullable SongList songList) {
                //歌单变化
            }
        });
        baseViewModel.observeData(BaseViewModel.PLAY_MODE_DATA, this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //播放模式变化
            }
        });
        baseViewModel.observeData(BaseViewModel.IS_PLAYING_DATA, this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isPlaying) {
                if (isPlaying == null) {
                    return;
                }
                if (isPlaying) {
                    iv_play.setVisibility(View.GONE);
                    iv_pause.setVisibility(View.VISIBLE);
                    btn_play_expand.setVisibility(View.GONE);
                    btn_pause_expand.setVisibility(View.VISIBLE);
                } else {
                    iv_play.setVisibility(View.VISIBLE);
                    iv_pause.setVisibility(View.GONE);
                    btn_play_expand.setVisibility(View.VISIBLE);
                    btn_pause_expand.setVisibility(View.GONE);
                }
                //播放状态变化
            }
        });
        baseViewModel.observeData(BaseViewModel.ERROR_FLAGS_DATA, this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean errorFlags) {
                if (errorFlags == null)
                    return;
                if (errorFlags) {
                    ToastUtil.showShort(BaseActivity.this, "出了点错误，请重试或换一首歌曲");
                }
            }
        });

        baseViewModel.observeData(BaseViewModel.POSTION, this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                String dur = baseViewModel.getCurPlaySong().getValue().getTime();
                if (dur == null)
                    return;
                int pos = Util.calculateProgress(progress, Long.valueOf(dur), MAX_PROGRESS);
                seekBar.setProgress(pos);
                tv_progress.setText(Util.durationToformat(progress));
            }
        });
    }

    private void initOnClickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.iv_play: {
                        iv_play.setVisibility(View.GONE);
                        iv_pause.setVisibility(View.VISIBLE);
                        baseViewModel.rePlay();

                        break;
                    }
                    case R.id.iv_pause: {
                        pause();
                        iv_play.setVisibility(View.VISIBLE);
                        iv_pause.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.btn_pause_expand:{
                        pause();
                        btn_play_expand.setVisibility(View.VISIBLE);
                        btn_pause_expand.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.btn_play_expand:{
                        btn_play_expand.setVisibility(View.GONE);
                        btn_pause_expand.setVisibility(View.VISIBLE);
                        baseViewModel.rePlay();
                        break;
                    }
                    case R.id.iv_next: {
                        next();
                        seekBar.setProgress(0);

                        break;
                    }
                    case R.id.btn_next_expand:{
                        next();
                        seekBar.setProgress(0);

                        break;
                    }
                    case R.id.iv_pre: {
                        pre();
                        seekBar.setProgress(0);
                        break;
                    }
                    case R.id.iv_loop: {
                        iv_loop.setVisibility(View.GONE);
                        iv_single.setVisibility(View.VISIBLE);
                        ToastUtil.showShort(getApplicationContext(), "已切换到单曲循环");
                        changeMode(Constant.MOED_SINGLE_RECYCLE);
                        break;
                    }
                    case R.id.iv_single: {
                        iv_single.setVisibility(View.GONE);
                        iv_random.setVisibility(View.VISIBLE);
                        changeMode(Constant.MODE_RANDOM);
                        ToastUtil.showShort(getApplicationContext(), "已切换到随机播放");
                        break;
                    }
                    case R.id.iv_random: {
                        iv_random.setVisibility(View.GONE);
                        iv_loop.setVisibility(View.VISIBLE);
                        changeMode(Constant.MODE_LIST_RECYCLE);
                        ToastUtil.showShort(getApplicationContext(), "已切换到列表循环");
                        break;
                    }
                    default:break;

                }
            }
        };
    }

    public void play(SongList songList, Item item) {
        baseViewModel.playSong(songList, item);
    }

    private void pause() {
        baseViewModel.pause();
    }

    private void pre() {
        baseViewModel.preSong();
    }

    private void next() {
        baseViewModel.nextSong();
    }

    private void changeMode(String mode) {
        baseViewModel.setPlayMode(mode);
    }


    @Override
    protected void onDestroy() {
        baseViewModel.removeObserves(this);
        super.onDestroy();
    }
}
