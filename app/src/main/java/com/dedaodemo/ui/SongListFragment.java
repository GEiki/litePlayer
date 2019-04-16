package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.ViewModel.SongListViewModel;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.ChooseSheetAdapter;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.behavior.FadeBehavior;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class SongListFragment extends Fragment implements View.OnClickListener, BaseAdapter.OnItemClickListener, MListAdapter.OnMenuItemOnClickListener {

    public static String TAG_SONG_LIST_FRAGMENT = "SongListFragment";
    public static final String ARG_SONG_LIST = "songList";
    private SongList mSongList;


    private FloatingActionButton btn_play_list;
    private Toolbar toolbar;
    private View mView;
    AlertDialog loadingDialog;
    BottomSheetDialog bottomSheetDialog;
    private TextView tv_default_background;
    private ImageView iv_head;
    private ImageView iv_back;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MListAdapter adapter;
    private SongListContract.Presenter viewModel;
    private BaseContract.Presenter baseViewModel;
    private int preSize = 0;
    private Observer<SongList> songListObserver;
    private static SongListFragment songListFragment;


    public SongListFragment() {
    }


    public static SongListFragment newInstance(SongList songList) {

        songListFragment = new SongListFragment();


        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG_LIST, songList);
        songListFragment.setArguments(args);
        return songListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        viewModel = ViewModelProviders.of(this).get(SongListViewModel.class);
        baseViewModel = ViewModelProviders.of(getActivity()).get(BaseViewModel.class);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mSongList = (SongList) bundle.getSerializable(ARG_SONG_LIST);
            preSize = mSongList.getSize();
            viewModel.loadSongData(mSongList);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_song_list, null, false);
        Util.setTranslucentStatus(getActivity());
        //添加底层播放栏
        super.onCreateView(inflater, container, savedInstanceState);
        addHeaderImgView(mView, inflater);
        toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        collapsingToolbarLayout = mView.findViewById(R.id.cop_layout);
        collapsingToolbarLayout.setTitle(mSongList.getTitle());
        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER_VERTICAL);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white,null));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black,null));
        collapsingToolbarLayout.setExpandedTitleMarginBottom(20);
        tv_default_background = mView.findViewById(R.id.tv_default_background);
        iv_back = mView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        /**
         * 初始化UI
         * */

        btn_play_list = mView.findViewById(R.id.btn_play_list);
        btn_play_list.setOnClickListener(this);
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) btn_play_list.getLayoutParams();
////        lp.setBehavior(new FadeBehavior(getContext(),null));
////        btn_play_list.setLayoutParams(lp);

        /**
        * 初始化dialog
        * */
        final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        loadingDialog=ab.setView(dialogView).create();

        initRecyclerView((ViewGroup) mView);

        /**
         * 注册观察歌单
         * */
        songListObserver = new Observer<SongList>() {
            @Override
            public void onChanged(@Nullable SongList mSongList) {
                if (mSongList == null)
                    return;
                adapter.setmData(mSongList.getSongList());
                recyclerView.setAdapter(adapter);
                SongListFragment.this.mSongList = mSongList;
            }
        };
        viewModel.observeSongList(getActivity(), songListObserver);
        return mView;
    }


    private void initRecyclerView(ViewGroup viewGroup) {
        recyclerView = mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Util.dip2px(getContext(),445));
        layoutParams.setMargins(8, 0, 8, 0);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(layoutParams);
        adapter = new MListAdapter(getContext());
        adapter.setmData(mSongList.getSongList());
        adapter.setMenuId(R.menu.song_menu);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemAddClickListener(this);

        if (mSongList.getSize() == 0) {
            recyclerView.setVisibility(View.GONE);
            tv_default_background.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_default_background.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        baseViewModel.playSong(mSongList, mSongList.getSongList().get(position));
    }

    private void addHeaderImgView(View view, LayoutInflater inflater) {
        iv_head = mView.findViewById(R.id.iv_head);
        final RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(getContext())
                .load(R.drawable.default_songlist_background)
                .apply(requestOptions)
                .into(iv_head);
        if (mSongList.getSongList() == null || mSongList.getSongList().size() <= 0) {
            return;
        }
        Item item = mSongList.getSongList().get(0);
        if (!TextUtils.isEmpty(item.getPic())) {
            Util.setPic(item.getPic(),iv_head,getContext());
        } else {
            Util.setSongImgToImageView(item,getContext(),iv_head);
        }


    }







    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment","SongListFragment");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
                break;
            }
            case R.id.btn_play_list: {
                if (mSongList != null && mSongList.getSongList().size() > 0) {
                    List<Item> list = mSongList.getSongList();
                    baseViewModel.playSong(mSongList,list.get(0));
                } else {
                    ToastUtil.showShort(getContext(),"歌单中没有歌曲可以播放");
                }
                break;
            }
            default:break;

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                break;
            }
            case R.id.action_search: {
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        inflater.inflate(R.menu.base_menu, menu);


    }

    @Override
    public void onMenuItemClick(MenuItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_add_song: {
                Item song = adapter.getmData().get(position);
                showChooseSongListDialog(song);
                break;
            }
            case R.id.action_remove: {
                Item song = adapter.getmData().get(position);
                ArrayList<Item> items = new ArrayList<>();
                items.add(song);
                viewModel.removeSong(items);
                break;
            }case R.id.action_info: {
                showMusicInfoDialog(adapter.getmData().get(position));
                break;
            }
            case R.id.action_add_playlist: {
                if (mSongList != null) {
                    ArrayList<Item> items = new ArrayList<>();
                    items.add(mSongList.getSongList().get(position));
                    baseViewModel.addSongToPlaylist(items);
                } else {
                    Log.e("SongListFragment","songList null error !");
                }

                break;
            }
            default:
                break;
        }
    }


    private void showChooseSongListDialog(final Item item) {
        if(bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(getContext());
        }
        RecyclerView view = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_sheet, null);
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        final ChooseSheetAdapter adapter = new ChooseSheetAdapter(getContext());
        viewModel.getSheetListLiveData().observe(this, new Observer<List<SongList>>() {
            @Override
            public void onChanged(@Nullable List<SongList> o) {
                adapter.setmData((ArrayList<SongList>) o);
                if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.show();
                }
            }
        });
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrayList<Item> items = new ArrayList<Item>();
                items.add(item);
                SongList songList = adapter.getmData().get(position);
                if (!songList.getTitle().equals(mSongList.getTitle())) {
                    viewModel.addSong(items, adapter.getmData().get(position));
                }
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        view.setAdapter(adapter);
        bottomSheetDialog.setContentView(view);
        viewModel.loadSheetList();
    }

    private void showMusicInfoDialog(Item item) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info,null);
        TextView tv_song_title = view.findViewById(R.id.tv_song_title);
        tv_song_title.setText(item.getTitle());
        TextView tv_artist = view.findViewById(R.id.tv_artist);
        tv_artist.setText(item.getAuthor());
        TextView tv_duration = view.findViewById(R.id.tv_duration);
        tv_duration.setText(Util.durationToformat(Long.valueOf(item.getTime())));
        TextView tv_album = view.findViewById(R.id.tv_album);
        tv_album.setText(item.getAlbum());
        TextView tv_size = view.findViewById(R.id.tv_size);
        tv_size.setText(String.valueOf(Util.bytes2megaBytes(item.getSize()))+"MB");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .create()
                .show();
    }

    @Override
    public void onDestroyView() {
        viewModel.removeObserveSongList(songListObserver);
        super.onDestroyView();
    }
}
