package com.sandy.pagepanel.observe;

import android.os.Handler;
import android.os.Message;


public final class ObservableHandler extends Handler {

    private static ObservableHandler instance;

    private final EventDispatcher eventDispatcher;// 事件分发器

    private ObservableHandler() {
        eventDispatcher = new EventDispatcher();
    }

    public static ObservableHandler getInstance() {

        if (instance == null) {
            synchronized (ObservableHandler.class) {
                if (instance == null) {
                    instance = new ObservableHandler();
                }
            }
        }

        return instance;
    }

    /**
     * 获得事件分发器
     *
     * @return 事件分发器
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void sendEvent(int whatType) {
        Message msg = obtainMessage(whatType);
        sendMessage(msg);
    }

    public void sendEvent(int whatType, Object obj) {
        Message msg = obtainMessage(whatType, obj);
        sendMessage(msg);
    }


    /**
     * Handle the received event, pass it to the listener.
     *
     * @param msg The event message.
     */
    @Override
    public void handleMessage(Message msg) {
        eventDispatcher.processMessage(msg);
    }
}
