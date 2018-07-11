package com.dedaodemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.BaseViewModel;
import com.dedaodemo.ViewModel.Contracts.SongListContract;
import com.dedaodemo.ViewModel.SongListViewModel;
import com.dedaodemo.adapter.MListAdapter;
import com.dedaodemo.bean.SongList;


public class SongListFragment extends BaseBottomFragment implements View.OnClickListener {

    public static String TAG_SONG_LIST_FRAGMENT = "SongListFragment";
    public static final String ARG_SONG_LIST = "songList";
    private SongList songList;


    private Toolbar toolbar;
    AlertDialog loadingDialog;
    private SongListContract.Presenter viewModel;


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
        viewModel = ViewModelProviders.of((AppCompatActivity) getActivity()).get(SongListViewModel.class);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            songList = (SongList) bundle.getSerializable(ARG_SONG_LIST);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        addCardView(v, inflater);

        toolbar = getToolbar();
        toolbar.setTitle(songList.getTitle());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        /**
         * 初始化UI
         * */
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);


        /*
        * 初始化dialog
        * */
        AlertDialog.Builder ab=new AlertDialog.Builder(getActivity());
        View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading,null);
        loadingDialog=ab.setView(dialogView).create();

        viewModel.observeSongList(getActivity(), new Observer<SongList>() {
            @Override
            public void onChanged(@Nullable SongList songList) {
                MListAdapter mListAdapter = new MListAdapter(getContext());
                mListAdapter.setItemData(songList.getSongList());
                setAdapter(mListAdapter);
            }
        });
        MListAdapter mListAdapter = new MListAdapter(getContext());
        mListAdapter.setItemData(songList.getSongList());
        setAdapter(mListAdapter);

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                play(position);
            }
        });
        return v;
    }

    private void addCardView(View view, LayoutInflater inflater) {
        CoordinatorLayout viewGroup = (CoordinatorLayout) view;
        AppBarLayout appBarLayout = (AppBarLayout) viewGroup.getChildAt(0);
        ViewGroup cardView = (ViewGroup) inflater.inflate(R.layout.song_list_header, null);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 500);
        cardView.setLayoutParams(lp);
        appBarLayout.addView(cardView);
        appBarLayout.setBackground(getResources().getDrawable(android.R.color.white));
        viewGroup.invalidate();
    }

    @Override
    protected BaseViewModel getViewModel() {
        return (BaseViewModel) viewModel;
    }

    @Override
    protected boolean speacialFlag() {
        return false;
    }

    @Override
    protected View getBaseBottomBarView() {
        return null;
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
        inflater.inflate(R.menu.base_menu, menu);


    }






}
