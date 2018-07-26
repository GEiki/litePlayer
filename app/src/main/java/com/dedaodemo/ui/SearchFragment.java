package com.dedaodemo.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.ViewModel.SearchViewModel;
import com.dedaodemo.adapter.BaseAdapter;
import com.dedaodemo.adapter.ChooseSheetAdapter;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.common.SongManager;
import com.dedaodemo.util.ToastUtil;
import com.dedaodemo.util.Util;

import java.util.ArrayList;


public class SearchFragment extends BaseBottomFragment implements BaseAdapter.OnItemClickListener, MListAdapter.OnMenuItemOnClickListener {

    private SearchContract.Presenter viewModel;
    private Toolbar toolbar;
    private SearchView searchView;
    private MListAdapter mListAdapter;
    private RecyclerView recyclerView;
    private Observer<ArrayList<Item>> searchObserve;


    public static final String TAG = "SEARCH_FRAGMENT";

    private String searchSource = Constant.TYPE_WY;
    private ArrayList<Item> searchList;
    private SongList searchSongList;
    private BottomSheetDialog bottomSheetDialog;


    public SearchFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = super.onCreateView(inflater, container, null);

        initRecyclerView((ViewGroup) v);

        //注册观察搜索列表
        searchObserve = new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Item> items) {
                if (mListAdapter != null) {
                    mListAdapter.setmData(items);
                    recyclerView.setAdapter(mListAdapter);
                    searchList = items;
                }

            }
        };
        viewModel.observeSearchSongList(this, searchObserve);


        toolbar = getToolbar();
        toolbar.setTitle("搜索");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setPopupTheme(R.style.ToolbarPopupTheme);

        /**
         * 初始化UI
         * */
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        addSearchView(v);
        return v;
    }

    private void initRecyclerView(ViewGroup viewGroup) {
        recyclerView = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.recycler_view, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dip2px(getContext(), 485));
        layoutParams.setMargins(8, 0, 8, 0);
        layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(layoutParams);
        viewGroup.addView(recyclerView, 1);
        mListAdapter = new MListAdapter(getContext());
        mListAdapter.setMenuId(R.menu.search_menu);
        mListAdapter.setOnItemAddClickListener(this);
        mListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mListAdapter);
    }

    private void addSearchView(final View view) {
        searchView = new SearchView(getContext());
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("当前搜索源为网易云");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchBean bean = new SearchBean();
                bean.setKey(query);
                bean.setSearchType(searchSource);
                viewModel.searchSong(bean);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Util.dip2px(getContext(), 250), ViewGroup.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(layoutParams);
        toolbar.addView(searchView);
        view.invalidate();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment","SearchFragment");
    }
    public static Fragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onItemClick(View view, int position) {
        if (searchSongList == null) {
            searchSongList = new SongList();
            searchSongList.setTitle(Constant.SEARCH_SONG_LIST);
            searchSongList.setSongList(searchList);

        }
        ((BaseViewModel) viewModel).playSong(searchSongList, searchList.get(position));
    }

    @Override
    protected boolean speacialFlag() {
        return false;
    }

    @Override
    protected void play(int pos) {

    }

    @Override
    protected View getBaseBottomBarView() {
        return null;
    }

    @Override
    protected BaseViewModel getViewModel() {
        return (BaseViewModel) viewModel;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.base_menu, menu);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                break;
            }
            case R.id.action_tx: {
                searchSource = Constant.TYPE_QQ;
                searchView.setQueryHint("当前搜索源为QQ音乐");
                ToastUtil.showShort(getContext(), "搜索源变更为QQ音乐");
                break;
            }
            case R.id.action_kg: {
                searchSource = Constant.TYPE_KG;
                searchView.setQueryHint("当前搜索源为酷狗");
                ToastUtil.showShort(getContext(), "搜索源变更为酷狗音乐");
                break;
            }
            case R.id.action_wy: {
                searchSource = Constant.TYPE_WY;
                searchView.setQueryHint("当前搜索源为网易云");
                ToastUtil.showShort(getContext(), "搜索源变更为网易云音乐");
                break;
            }
            default:
                break;
        }
        return true;
    }

    @Override
    public void onMenuItemClick(MenuItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_add_song: {
                showChooseSongListDialog(mListAdapter.getmData().get(position));
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
                viewModel.addSong(adapter.getmData().get(position), item);
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
        viewModel.removeObserveSearchSongList(searchObserve);
        super.onDestroyView();
    }
}
