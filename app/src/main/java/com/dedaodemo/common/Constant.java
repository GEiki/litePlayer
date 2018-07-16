package com.dedaodemo.common;

/**
 * Created by guoss on 2018/5/1.
 */

public class Constant {
    public static final String MUSIC_SEARCH_URL = "https://api.hibai.cn/api/index/index";

    //网易云音乐搜索代码
    public static final String TYPE_WY = "020116";
    //QQ音乐搜索代码
    public static final String TYPE_QQ = "020336";
    //酷狗音乐搜索代码
    public static final String TYPE_KG = "020225";
    //下载路径
    public static String DOWNLOAD_PATH = "";

    //sharePreferenceKey
    public static final String SP_KEY = "MUSIC";
    //启动标志
    public static final String SP_KEY_LANUCH = "LANUCH_FLAG";

    //当前歌曲
    public static final String CURRENT_SONG = "CUR_SONG";
    //当前歌单
    public static final String CURRENT_SONGLIST = "CUR_SONG_LIST";
    //当前模式
    public static final String CURRENT_MODE = "CUR_MODE";
    //时长
    public static final String DURATION = "duration";
    //进度
    public static final String POSITION = "position";


    //播放
    public static final int ACTION_PLAY = 1;
    //暂停
    public static final int ACTION_PAUSE = 2;
    //上一首
    public static final int ACTION_PRE_SONG = 3;
    //下一首
    public static final int ACTION_NEXT_SONG = 4;
    //继续播放
    public static final int ACTION_RE_PLAY = 5;
    //播放完成
    public static final int ACTION_COMPLETE = 6;
    //获取进度
    public static final int ACTION_REQUEST_DURATION = 7;
    //定位
    public static final int ACTION_SEEK_TO = 8;
    //初始化
    public static final int ACTION_INIT = 9;


    /**
     * 播放顺序
     */
    //顺序
    public static final String ORDER = "order";
    //随机
    public static final String RANDOM = "random";
    //列表循环
    public static final String LIST_RECYCLE = "list_recycle";
    //单曲循环
    public static final String SINGLE_RECYCLE = "single_recycle";

    //搜索列表名
    public static final String SEARCH_SONG_LIST = "SeArCh";

    //播放错误
    public static final String PLAY_ERROR = "play_error";

}
