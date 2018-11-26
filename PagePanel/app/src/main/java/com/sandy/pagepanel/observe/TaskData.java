package com.sandy.pagepanel.observe;


import static com.sandy.pagepanel.base.ResponseCode.CODE_SUCCESS;

public class TaskData {

    private int event; //子线程到主线程的动作事件ID
    private long timestamp; //请求的时间戳，可以唯一标示一个请求的ID

    private int code; //业务数据返回的code
    private String message = ""; //业务数据返回的消息
    private Object data; // 子线程到主线程之间传输的数据对象

    private boolean isCommonFailure;//公共异常，比如接口升级异常
    private int commonCodeFailureType;//公共异常大分类code

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int mEvent) {
        this.event = mEvent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.timestamp = mTimestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int mCode) {
        this.code = mCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mMsg) {
        this.message = mMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object mData) {
        this.data = mData;
    }

    public boolean isCommonFailure() {
        return isCommonFailure;
    }

    public void setCommonFailure(boolean isBaseCommonFailure) {
        this.isCommonFailure = isBaseCommonFailure;
    }

    public int getCommonCodeFailureType() {
        return commonCodeFailureType;
    }

    public void setCommonCodeFailureType(int commonCodeFailureType) {
        this.commonCodeFailureType = commonCodeFailureType;
    }
}
