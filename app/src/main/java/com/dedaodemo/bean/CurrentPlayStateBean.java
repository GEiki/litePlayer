package com.dedaodemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 01377578 on 2018/8/7.
 */

public class CurrentPlayStateBean implements Serializable {
    private List<Item> playList;
    private int index;
    private String mode;
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public List<Item> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Item> playList) {
        this.playList = playList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
