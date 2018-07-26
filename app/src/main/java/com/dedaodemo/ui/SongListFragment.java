package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.ViewModel.SongListViewModel;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.ChooseSheetAdapter;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;


public class SongListFragment extends BaseBottomFragment implements View.OnClickListener, BaseAdapter.OnItemClickListener, MListAdapter.OnMenuItemOnClickListener {

    public static String TAG_SONG_LIST_FRAGMENT = "SongListFragment";
    public static final String ARG_SONG_LIST = "songList";
    private SongList songList;


    private Toolbar toolbar;
    private View mView;
    AlertDialog loadingDialog;
    BottomSheetDialog bottomSheetDialog;
    private ImageView iv_head;
    private RecyclerView recyclerView;
    private MListAdapter adapter;
    private SongListContract.Presenter viewModel;
    private int preSize = 0;
    private Observer<SongList> songListObserver;


    public SongListFragment() {
    }


    public static SongListFragment newInstance(SongList songList) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG_LIST, songList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        viewModel = ViewModelProviders.of(this).get(SongListViewModel.class);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            songList = (SongList) bundle.getSerializable(ARG_SONG_LIST);
            preSize = songList.getSize();
            viewModel.setSongList(songList);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_song_list, null, false);
        //添加底层播放栏
        super.onCreateView(inflater, container, savedInstanceState);
        addHeaderImgView(mView, inflater);
        toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle(songList.getTitle());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        /**
         * 初始化UI
         * */
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);


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
                if (songList.getTitle().equals(mSongList.getTitle()) && mSongList.getSize() < preSize) {
                    adapter.setmData(songList.getSongList());
                    preSize = mSongList.getSize();
                    recyclerView.setAdapter(adapter);
                    ToastUtil.showShort(getActivity(), "移除歌曲成功");
                } else if (songList.getTitle().equals(mSongList.getTitle()) && mSongList.getSize() == preSize) {
                    //do nothing
                } else {
                    ToastUtil.showShort(getActivity(), "添加成功");
                }

            }
        };
        viewModel.observeSongList(getActivity(), songListObserver);
        return mView;
    }

    @Override
    public View getParentView() {
        return mView;
    }

    private void initRecyclerView(ViewGroup viewGroup) {
        recyclerView = mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(8, 0, 8, 0);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(layoutParams);
        adapter = new MListAdapter(getContext());
        adapter.setmData(songList.getSongList());
        adapter.setMenuId(R.menu.song_menu);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemAddClickListener(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        play(position);
    }

    private void addHeaderImgView(View view, LayoutInflater inflater) {
        iv_head = mView.findViewById(R.id.iv_head);
        //尝试加载专辑封面
        Item item = null;
        if (songList.getSongList() != null && !songList.getSongList().isEmpty()) {
            item = songList.getSongList().get(0);
        }

        if (item == null || item.getType() == Item.LOCAL_MUSIC) {
            Glide.with(getContext())
                    .load(R.drawable.default_songlist_background)
                    .into(iv_head);
        } else {
            Glide.with(getContext())
                    .asDrawable()
                    .load(item.getPic()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Glide.with(getContext())
                            .load(R.drawable.default_songlist_background)
                            .into(iv_head);
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(iv_head);
        }
    }

    @Override
    protected BaseViewModel getViewModel() {
        return (BaseViewModel) viewModel;
    }


    @Override
    protected void setBottomBarVisibility(int visibility) {
        super.setBottomBarVisibility(visibility);
    }

    @Override
    protected void play(int pos) {
        ((BaseViewModel) viewModel).playSong(songList, songList.getSongList().get(pos));
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
            }
            default:
                break;
        }
    }

    private void showChooseSongListDialog(final Item item) {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(getContext());
        RecyclerView view = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_sheet, null);
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        final ChooseSheetAdapter adapter = new ChooseSheetAdapter(getContext());
        adapter.setmData(SongManager.getInstance().getSheetList());
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ArrayList<Item> items = new ArrayList<Item>();
                items.add(item);
                viewModel.addSong(items, adapter.getmData().get(position));
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        view.setAdapter(adapter);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    @Override
    public void onDestroyView() {
        viewModel.removeObserveSongList(songListObserver);
        super.onDestroyView();
    }
}
