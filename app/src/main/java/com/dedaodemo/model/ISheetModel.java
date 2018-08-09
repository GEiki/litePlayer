package com.dedaodemo.model;

import com.dedaodemo.bean.CurrentPlayStateBean;
import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by 01377578 on 2018/7/27.
 */

public interface ISheetModel {
    Observable addSongs(SongList songList, ArrayList<Item> items);

    Observable createSongList(SongList songList);

    Observable removeSongList(SongList songList);

    Observable loadData();

    Observable saveState(CurrentPlayStateBean currentPlayStateBean);

    Observable loadPlayList();

}
