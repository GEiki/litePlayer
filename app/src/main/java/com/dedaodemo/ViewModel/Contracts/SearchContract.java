package com.dedaodemo.ViewModel.Contracts;

import com.dedaodemo.bean.Item;
import com.dedaodemo.bean.SearchBean;

import java.util.ArrayList;

/**
 * Created by Guoss on 2018/6/28.
 */

public class SearchContract {


    public interface Presenter {
        public void searchSong(SearchBean bean);
    }

    public interface ViewModel {
        public void onSearchSuccess(ArrayList<Item> resultList);

        public void onSearchFail(String msg);
    }

    public interface Model {
        public void searchSongOnline(SearchBean bean);
    }
}
