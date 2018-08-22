package com.dedaodemo.model;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Guoss on 2018/7/27.
 */

public interface ISearchModel {
    Observable<List<Item>> searchSongOnline(SearchBean bean);

}
