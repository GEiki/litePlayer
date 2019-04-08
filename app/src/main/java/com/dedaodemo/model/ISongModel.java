package com.dedaodemo.model;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SongList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 01377578 on 2018/7/27.
 */

public interface ISongModel {

    /**
     * 添加歌曲到歌单
     */
    Observable addSong(SongList songList, Item item);

    Observable addSongs(SongList songList, ArrayList<Item> list);



    /**
     * 从歌单中移除歌曲
     */
    Observable removeSong(SongList songList, ArrayList<Item> item);

    /**
     * 加载歌单中的歌曲
     */
    Observable<List<Item>> loadSongData(SongList songList);
}
