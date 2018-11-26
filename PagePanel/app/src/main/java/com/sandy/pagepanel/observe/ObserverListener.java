package com.sandy.pagepanel.observe;

import android.os.Message;


public interface ObserverListener {

    /**
     * 异步事件处理回调
     *
     * @param whatType 事件大类型
     * @param message  数据
     */
    void onEvent(int whatType, Message message);
}
