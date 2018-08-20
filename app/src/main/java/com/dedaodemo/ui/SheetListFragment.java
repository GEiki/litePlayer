package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.ViewModel.SheetListViewModel;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.SongListAdapter;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.Util;

import java.util.ArrayList;


public class SheetListFragment extends BaseBottomFragment implements NavigationView.OnNavigationItemSelectedListener, SongListAdapter.onMenuSongItemClickListener, BaseAdapter.OnItemClickListener {

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
        toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("Lite");
        toolbar.setPopupTheme(R.style.ToolbarPopupTheme);
        toolbar.setTitleMarginStart(30);
        toolbar.setTitleMarginEnd(30);
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.setMargins(0, Util.dip2px(getContext(), 20), 0, 0);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
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
        viewModel.observeSongLists(getActivity(), sheetListObserve);
        viewModel.loadData();


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
    }

    @Override
    public View getParentView() {
        return ((ViewGroup) mView).getChildAt(0);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(getContext(), 485));
        layoutParams.setMargins(8, 0, 8, 0);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(layoutParams);
        songListAdapter = new SongListAdapter(getContext());
        songListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(songListAdapter);
    }

    @Override
    public void onItemClick(View v, int position) {
        SongListFragment songListFragment = SongListFragment.newInstance(sheetList.get(position));
        showFragment(songListFragment, SheetListFragment.this);
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
                showFragment(searchFragment, SheetListFragment.this);
                break;
            }
            case R.id.action_add_song_list: {
                Fragment addSheetFragment = AddSheetFragment.newInstance(new Bundle());
                showFragment(addSheetFragment, SheetListFragment.this);
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(MenuItem item, int position) {
        switch (item.getItemId()) {
            case R.id.item_play: {
                break;
            }
            case R.id.item_remove: {
                viewModel.removeSongList(sheetList.get(position));
                break;
            }
        }
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
