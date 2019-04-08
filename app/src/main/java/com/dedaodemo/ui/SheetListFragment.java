package com.dedaodemo.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.ViewModel.SheetListViewModel;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.SongListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.dedaodemo.common.Constant.BASE_BACK_STACK;


public class SheetListFragment extends Fragment  implements NavigationView.OnNavigationItemSelectedListener, SongListAdapter.onMenuSongItemClickListener, BaseAdapter.OnItemClickListener {

    public static String SHEET_LIST_FRAGMENT = "SheetListFragment";
    public static String SHEET_BACK_STACK = "sheetBackStack";

    private RecyclerView recyclerView;
    private SongListAdapter songListAdapter;
    private SheetListContract.Presenter viewModel;
    private BaseContract.Presenter baseViewModel;
    private ArrayList<SongList> sheetList;
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private Observer<ArrayList<SongList>> sheetListObserve;
    private SongListFragment songListFragment;
    private EditText et_title;
    private EditText et_description;
    /**
     * 从服务启动的标志
     */
    public boolean startFlags = false;


    private Toolbar toolbar;
    private View mView;
    private DrawerLayout drawerLayout;


    public SheetListFragment() {
    }

    public static SheetListFragment newInstance() {
        SheetListFragment fragment = new SheetListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity()).get(SheetListViewModel.class);
        baseViewModel = ViewModelProviders.of(getActivity()).get(BaseViewModel.class);
        getActivity().getLifecycle().addObserver((SheetListViewModel) viewModel);
        Bundle bundle = getArguments();
        if (bundle != null) {
            startFlags = bundle.getBoolean(Constant.ACTION_N_FROM_SERVICE, false);
        }
        baseViewModel.init(startFlags);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sheet_list, container, false);
        Util.setTranslucentStatus(getActivity());
        toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("Lite");
        toolbar.setPopupTheme(R.style.ToolbarPopupTheme);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        super.onCreateView(inflater, container, savedInstanceState);
        initRecyclerView();
        sheetListObserve = new Observer<ArrayList<SongList>>() {
            @Override
            public void onChanged(@Nullable ArrayList<SongList> songLists) {
                if (progressBar != null) {
                    dismissSearching();
                }
                if (songLists == null)
                    return;
                songListAdapter.setOnMenuItemClickListener(SheetListFragment.this);
                songListAdapter.setmData(songLists);
                recyclerView.setAdapter(songListAdapter);
                sheetList = songLists;

            }
        };
        viewModel.loadData();
        viewModel.observeSongLists(getActivity(), sheetListObserve);



        //设置返回键
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        initNavigationView(mView);
        toolbar.setNavigationIcon(R.drawable.ic_action_navgation);
        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            viewModel.loadData();
        }
    }




    private void initRecyclerView() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3,RecyclerView.VERTICAL,false));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(getContext(), 445));
        layoutParams.setMargins(8, 0, 8, 0);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(layoutParams);
        songListAdapter = new SongListAdapter(getContext());
        songListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(songListAdapter);
    }

    @Override
    public void onItemClick(View v, int position) {
       songListFragment = SongListFragment.newInstance(sheetList.get(position));
       showFragment(songListFragment,this,Constant.SONG_LIST_FRAGMENT);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    /**
     * 初始化Navigation
     */
    private void initNavigationView(View v) {
        drawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search: {
                Fragment searchFragment = SearchFragment.newInstance();
                showFragment(searchFragment, SheetListFragment.this,Constant.SEARCH_FRAGMENT);
                break;
            }
            case R.id.action_add_song_list: {
                Fragment addSheetFragment = AddSheetFragment.newInstance(new Bundle());
                showFragment(addSheetFragment, SheetListFragment.this,Constant.ADD_SHEET_FRAGMENT);
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * fragment跳转
     */
    public void showFragment(Fragment showFragment, Fragment hideFragment,String TAG) {
        Slide slide = new Slide();
        slide.setDuration(500);
        showFragment.setEnterTransition(slide);
        showFragment.setAllowEnterTransitionOverlap(true);
        showFragment.setAllowReturnTransitionOverlap(true);
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, showFragment, TAG)
                .addToBackStack(BASE_BACK_STACK)
                .show(showFragment)
                .hide(hideFragment)
                .commit();
    }

    @Override
    public void onMenuItemClick(MenuItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_rename: {
                showRenameDialog(position);
                break;
            }
            case R.id.action_delete: {
                viewModel.removeSongList(sheetList.get(position));
                break;
            }
            case R.id.action_playAll: {
                List<Item> songList = sheetList.get(position).getSongList();
                if (songList != null) {
                    Item song = songList.get(0);
                    baseViewModel.playSong(sheetList.get(position),song);
                }
                break;
            }
            default:break;
        }
    }

    private void showRenameDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.buttonDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rename,null);
        et_title = view.findViewById(R.id.et_title);
        et_description = view.findViewById(R.id.et_description);
        builder.setView(view)
                .setTitle(sheetList.get(position).getTitle())
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SongList songList = sheetList.get(position);
                        String title = et_title.getText().toString();
                        String description = et_description.getText().toString();
                        if (!TextUtils.isEmpty(title)) {
                            songList.setTitle(title);
                        }
                        if (!TextUtils.isEmpty(description)) {
                            songList.setDescription(description);
                        }
                        viewModel.updateSongList(songList);
                        songListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onDestroyView() {
        viewModel.removeObserveSongLists(sheetListObserve);
        super.onDestroyView();
    }


    private void showDialog() {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(R.layout.dialog_loading);
            dialog = builder.create();
        }
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_scan: {
                viewModel.scanMusic();
                showSearching();
                break;
            }
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
    private void showSearching() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        progressBar =  viewGroup.findViewById(R.id.progressBar2);
        viewGroup.removeView(progressBar);
        toolbar.addView(progressBar);
        toolbar.invalidate();
    }

    private void dismissSearching() {
        toolbar.removeView(progressBar);
    }



}
