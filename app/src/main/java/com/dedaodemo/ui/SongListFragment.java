package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
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
import android.widget.RelativeLayout;

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
    private ImageView iv_head;
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
        if (songListFragment == null) {
            songListFragment = new SongListFragment();
        }

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
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white,null));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black,null));
        collapsingToolbarLayout.setExpandedTitleMarginBottom(20);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        /**
         * 初始化UI
         * */

        btn_play_list = mView.findViewById(R.id.btn_play_list);
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
    }

    @Override
    public void onItemClick(View v, int position) {
        baseViewModel.playSong(mSongList, mSongList.getSongList().get(position));
    }

    private void addHeaderImgView(View view, LayoutInflater inflater) {
        iv_head = mView.findViewById(R.id.iv_head);
        //尝试加载专辑封面
        Item item = null;
        if (mSongList.getSongList() != null && !mSongList.getSongList().isEmpty()) {
            item = mSongList.getSongList().get(0);
            viewModel.setPic(item,iv_head);
        } else {
            final RequestOptions requestOptions = new RequestOptions()
                    .transform(new BlurTransformation(25, 5))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .skipMemoryCache(true);
            Glide.with(getContext())
                    .load(R.drawable.default_songlist_background)
                    .apply(requestOptions)
                    .into(iv_head);
        }


    }







    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment","SongListFragment");
    }

    @Override
    public void onClick(View v) {

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
    public void onMenuItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.tv_move: {
                adapter.closeSwipeLayout();
                Item song = adapter.getmData().get(position);
                showChooseSongListDialog(song);
                break;
            }
            case R.id.tv_delete: {
                Item song = adapter.getmData().get(position);
                ArrayList<Item> items = new ArrayList<>();
                items.add(song);
                viewModel.removeSong(items);
                break;
            }
            default:
                break;
        }

    }

    private void showChooseSongListDialog(final Item item) {
        bottomSheetDialog = new BottomSheetDialog(getContext());
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

    @Override
    public void onDestroyView() {
        viewModel.removeObserveSongList(songListObserver);
        super.onDestroyView();
    }
}
