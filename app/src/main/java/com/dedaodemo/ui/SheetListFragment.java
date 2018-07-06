package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.ViewModel.SheetListViewModel;
import com.dedaodemo.adapter.SongListAdapter;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;


public class SheetListFragment extends BaseBottomFragment implements NavigationView.OnNavigationItemSelectedListener {

    public static String SHEET_LIST_FRAGMENT = "SheetListFragment";
    public static String SHEET_BACK_STACK = "sheetBackStack";

    private ListView listView;
    private SongListAdapter adapter;
    private SheetListContract.Presenter viewModel;
    private CoordinatorLayout baseBottomBarLayout;
    private ArrayList<SongList> sheetList;


    private Toolbar toolbar;
    private DrawerLayout drawerLayout;


    public SheetListFragment() {
    }

    public static SheetListFragment newInstance() {
        SheetListFragment fragment = new SheetListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of((AppCompatActivity) getActivity()).get(SheetListViewModel.class);
        getActivity().getLifecycle().addObserver((SheetListViewModel) viewModel);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheet_list, container, false);
        initNavigationView(view);
        baseBottomBarLayout = (CoordinatorLayout) view.findViewById(R.id.base_bottom_bar_view);
        //让父类初始化baseBottomBarLayout
        super.onCreateView(inflater, container, savedInstanceState);
        toolbar = getToolbar();
        toolbar.setTitle("Lite");
        toolbar.setTitleMarginStart(30);
        toolbar.setTitleMarginEnd(30);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        viewModel.observeSongLists(getActivity(), new Observer<ArrayList<SongList>>() {
            @Override
            public void onChanged(@Nullable ArrayList<SongList> songLists) {
                SongListAdapter listAdapter = new SongListAdapter(getContext());
                listAdapter.setData(songLists);
                setAdapter(listAdapter);
                sheetList = songLists;
            }
        });
        viewModel.loadData();


        //设置返回键
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        initNavigationView(view);
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongListFragment songListFragment = SongListFragment.newInstance(sheetList.get(position));
                //设置过度动画
                Slide slide = new Slide();
                slide.setDuration(700);
                songListFragment.setEnterTransition(slide);
                songListFragment.setAllowEnterTransitionOverlap(true);
                songListFragment.setAllowReturnTransitionOverlap(true);
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, songListFragment, SHEET_LIST_FRAGMENT)
                        .addToBackStack(SHEET_BACK_STACK)
                        .show(songListFragment)
                        .hide(SheetListFragment.this)
                        .commit();
            }
        });

        return view;
    }

    /**
     * 返回true时父类会调用getBaseBottomFlag
     */
    @Override
    protected boolean speacialFlag() {
        return true;
    }

    /**
     * speacialFlag返回true时被调用
     */
    @Override
    protected View getBaseBottomBarView() {
        return baseBottomBarLayout;
    }

    @Override
    protected BaseViewModel getViewModel() {
        return (BaseViewModel) viewModel;
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
            case R.id.action_settings: {
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
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void play(int pos) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_scan: {
                viewModel.scanMusic();
                break;
            }
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }


}
