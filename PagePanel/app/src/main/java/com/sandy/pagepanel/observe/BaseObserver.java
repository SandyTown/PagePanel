package com.sandy.pagepanel.observe;

import android.os.Message;

import java.util.HashSet;
import java.util.Set;


public abstract class BaseObserver implements ObserverListener {


    private final Set<Long> timestamps = new HashSet<>();
    private final EventDispatcher eventDispatcher = ObservableHandler.getInstance().getEventDispatcher();


    public void registerListener() {
        eventDispatcher.registerListener(this);
    }


    public void unregisterListener() {
        eventDispatcher.unregisterListener(this);
    }

    protected long genTimestamp() {
        long timestamp = System.currentTimeMillis();
        timestamps.add(timestamp);
        return timestamp;
    }

    protected boolean containTimestamp(long timestamp) {
        return timestamps.contains(timestamp);
    }

    @Override
    public void onEvent(int whatType, Message message) {

        switch (whatType) {
            case TaskEvent.WHAT_HTTP_SUCCESS:
                onRequestSuccess((TaskData) message.obj);
                break;
            case TaskEvent.WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL:
                onRequestSuccessButBusinessFail((TaskData) message.obj);
                break;
            case TaskEvent.WHAT_HTTP_ERROR:
                onRequestError((TaskData) message.obj);
                break;
            case TaskEvent.WHAT_COMMON_FAILURE:
                onCommonFailure((TaskData) message.obj);
                break;
            default:
                break;
        }
    }

    protected void onRequestSuccess(TaskData taskData) {
        // 一般情况，这里都是子类处理。
    }

    protected void onRequestSuccessButBusinessFail(TaskData taskData) {
    }

    protected void onRequestError(TaskData taskData) {
    }


    protected void onCommonFailure(TaskData taskData) {
    }


}
