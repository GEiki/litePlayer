package com.dedaodemo.model;

import com.dedaodemo.MyApplication;
import com.dedaodemo.ViewModel.Contracts.BaseContract;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.database.MyDatabaseHelper;

/**
 * Created by 01377578 on 2018/7/27.
 */
@Deprecated
public class BottomBarModel implements BaseContract.Model {

    private static final String TAG = "BottomBarModel";
    private BaseContract.ViewModel viewModel;
    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(MyApplication.getMyApplicationContext(), MyDatabaseHelper.SONG_DATABASE_NAME, null, MyDatabaseHelper.VERSION);

    public BottomBarModel(BaseContract.ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void loadBottomBarState() {


    }

    @Override
    public void saveBottomBarState(SongList songList, Item item) {


    }
}
