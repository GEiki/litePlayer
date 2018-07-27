package com.dedaodemo.model;

import com.dedaodemo.bean.SearchBean;

import io.reactivex.Observable;

/**
 * Created by Guoss on 2018/7/27.
 */

public interface ISearchModel {
    Observable searchSongOnline(SearchBean bean);

}
