package com.sandy.pagepanel.base;


public final class ResponseCode {
    private ResponseCode() {
        //Hide the constructor
    }

    public static final int CODE_SUCCESS = 0; // 操作成功

    public static final int CODE_EXCEPTION = -100; // http没有连接通
    public static final int CODE_USER_INFO_EXCEPTION = -101;
    public static final int CODE_NEED_UPGRADE_EXCEPTION = -102;
}
