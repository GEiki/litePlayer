package com.dedaodemo.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.view.Gravity;
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
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.behavior.FooterBehavior;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

import java.util.Timer;

/**
 * 包含bottomPlaybar的fragment
 */
public abstract class BaseBottomFragment extends Fragment {

    public static final String BASE_BACK_STACK = "base_back_stack";
    public static final int MAX_PROGRESS = 1000;

    //    private RecyclerView recyclerView;
    private BottomSheetDialog bottomSheetDialog;
//    private BaseAdapter adapter;

    private RelativeLayout bottom_play_bar;
    private TextView iv_circle;
    private ImageView iv_play;
    private ImageView iv_pause;
    private ImageView iv_loop;
    private ImageView iv_single;
    private ImageView iv_random;
    private TextView tv_duration;
    private TextView tv_progress;
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



    public BaseBottomFragment() {
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseViewModel = ViewModelProviders.of(getActivity()).get(BaseViewModel.class);
        if (getArguments() != null) {
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getParentView();
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initBottomPlayBar((ViewGroup) view);
        initPlayDialog();
        observeLiveData();
        baseViewModel.initBottomBar();

        return view;
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



    /**
     * 该方法应该返回父布局，用于添加底层播放栏
     * */
    public abstract View getParentView();

    /**
     * 初始化播放窗口
     * **/
    private void initPlayDialog() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_play, null);
        view.findViewById(R.id.iv_next).setOnClickListener(onClickListener);
        view.findViewById(R.id.iv_pre).setOnClickListener(onClickListener);
        iv_loop = view.findViewById(R.id.iv_loop);
        iv_loop.setOnClickListener(onClickListener);
        iv_single = view.findViewById(R.id.iv_single);
        iv_single.setOnClickListener(onClickListener);
        iv_random = view.findViewById(R.id.iv_random);
        iv_random.setOnClickListener(onClickListener);
        iv_pause = view.findViewById(R.id.iv_pause);
        iv_pause.setOnClickListener(onClickListener);
        iv_play = view.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(onClickListener);
        tv_title = view.findViewById(R.id.tv_title);
        tv_aritist = view.findViewById(R.id.tv_artist);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_progress = view.findViewById(R.id.tv_progress);
        seekBar = view.findViewById(R.id.seekBar);
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
        bottomSheetDialog.setContentView(view);


    }

    /**
     * 初始化底层播放栏
     */
    private void initBottomPlayBar(ViewGroup v) {

        initOnClickListener();
        bottom_play_bar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.bottom_play_bar, null, false);
        bottom_play_bar.setVisibility(View.VISIBLE);

        //设置behavior响应滑动
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(getContext(), 65f));
        layoutParams.gravity = Gravity.BOTTOM;
        bottom_play_bar.setBackground(getResources().getDrawable(R.color.transparent, null));
        layoutParams.setBehavior(new FooterBehavior());
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

        bottom_play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        v.addView(bottom_play_bar);



    }




    /**
     * 添加观察者
     */
    private void observeLiveData() {
        baseViewModel.observeData(BaseViewModel.CURRENT_SONG_DATA, this, new Observer<Item>() {//注册当前歌曲观察者
            @Override
            public void onChanged(@Nullable Item item) {
                if (item == null) {
                    return;
                }
                tv_title.setText(item.getTitle());
                tv_title_expand.setText(item.getTitle());
                tv_aritist.setText(item.getAuthor());
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
                    ToastUtil.showShort(getActivity(), "出了点错误，请重试或换一首歌曲");
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
                        ToastUtil.showShort(getActivity(), "已切换到单曲循环");
                        changeMode(Constant.MOED_SINGLE_RECYCLE);
                        break;
                    }
                    case R.id.iv_single: {
                        iv_single.setVisibility(View.GONE);
                        iv_random.setVisibility(View.VISIBLE);
                        changeMode(Constant.MODE_RANDOM);
                        ToastUtil.showShort(getActivity(), "已切换到随机播放");
                        break;
                    }
                    case R.id.iv_random: {
                        iv_random.setVisibility(View.GONE);
                        iv_loop.setVisibility(View.VISIBLE);
                        changeMode(Constant.MODE_LIST_RECYCLE);
                        ToastUtil.showShort(getActivity(), "已切换到列表循环");
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
    public void onDestroyView() {
        timer.cancel();
        baseViewModel.removeObserves(this);
        super.onDestroyView();
    }

}
