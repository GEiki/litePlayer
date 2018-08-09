package com.dedaodemo.service;

/**
 * Created by guoss on 2018/8/8.
 */

public interface IMusicPlayer {
    void play(int index);

    void pause();

    void replay();

    void next();

    void previous();

    void changMode(String mode);

}
