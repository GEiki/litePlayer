package com.dedaodemo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;

import com.dedaodemo.R;
import com.dedaodemo.model.Item;
import com.dedaodemo.model.SongList;
import com.dedaodemo.model.SongViewModel;

import java.util.ArrayList;
import java.util.List;


public class SongFragment extends BaseBottomFragment {

    public static String SONG_FRAGMENT="songFragment";

    private ListView listView;
    private MListAdapter adapter;

    private SongListFragment.OnFragmentInteractionListener mListener;
    private static SongList songList;
    private SongViewModel model;

    private Toolbar toolbar;


    public SongFragment() {
    }

    public static SongFragment newInstance(SongList list) {
        songList = list;
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of((AppCompatActivity)getActivity()).get(SongViewModel.class);
        getActivity().getLifecycle().addObserver(model);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_song, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(songList.getTitle());
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //设置返回键
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        initListView(v);
        return v;
    }

    private void initListView(View v){
        listView = (ListView)v.findViewById(R.id.list_view);
        adapter = new MListAdapter(getContext());
        if(songList != null){
            ArrayList<Item> items =songList.getSongList().getValue();
            adapter.setItemData(items);
        }
        listView.setAdapter(adapter);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongListFragment.OnFragmentInteractionListener) {
            mListener = (SongListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                getActivity().onBackPressed();
                break;
            }
            default:break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void play() {

    }

    @Override
    protected void pause() {

    }

    @Override
    protected void pre() {

    }

    @Override
    protected void next() {

    }

    @Override
    protected void onSeekBarProgressChange(SeekBar seekBar, int progress, boolean fromUser) {

    }
}
