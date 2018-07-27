package com.dedaodemo.model;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 01377578 on 2018/7/27.
 */

public interface ISheetModel {
    Observable addSongs(SongList songList, ArrayList<Item> items);

    Observable createSongList(SongList songList, int size);

    Observable removeSongList(SongList songList);

    Observable loadData();

    Observable saveState(List<Item> list, Item item);
}