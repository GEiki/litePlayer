package com.dedaodemo.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.AdapterView;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.SearchContract;
import com.dedaodemo.ViewModel.SearchViewModel;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.common.Constant;
import com.dedaodemo.util.ToastUtil;

import java.util.ArrayList;


public class SearchFragment extends BaseBottomFragment implements AdapterView.OnItemClickListener {

    private SearchContract.Presenter viewModel;
    private Toolbar toolbar;
    private SearchView searchView;


    public static final String TAG = "SEARCH_FRAGMENT";

    private String searchSource = Constant.TYPE_QQ;
    private ArrayList<Item> searchList;
    private SongList searchSongList;


    public SearchFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = super.onCreateView(inflater, container, null);
        viewModel.observeSearchSongList(getActivity(), new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Item> items) {
                MListAdapter adapter1 = new MListAdapter(getContext());
                adapter1.setItemData(items);
                setAdapter(adapter1);
                searchList = items;
            }
        });

        setOnItemClickListener(this);

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

        return v;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        searchView = (SearchView) menu.getItem(0).getActionView();
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("请输入关键词搜索");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchBean bean = new SearchBean();
                bean.setKey(query);
                bean.setSearchType(searchSource);
                viewModel.searchSong(bean);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


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
                ToastUtil.showShort(getContext(), "搜索源变更为QQ音乐");
                break;
            }
            case R.id.action_kg: {
                searchSource = Constant.TYPE_KG;
                ToastUtil.showShort(getContext(), "搜索源变更为酷狗音乐");
                break;
            }
            case R.id.action_wy: {
                searchSource = Constant.TYPE_WY;
                ToastUtil.showShort(getContext(), "搜索源变更为网易云音乐");
                break;
            }
            default:
                break;
        }
        return true;
    }
}
