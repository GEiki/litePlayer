package com.dedaodemo.ui;


import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 包含bottomPlaybar的fragment
 */
public abstract class BaseBottomFragment extends Fragment {

    public static final String BASE_BACK_STACK = "base_back_stack";

    private RecyclerView recyclerView;
    private BottomSheetDialog bottomSheetDialog;
    private Toolbar toolbar;
    private BaseAdapter adapter;

    private RelativeLayout bottom_play_bar;
    private TextView iv_circle;
    private ImageView iv_play;
    private ImageView iv_pause;
    private TextView tv_title;
    private TextView tv_aritist;

    private TextView tv_title_expand;
    private TextView tv_artist_expand;

    public ImageButton btn_play_expand;
    public ImageButton btn_pause_expand;
    private ImageButton btn_next_expand;
    private SeekBar seekBar;
    private View.OnClickListener onClickListener;
    private BaseContract.Presenter baseViewModel;
    private Handler handler = new Handler();
    private Timer timer = new Timer(true);
    private TimerTask progressTask = new TimerTask() {
        @Override
        public void run() {
                baseViewModel.requestProgress(new SongManager.IProgressCallback() {
                    @Override
                    public void onResponse(int position, long duration) {
                        seekBar.setMax((int) duration);
                        seekBar.setProgress(position);
                    }
                });
        }
    };


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
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initBottomPlayBar(view);
        //开始监听进度
        timer.schedule(progressTask, 1500, 1500);
        initPlayDialog();
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

    public void setAdapter(BaseAdapter adapter) {
        recyclerView.setAdapter(adapter);
        this.adapter = adapter;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    /**
     * fragment跳转
     */
    public void showFragment(Fragment showFragment, Fragment hideFragment) {
        Explode explode = new Explode();
        explode.setDuration(500);
        showFragment.setEnterTransition(explode);
        showFragment.setAllowEnterTransitionOverlap(true);
        showFragment.setAllowReturnTransitionOverlap(true);
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, showFragment, SearchFragment.TAG)
                .addToBackStack(BASE_BACK_STACK)
                .show(showFragment)
                .hide(hideFragment)
                .commit();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setOnItemClickListener(final BaseAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
    }
    /**
     * 返回true时父类会调用getBaseBottomFlag
     * */
    protected abstract boolean speacialFlag();

    /**
     * speacialFlag返回true时被调用,用于需要在该baseBottomFragment外嵌套其他布局的情况
     * 不需要时让specialFlag默认返回false且getBaseBottomBarView方法不需要实现
     */
    protected abstract View getBaseBottomBarView();

    /**
     * 初始化播放窗口
     * **/
    private void initPlayDialog() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_play, null);
        view.findViewById(R.id.iv_next).setOnClickListener(onClickListener);
        view.findViewById(R.id.iv_pre).setOnClickListener(onClickListener);
        iv_pause = view.findViewById(R.id.iv_pause);
        iv_pause.setOnClickListener(onClickListener);
        iv_play = view.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(onClickListener);
        tv_title = view.findViewById(R.id.tv_title);
        tv_aritist = view.findViewById(R.id.tv_artist);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    baseViewModel.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bottomSheetDialog.setContentView(view);


    }

    /**
     * 初始化底层播放栏
     */
    private void initBottomPlayBar(View v) {

        initOnClickListener();
        bottom_play_bar = v.findViewById(R.id.ll_bottom_play_bar);
        bottom_play_bar.setVisibility(View.VISIBLE);

        iv_circle = v.findViewById(R.id.iv_circle);
        tv_artist_expand = v.findViewById(R.id.tv_artist_expand);
        tv_title_expand = v.findViewById(R.id.tv_title_expand);
        btn_pause_expand = v.findViewById(R.id.btn_pause_expand);
        btn_pause_expand.setOnClickListener(onClickListener);
        btn_play_expand = v.findViewById(R.id.btn_play_expand);
        btn_play_expand.setOnClickListener(onClickListener);
        btn_next_expand = v.findViewById(R.id.btn_next_expand);
        btn_next_expand.setOnClickListener(onClickListener);

        bottom_play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();

            }
        });



    }

    public void hideBottomBar() {
        bottom_play_bar.setVisibility(View.GONE);
    }

    public void showBottomBar() {
        bottom_play_bar.setVisibility(View.VISIBLE);
    }


    /**
     * dip转换为px
     * */
    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }


    /**
     * 添加观察者
     */
    private void observeLiveData() {
        baseViewModel.observeCurrentSong(getActivity(), new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                tv_title.setText(item.getTitle());
                tv_title_expand.setText(item.getTitle());
                tv_aritist.setText(item.getAuthor());
                tv_artist_expand.setText(item.getAuthor());
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
    public void onDestroyView() {
        timer.cancel();
        super.onDestroyView();
    }
}
