package com.sandy.pagepanel.observe;


public final class TaskEvent {

    public static final int WHAT_SYS_NETWORK_ON = 11;//网络开启
    public static final int WHAT_SYS_NETWORK_OFF = 12;//网络关闭


    public static final int WHAT_HTTP_SUCCESS = 51;//请求网络成功
    public static final int WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL = 52;//请求网络成功，但业务失败
    public static final int WHAT_HTTP_ERROR = 53;//请求网络失败

    public static final int WHAT_COMMON_FAILURE = 54;//公共错误，且需要处理，这里有统一地方处理



    /*==========业务模块==========*/
    public static final int GET_SONG_LIST = 126;
    public static final int GET_SONG_LIST_LOAD_MORE = 127;


    private TaskEvent() {
        //Hide the constructor
    }
}
