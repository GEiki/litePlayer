package com.dedaodemo.ui;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.adapter.SongListAdapter;
import com.dedaodemo.service.MusicService;
import com.dedaodemo.R;
import com.dedaodemo.util.ScanUtil;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.HttpUtil;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.ViewModel.SongViewModel;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SongListFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    private final static int SCAN_FINISH = 3;
    private final static int SCAN_FAIL=4;
    private final static int START_SCAN=5;
    public final static int REQUEST_CODE=6;
    private final static int LOAD_SUCCESS=7;
    private final static int ADD_SONG_SUCCESS=8;
    private final static int SONG_EXIST=9;
    private final static int DELETE_SUCCESS=10;
    private final static int REMOVE_SONGLIST_SUCCESS=11;
    public final static int CHOOSE_STATE=12;
    public final static int CHOOSE_FINSHI=13;
    private final static int SET_PROGRESS=14;
    public static String TAG_SONG_LIST_FRAGMENT = "SongListFragment";


    public interface OnFragmentInteractionListener {
        void changeFragment(String TAG);
        void play(SongList list,int index);
        boolean next();
        boolean pre();
        void pause();
        void replay();
        int getProgress();
        void seekTo(int position);
        long getDuration();


    }

    public   class MyHandler extends Handler{
        private Context mContext;

        public MyHandler(Context context){
            mContext=context;
        }
        @Override
        public void handleMessage(Message msg){
            loadingDialog.dismiss();
            switch (msg.what){
                case 1:break;
                case 0:break;
                case SCAN_FINISH:{
                    Snackbar.make(parent_layout,"扫描完毕",Snackbar.LENGTH_SHORT).show();
//                    mMListAdapter=new MListAdapter(MainActivity.this,items);
//                    mListView.setAdapter(mMListAdapter);
                    break;
                }
                case SCAN_FAIL:{
                    Snackbar.make(parent_layout,"本地歌曲为空",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case LOAD_SUCCESS:{
                    updateBottomPlayBar();
                    break;
                }
                case ADD_SONG_SUCCESS:{
                    Snackbar.make(parent_layout,"添加歌曲成功",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case SONG_EXIST:{
                    Snackbar.make(parent_layout,"歌曲已存在",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case DELETE_SUCCESS:{
                    Snackbar.make(parent_layout,"成功从歌单中移除",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case REMOVE_SONGLIST_SUCCESS:{
                    Snackbar.make(parent_layout,"移除歌单成功",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                case CHOOSE_STATE:{
                    chooseStateSnackbar.show();
                    break;
                }
                case CHOOSE_FINSHI:{
                    chooseStateSnackbar.dismiss();
                    break;
                }
                case SET_PROGRESS:{
                    seekBar.setProgress(msg.arg1);
                    break;
                }
                default:break;
            }
            super.handleMessage(msg);
        }
    }





    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!thread.interrupted()){
                try{
                    Thread.sleep(1000);
                    float a = (float) musicService.getCurrentPosition();
                    float durantion = (float) musicService.getDuration();

                    int  b =(int)(100*(a/durantion));
                    Log.i("seek",String.valueOf(b));
                    Message message = new Message();
                    message.arg1 = b;
                    message.what = SET_PROGRESS;
                    myHandler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }
    });
    private TabLayout tab_layout;
    private ViewPager view_pager;
    private MListAdapter mMListAdapter;
    private LinearLayout ll_test;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout parent_layout;
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
    private Toolbar toolbar;
    private Snackbar chooseStateSnackbar;
    BottomSheetBehavior bottomSheetBehavior;
    AlertDialog loadingDialog;
    private ListView listView;
    private SongListAdapter listAdapter;

    private MyHandler myHandler = new MyHandler(getActivity());
    private SongViewModel model;
    public MusicService musicService;
    private boolean isHidding = true;



    private OnFragmentInteractionListener mListener;

    public SongListFragment() {
    }


    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of((AppCompatActivity)getActivity()).get(SongViewModel.class);
        getActivity().getLifecycle().addObserver(model);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("音乐");

        /**
         * 初始化UI
         * */
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        parent_layout = (CoordinatorLayout) v.findViewById(R.id.parent_layout);
        setHasOptionsMenu(true);
        initNavigationView(v);
        initListView(v);
        initBottomPlayBar(v);

        /*
        * 初始化dialog
        * */
        AlertDialog.Builder ab=new AlertDialog.Builder(getActivity());
        View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        loadingDialog=ab.setView(dialogView).create();

        return v;
    }

    /**
     * 初始化Navigation
     * */
    private void initNavigationView(View v){
         drawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    /**
     * 初始化ListView
     * */
    private void initListView(View v){
        listView = (ListView)v.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("ListView", "onItemClick: ");
                Slide slide  = new Slide();
                slide.setDuration(500);
                FragmentManager fm =getActivity().getSupportFragmentManager();
                Fragment songFragment = (Fragment) SongFragment.newInstance(model.getSongLists().get(position));
                songFragment.setEnterTransition(slide);
                songFragment.setExitTransition(slide);
                fm.beginTransaction().hide(SongListFragment.this).add(R.id.fragment_container,songFragment,SongFragment.SONG_FRAGMENT).addToBackStack(null).commit();
            }
        });
        listAdapter = new SongListAdapter(getActivity());
        listAdapter.setData(model.getSongLists());
        model.observeSongLists(getActivity(), new Observer<ArrayList<SongList>>() {
            @Override
            public void onChanged(@Nullable ArrayList<SongList> songLists) {
                Log.i("DATA","update ui");
                listAdapter.setData(songLists);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
        }
        });

    }
    /**
     * 初始化底层播放栏
     * */
    private void initBottomPlayBar(View v){

        bottom_play_bar = (ConstraintLayout)v.findViewById(R.id.ll_bottom_play_bar);
        bottom_layout_expand = (ConstraintLayout)v.findViewById(R.id.bottom_layout_expand);
        bottom_layout_hide = (ConstraintLayout)v.findViewById(R.id.bottom_layout_hide);
        iv_circle =(TextView) v.findViewById(R.id.iv_circle);
        tv_artist_expand = (TextView)v.findViewById(R.id.tv_artist_expand);
        tv_artist_hide = (TextView)v.findViewById(R.id.tv_artist_hide);
        tv_title_expand = (TextView)v.findViewById(R.id.tv_title_expand);
        tv_title_hide= (TextView)v.findViewById(R.id.tv_title_hide);
        btn_pause_hide = (ImageButton)v.findViewById(R.id.btn_pause);
        btn_pause_hide.setOnClickListener(this);
        btn_play_hide = (ImageButton)v.findViewById(R.id.btn_play);
        btn_play_hide.setOnClickListener(this);
        btn_next_hide = (ImageButton)v.findViewById(R.id.btn_next);
        btn_next_hide.setOnClickListener(this);
        btn_pause_expand = (ImageButton)v.findViewById(R.id.btn_pause_expand);
        btn_pause_expand.setOnClickListener(this);
        btn_play_expand = (ImageButton)v.findViewById(R.id.btn_play_expand);
        btn_play_expand.setOnClickListener(this);
        btn_next_expand = (ImageButton)v.findViewById(R.id.btn_next_expand);
        btn_next_expand.setOnClickListener(this);
        btn_pre_expand = (ImageButton)v.findViewById(R.id.btn_pre_expand);
        btn_pre_expand.setOnClickListener(this);
        seekBar = (SeekBar)v.findViewById(R.id.seekBar3);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(musicService.isPrepared()) {
                        float a = (float) ((float) progress / 100f);
                        int b = (int) ((float) (mListener.getDuration()) * a);
                        musicService.seekTo(b);
                    }
                }
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
    private void updateBottomPlayBar(){
//        if(!songLists.isEmpty()) {
//            tv_title_hide.setText(songLists.get(curSongList).getSongList().get(curSong).getTitle());
//            tv_title_hide.setBackgroundColor(songLists.get(curSongList).getColor());
//            tv_artist_hide.setText(songLists.get(curSongList).getSongList().get(curSong).getAuthor());
//            String str = String.valueOf(songLists.get(curSongList).getTitle().charAt(0));
//            iv_circle.setText(str);
//            tv_title_expand.setText(songLists.get(curSongList).getSongList().get(curSong).getTitle());
//            tv_title_expand.setBackgroundColor(songLists.get(curSongList).getColor());
//            tv_artist_expand.setText(songLists.get(curSongList).getSongList().get(curSong).getAuthor());
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:{
                ArrayList<SongList> songLists = model.getSongLists();
                if(!songLists.isEmpty()){
                    if(musicService.isPasusing()){
                        musicService.rePlay();
                        btn_play_hide.setVisibility(View.GONE);
                        btn_pause_hide.setVisibility(View.VISIBLE);
                        btn_play_expand.setVisibility(View.GONE);
                        btn_pause_expand.setVisibility(View.VISIBLE);
                        return;
                    }
//                    musicService.play(curSong,songLists.get(curSongList).getSongList());
                    btn_play_hide.setVisibility(View.GONE);
                    btn_pause_hide.setVisibility(View.VISIBLE);
                    btn_play_expand.setVisibility(View.GONE);
                    btn_pause_expand.setVisibility(View.VISIBLE);
                    seekBar.setProgress(0);
                    if(!thread.isAlive()){
                        thread.start();
                    }
                }else {
                    Snackbar.make(parent_layout,"还没有添加歌曲哦",Snackbar.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.btn_pause:{
                musicService.pause();
                btn_play_hide.setVisibility(View.VISIBLE);
                btn_pause_hide.setVisibility(View.GONE);
                btn_play_expand.setVisibility(View.VISIBLE);
                btn_pause_expand.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_pause_expand:{
                musicService.pause();
                btn_play_hide.setVisibility(View.VISIBLE);
                btn_pause_hide.setVisibility(View.GONE);
                btn_play_expand.setVisibility(View.VISIBLE);
                btn_pause_expand.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_play_expand:{
                ArrayList<SongList> songLists = model.getSongLists();
                if(!songLists.isEmpty()){
                    if(musicService.isPasusing()){
                        musicService.rePlay();
                        btn_play_hide.setVisibility(View.GONE);
                        btn_pause_hide.setVisibility(View.VISIBLE);
                        btn_play_expand.setVisibility(View.GONE);
                        btn_pause_expand.setVisibility(View.VISIBLE);
                        return;
                    }
//                    musicService.play(curSong,songLists.get(curSongList).getSongList());
                    btn_play_hide.setVisibility(View.GONE);
                    btn_pause_hide.setVisibility(View.VISIBLE);
                    btn_play_expand.setVisibility(View.GONE);
                    btn_pause_expand.setVisibility(View.VISIBLE);
                    seekBar.setProgress(0);
                    thread.start();
                }else {
                    Snackbar.make(parent_layout,"还没有添加歌曲哦",Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btn_next:{
                musicService.next();
                seekBar.setProgress(0);

                break;
            }
            case R.id.btn_next_expand:{
                musicService.next();
                seekBar.setProgress(0);

                break;
            }
            case R.id.btn_pre_expand:{
                musicService.pre();
                seekBar.setProgress(0);
                break;
            }
            default:break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment","SongListFragment");
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        //初始化searchView
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("请输入歌曲名称");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(parent_layout,"搜索中",Snackbar.LENGTH_SHORT).show();
                model.searchSong(query, Constant.TYPE_QQ, new HttpUtil.ResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {

                        try {
                            if(response != null && response.toString().contains("ResultCode")) {
                                if (response.getInt("ResultCode") == 1) {
                                    Snackbar.make(parent_layout,"搜索成功",Snackbar.LENGTH_SHORT).show();
                                    ArrayList<Item> items = new ArrayList<>();
                                    JSONArray jsonArray = response.getJSONArray("Body");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject song = jsonArray.getJSONObject(i);
                                        Item item = new Item();
                                        item.setTitle(song.getString("title"));
                                        item.setAuthor(song.getString("author"));
                                        item.setURL(song.getString("url"));
                                        item.setLrc(song.getString("pic"));
                                        item.setPic(song.getString("lrc"));
                                        item.setType(Item.INTERNET_MUSIC);
                                        items.add(item);
                                    }
                                    model.updateSearchList(items);



                                } else {
                                    Snackbar.make(parent_layout,"搜索失败",Snackbar.LENGTH_SHORT).show();
                                }
                            }else {
                                Snackbar.make(parent_layout,"程序出了问题",Snackbar.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String erroMsg) {
                        Snackbar.make(parent_layout,"服务器有问题："+ erroMsg,Snackbar.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_scan:{
                //异步扫描本地歌曲
                loadingDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<Item> itemList= ScanUtil.scanMusicFiles(getActivity().getApplicationContext());//加强版扫描

                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<SongList> songLists = model.getSongLists();
                                ArrayList<Item> songs = new ArrayList<>();
                                if(!itemList.isEmpty()) {
                                    if (songLists.size()>0) {
                                        for(Item i : itemList){
                                            songs.add(i);
                                        }
                                        model.addSongToSongList(songs,songLists.get(0));
                                        myHandler.sendEmptyMessage(SCAN_FINISH);

                                    } else {
                                        //创建全部歌曲歌单
                                        final SongList songList = new SongList();
                                        songList.setTitle("全部歌曲");
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年mm月dd日");
                                        Date date = new Date(System.currentTimeMillis());
                                        songList.setCreateDate(simpleDateFormat.format(date));
                                        songList.setSize(String.valueOf(itemList.size()));

//                                    //创建歌单
//                                        int count = 0;
//                                        for (int i=0;i<itemList.size();i++) {//添加歌曲到歌单和数据库
//                                            songList.addSong(itemList.get(i));
//                                            count++;
//                                        }
                                        model.addSongList(songList);
                                        model.addSongToSongList(itemList,songList);
                                        myHandler.sendEmptyMessage(SCAN_FINISH);

                                    }
                                }else{
                                    myHandler.sendEmptyMessage(SCAN_FAIL);
                                }
                            }
                        });


                    }
                }).start();


                break;
            }
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }



}
