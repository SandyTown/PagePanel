package com.sandy.pagepanel.observe;


import com.sandy.pagepanel.base.BaseResponse;
import com.sandy.pagepanel.base.ResponseCode;

public class TaskDataHelper {

    private TaskDataHelper() {
        //Hide the constructor
    }


    /**
     * 发送网络请求成功事件
     *
     * @param baseResponse 已经解析了的返回数据
     * @param event        请求的事件
     * @param timestamp    请求的时间戳，可以唯一标示一个请求的ID
     */
    public static void handleResponseMessage(BaseResponse baseResponse, int event, long timestamp) {
        if (baseResponse != null) {
            TaskData taskData = new TaskData();
            taskData.setEvent(event);
            taskData.setTimestamp(timestamp);
            taskData.setCode(baseResponse.getCode());
            taskData.setMessage(baseResponse.getMsg());
            taskData.setData(baseResponse);
            if (taskData.getCode() == ResponseCode.CODE_SUCCESS) {
                ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_SUCCESS, taskData);
            } else if (taskData.getCode() == ResponseCode.CODE_NEED_UPGRADE_EXCEPTION) {
                taskData.setCode(ResponseCode.CODE_NEED_UPGRADE_EXCEPTION);
                taskData.setCommonFailure(true);
                taskData.setCommonCodeFailureType(ResponseCode.CODE_NEED_UPGRADE_EXCEPTION);

                ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL, taskData);
                ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_COMMON_FAILURE, taskData);
            } else {
                ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL, taskData);
            }
        } else {
            handleError(event, ResponseCode.CODE_EXCEPTION, timestamp);
        }

    }


    /**
     * 发送错误消息
     *
     * @param event         请求的事件ID
     * @param exceptionCode 异常类型
     * @param timestamp     请求的时间戳，可以唯一标示一个请求的ID
     */
    public static void handleError(int event, int exceptionCode, long timestamp) {
        TaskData taskData = new TaskData();
        taskData.setEvent(event);
        taskData.setTimestamp(timestamp);
        taskData.setCode(exceptionCode);
        taskData.setMessage("");
        ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_ERROR, taskData);
    }

    public static void handleUserInfoError(int event, long timestamp) {
        TaskData taskData = new TaskData();
        taskData.setEvent(event);
        taskData.setTimestamp(timestamp);
        taskData.setCode(ResponseCode.CODE_USER_INFO_EXCEPTION);
        taskData.setMessage("用户信息错误，请登录!");

        taskData.setCommonFailure(true);
        taskData.setCommonCodeFailureType(ResponseCode.CODE_USER_INFO_EXCEPTION);

        ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL, taskData);
        ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_COMMON_FAILURE, taskData);
    }

    /**
     * 发送错误的消息
     *
     * @param event     请求的事件ID
     * @param timestamp 请求的时间戳，可以唯一标示一个请求的ID
     */
    public static void handleUpgradeError(int event, long timestamp) {
        TaskData taskData = new TaskData();
        taskData.setEvent(event);
        taskData.setTimestamp(timestamp);
        taskData.setCode(ResponseCode.CODE_NEED_UPGRADE_EXCEPTION);
        taskData.setMessage("");
        taskData.setCommonFailure(true);
        taskData.setCommonCodeFailureType(ResponseCode.CODE_NEED_UPGRADE_EXCEPTION);

        ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_HTTP_SUCCESS_BUT_BUSINESS_FAIL, taskData);
        ObservableHandler.getInstance().sendEvent(TaskEvent.WHAT_COMMON_FAILURE, taskData);
    }
}

