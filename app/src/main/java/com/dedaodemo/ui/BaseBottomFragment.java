package com.dedaodemo.ui;


import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;

/**
 * 包含bottomPlaybar的fragment
 */
public abstract class BaseBottomFragment extends Fragment {

    private ListView listView;
    private Toolbar toolbar;
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
    private BaseContract.Presenter baseViewModel;
    private Handler handler = new Handler();
    private boolean progress_flag = true;
    private boolean isThreadDown = true;

    private Thread progressThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (SongManager.getInstance().isPlaying() && progress_flag && !Thread.interrupted()) {
                baseViewModel.requestProgress(new SongManager.IProgressCallback() {
                    @Override
                    public void onResponse(int position, long duration) {
                        final int progress = (int) (100 * (position / duration));
                        seekBar.setProgress(progress);
                        progress_flag = true;
                    }
                });
                //callback未被回调的时候不再发起请求
                progress_flag = false;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Thread.interrupted())
                    isThreadDown = true;
            }
        }
    });


    public BaseBottomFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseViewModel = getViewModel();
        if (getArguments() != null) {
        }
    }

    protected abstract BaseViewModel getViewModel();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_bottom_bar, container, false);
        if (speacialFlag()) {
            view = getBaseBottomBarView();
        }
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        listView = (ListView) view.findViewById(R.id.list_view);
        initBottomPlayBar(view);
        observeLiveData();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            baseViewModel.initBottomBar();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        baseViewModel.initBottomBar();
        super.onResume();
    }

    public void setAdapter(ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }
    /**
     * 返回true时父类会调用getBaseBottomFlag
     * */
    protected abstract boolean speacialFlag();

    /**
     * speacialFlag返回true时被调用
     */
    protected abstract View getBaseBottomBarView();

    /**
     * 初始化底层播放栏
     * */
    private void initBottomPlayBar(View v) {
//        View v =LayoutInflater.from(getContext()).inflate(R.layout.bottom_play_bar,(ViewGroup)getView(),false);
        initOnClickListener();
        bottom_play_bar = (ConstraintLayout)v.findViewById(R.id.ll_bottom_play_bar);
        bottom_play_bar.setVisibility(View.VISIBLE);
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
                baseViewModel.seekTo(progress);
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


    }

    /**
     * 添加观察者
     */
    private void observeLiveData() {
        baseViewModel.observeCurrentSong(getActivity(), new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                tv_title_hide.setText(item.getTitle());
                tv_title_expand.setText(item.getTitle());
                tv_artist_hide.setText(item.getAuthor());
                tv_title_expand.setText(item.getAuthor());
            }
        });
        baseViewModel.observeCurrentSongList(getActivity(), new Observer<SongList>() {
            @Override
            public void onChanged(@Nullable SongList songList) {
                //歌单变化
            }
        });
        baseViewModel.observePlayMode(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //播放模式变化
            }
        });
        baseViewModel.observePlayState(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isPlaying) {
                if (isPlaying) {
                    btn_play_hide.setVisibility(View.GONE);
                    btn_pause_hide.setVisibility(View.VISIBLE);
                    btn_play_expand.setVisibility(View.GONE);
                    btn_pause_expand.setVisibility(View.VISIBLE);
                } else {
                    btn_play_hide.setVisibility(View.VISIBLE);
                    btn_pause_hide.setVisibility(View.GONE);
                    btn_play_expand.setVisibility(View.VISIBLE);
                    btn_pause_expand.setVisibility(View.GONE);
                }
                //播放状态变化
            }
        });
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
                        baseViewModel.rePlay();
                        if (isThreadDown) {
                            progressThread.start();
                            isThreadDown = false;
                        }

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
                        baseViewModel.rePlay();
                        if (isThreadDown) {
                            progressThread.start();
                            isThreadDown = false;
                        }
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

    protected abstract void play(int pos);

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
    public void onStop() {
        progressThread.interrupt();
        super.onStop();
    }
}
