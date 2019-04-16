package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.LrcBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.behavior.FooterBehavior;
import com.dedaodemo.common.Constant;
import com.dedaodemo.ui.widget.LrcView;
import com.dedaodemo.util.LrcUtil;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

import java.util.ArrayList;
import java.util.List;

public class
BaseActivity extends AppCompatActivity  implements BaseAdapter.OnItemClickListener {
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
    private ImageButton btn_list;
    private SeekBar seekBar;
    private BottomSheetDialog playlistDialog;
    private ImageView iv_list_delete;
    private LinearLayout ll_order;
    private LinearLayout ll_random;
    private LinearLayout ll_single;
    private RecyclerView rlv_playlist;
    private TextView tv_close;
    private LrcView lrcView;

    private View.OnClickListener onClickListener;
    private BaseContract.Presenter baseViewModel;
    private FooterBehavior behavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private boolean isBottomBarExpand;
    private int oldState = 4;
    private ArrayList<LrcBean> lrcBeans;
    private MListAdapter playlistAdapter;
    private Handler threadHandler;
    private HandlerThread handlerThread;

    private static final int SET_TIME = 0;
    private static final int GET_LRC = 1;
    private static final int MSG_GET_LRC = 2;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
           switch (msg.what) {
               case SET_TIME: {
                   lrcView.setCurrentTime(msg.arg1);
                   break;
               }
               case GET_LRC: {
                   lrcBeans = (ArrayList<LrcBean>)(msg.obj);
                   break;
               }
               default:break;
           }
            return true;
        }
    });





    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        initHandlerThread();
        initBottomPlayBar();
        initPlayDialog();
        observeLiveData();
        baseViewModel.initBottomBar();
        initPlaylistDialog();
    }

    private void initHandlerThread() {
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GET_LRC:{
                        Item item = (Item) msg.obj;
                        ArrayList<LrcBean> lrc = LrcUtil.getLrc(item);
                        Message message = new Message();
                        message.what = GET_LRC;
                        message.obj = lrc;
                        handler.sendMessage(message);
                        break;
                    }
                    default:break;
                }
            }
        };
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

        lrcView = bottom_play.findViewById(R.id.lrc_view);
        seekBar.setMax(MAX_PROGRESS);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && baseViewModel.getCurPlaySong().getValue() != null) {
                    String dur = baseViewModel.getCurPlaySong().getValue().getTime();
                    int pos = Util.progressToposition(progress, Long.valueOf(dur), MAX_PROGRESS);
                    baseViewModel.seekTo(pos);
                    lrcView.setCurrentTime(pos);
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
                    initLrcView();
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    if (oldState == BottomSheetBehavior.STATE_COLLAPSED) {//收起变为展现
                        ll_bottom_play_bar.setBackground(getResources().getDrawable(R.color.white,null));
                        ll_control_group.setVisibility(View.GONE);
                        initLrcView();
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
        btn_list = bottom_play_bar.findViewById(R.id.btn_list);
        btn_list.setOnClickListener(onClickListener);
        btn_play_expand = bottom_play_bar.findViewById(R.id.btn_play_expand);
        btn_play_expand.setOnClickListener(onClickListener);
        btn_list = bottom_play_bar.findViewById(R.id.btn_list);
        btn_list.setOnClickListener(onClickListener);
        ll_control_group = bottom_play_bar.findViewById(R.id.ll_control_group);
        bottom_play_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.ll_item: {
                SongList songList = baseViewModel.getPlaylist();
                baseViewModel.playSong(songList,songList.getSongList().get(position));
                playlistAdapter.setCurSongIndex(position);
                playlistAdapter.notifyItemChanged(position);
                playlistAdapter.notifyItemRangeChanged(0,playlistAdapter.getItemCount());

                break;
            }
            case R.id.iv_list_delete: {
                baseViewModel.removeSongFromPlaylist(position);
                playlistAdapter.notifyItemRemoved(position);
                //使用notifyItemRemove后必须调用notifyItemRangeChange
                playlistAdapter.notifyItemRangeChanged(position,playlistAdapter.getItemCount());
                playlistAdapter.notifyItemChanged(position);
                Log.i("Test",String.valueOf(playlistAdapter.getItemCount()));
                break;
            }
        }
    }

    private void updateAdapter() {
        if (baseViewModel.getPlaylist() != null) {
            MListAdapter adapter = new MListAdapter(this);
            adapter.setmData(baseViewModel.getPlaylist().getSongList());
            adapter.setIsPlayList(true);
            adapter.setOnItemClickListener(this);
            Item item = baseViewModel.getCurPlaySong().getValue();
            if (item == null) {
                adapter.setCurSongIndex(-1);
            } else {
                int index = baseViewModel.getPlaylist().getSongList().indexOf(item);
                adapter.setCurSongIndex(index);
            }

            rlv_playlist.setAdapter(adapter);
            playlistAdapter = adapter;
        }

    }


    /**
     * 初始化播放列表窗口
     * */
    private void initPlaylistDialog() {
        playlistDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_playlist,null);
        ll_order = view.findViewById(R.id.ll_order);
        ll_random = view.findViewById(R.id.ll_random);
        ll_single = view.findViewById(R.id.ll_single);
        iv_list_delete = view.findViewById(R.id.iv_delete_all);
        tv_close = view.findViewById(R.id.tv_close);
        rlv_playlist = view.findViewById(R.id.recycler_view);
        rlv_playlist.setLayoutManager(new LinearLayoutManager(this));
        iv_list_delete.setOnClickListener(onClickListener);
        tv_close.setOnClickListener(onClickListener);
        ll_order.setOnClickListener(onClickListener);
        ll_random.setOnClickListener(onClickListener);
        ll_single.setOnClickListener(onClickListener);
        playlistDialog.setContentView(view);
    }

    /**
     * 显示播放列表窗口
     * */
    private void  showPlaylistDialog() {
        updateAdapter();
        if (playlistDialog != null && !playlistDialog.isShowing()) {
            playlistDialog.show();
        }

    }

    /**
     * 初始化歌词视图
     * */
    private void initLrcView() {
        lrcView.setBeans(lrcBeans);
    }


    /**
     * 添加观察者
     */
    private void observeLiveData() {
        //當前播放歌曲發生變化
        baseViewModel.observeData(BaseViewModel.CURRENT_SONG_DATA, this, new Observer<Item>() {//注册当前歌曲观察者
            @Override
            public void onChanged(@Nullable final Item item) {
                if (item == null) {
                    tv_artist_expand.setText("无");
                    tv_title_expand.setText("没有歌曲");
                    return;
                }
                lrcBeans = null;
                lrcView.setBeans(null);
                //获取歌词
                Message message = new Message();
                message.what = MSG_GET_LRC;
                message.obj = item;
                threadHandler.sendMessage(message);
                //获取歌曲封面
                if (!TextUtils.isEmpty(item.getPic())) {
                    Util.setPic(item.getPic(),iv_circle,BaseActivity.this);
                } else {

                    Util.setSongImgToImageView(item,BaseActivity.this,iv_circle);
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
                updateAdapter();
            }
        });
        baseViewModel.observeData(BaseViewModel.PLAY_MODE_DATA, this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //播放模式变化
                if (s != null) {
                    switch (s) {
                        case Constant.MODE_LIST_RECYCLE: {
                            iv_loop.setVisibility(View.VISIBLE);
                            ll_order.setVisibility(View.VISIBLE);
                            ll_random.setVisibility(View.GONE);
                            ll_single.setVisibility(View.GONE);
                            iv_single.setVisibility(View.GONE);
                            iv_random.setVisibility(View.GONE);
                            break;
                        }
                        case Constant.MODE_RANDOM: {
                            iv_random.setVisibility(View.VISIBLE);
                            ll_random.setVisibility(View.VISIBLE);
                            ll_order.setVisibility(View.GONE);
                            ll_single.setVisibility(View.GONE);
                            iv_single.setVisibility(View.GONE);
                            iv_loop.setVisibility(View.GONE);
                            break;
                        }
                        case Constant.MOED_SINGLE_RECYCLE:{
                            ll_single.setVisibility(View.VISIBLE);
                            ll_order.setVisibility(View.GONE);
                            ll_random.setVisibility(View.GONE);
                            iv_single.setVisibility(View.VISIBLE);
                            iv_random.setVisibility(View.GONE);
                            iv_loop.setVisibility(View.GONE);
                            break;
                        }
                        default:break;
                    }
                }

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

        baseViewModel.observeData(BaseViewModel.POSTION, this, new Observer<Integer>() {//播放进度
            @Override
            public void onChanged(@Nullable Integer progress) {
                if (baseViewModel.getCurPlaySong().getValue() == null) {
                    return;
                }
                Message message = new Message();
                message.what = SET_TIME;
                message.arg1 = progress;
                handler.sendMessage(message);
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
                    case R.id.btn_list:{
                        showPlaylistDialog();
                        break;
                    }
                    case R.id.iv_pre: {
                        pre();
                        seekBar.setProgress(0);
                        break;
                    }
                    case R.id.iv_loop: {
                        ToastUtil.showShort(getApplicationContext(), "已切换到单曲循环");
                        changeMode(Constant.MOED_SINGLE_RECYCLE);
                        break;
                    }
                    case R.id.iv_single: {
                        changeMode(Constant.MODE_RANDOM);
                        ToastUtil.showShort(getApplicationContext(), "已切换到随机播放");
                        break;
                    }
                    case R.id.iv_random: {
                        changeMode(Constant.MODE_LIST_RECYCLE);
                        ToastUtil.showShort(getApplicationContext(), "已切换到列表循环");
                        break;
                    }
                    case R.id.iv_delete_all: {
                        baseViewModel.removeAllSongFromPlaylist();
                        break;
                    }
                    case R.id.ll_random: {
                        changeMode(Constant.MODE_LIST_RECYCLE);
                        ToastUtil.showShort(getApplicationContext(), "已切换到列表循环");
                        break;

                    }
                    case R.id.ll_order:{
                        changeMode(Constant.MOED_SINGLE_RECYCLE);
                        ToastUtil.showShort(getApplicationContext(), "已切换到单曲循环");
                        break;
                    }
                    case R.id.ll_single:{
                        changeMode(Constant.MODE_RANDOM);
                        ToastUtil.showShort(getApplicationContext(), "已切换到随机播放");
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
    protected void onStop() {
        baseViewModel.saveProgress();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        baseViewModel.removeObserves(this);
        handlerThread.quit();
        super.onDestroy();
    }
}
