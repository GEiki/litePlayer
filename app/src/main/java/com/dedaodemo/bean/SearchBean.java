package com.dedaodemo.bean;

import java.io.Serializable;

/**
 * Created by 01377578 on 2018/6/27.
 */

public class SearchBean implements Serializable {

    /**
     * 搜索平台
     **/
    private String searchType;
    /**
     * 搜索歌曲关键词
     */
    private String key;

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
