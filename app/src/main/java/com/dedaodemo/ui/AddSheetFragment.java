package com.dedaodemo.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dedaodemo.R;
import com.dedaodemo.ViewModel.Contracts.SheetListContract;
import com.dedaodemo.ViewModel.SheetListViewModel;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSheetFragment extends Fragment {

    private EditText et_sheet;
    private Toolbar toolbar;
    private SheetListContract.Presenter viewModel;
    private List<SongList> sheetList;
    public static final String TAG = "ADD_SHEET_FRAGMENT";


    public AddSheetFragment() {
        // Required empty public constructor
    }

    public static AddSheetFragment newInstance(Bundle args) {

        AddSheetFragment fragment = new AddSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_sheet, container, false);
        et_sheet = v.findViewById(R.id.et_sheet);
        et_sheet.setSingleLine();

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("添加歌单");
        toolbar.setTitleMarginStart(30);
        toolbar.setTitleMarginEnd(30);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(getActivity()).get(SheetListViewModel.class);
        viewModel.observeSongLists(this, new Observer<ArrayList<SongList>>() {
            @Override
            public void onChanged(@Nullable ArrayList<SongList> songLists) {
                sheetList = songLists;
            }
        });
        viewModel.loadData();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.add_sheet_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.action_finish:
                if (checkInputText()) {
                    SongList songList = new SongList();
                    songList.setTitle(et_sheet.getText().toString());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                    Date date = new Date(System.currentTimeMillis());
                    songList.setCreateDate(format.format(date));
                    viewModel.addSongList(songList);
                    ToastUtil.showShort(getActivity(), "添加歌单成功");
                    getActivity().onBackPressed();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean checkInputText() {
        String str = et_sheet.getText().toString();
        if (str.equals("")) {
            ToastUtil.showShort(getActivity(), "歌单名不能为空");
            return false;
        } else if (str.length() >= 12) {
            ToastUtil.showShort(getActivity(), "歌单名不能超过12个字");
            return false;
        } else {
            for (SongList list : sheetList) {
                if (str.equals(list.getTitle())) {
                    ToastUtil.showShort(getActivity(), "不能重复创建歌单");
                    return false;
                }
            }
        }
        return true;
    }


}
