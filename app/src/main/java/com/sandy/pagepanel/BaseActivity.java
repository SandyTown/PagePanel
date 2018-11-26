package com.sandy.pagepanel;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.sandy.pagepanel.observe.EventDispatcher;
import com.sandy.pagepanel.observe.ObservableHandler;
import com.sandy.pagepanel.observe.ObserverListener;
import com.sandy.pagepanel.observe.TaskData;
import com.sandy.pagepanel.observe.TaskEvent;

import java.util.HashSet;
import java.util.Set;


public abstract class BaseActivity extends AppCompatActivity implements ObserverListener {


    protected boolean activityShowing;
    private final EventDispatcher eventDispatcher = ObservableHandler.getInstance().getEventDispatcher();
    private final Set<Long> timestamps = new HashSet<>();
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDispatcher.registerListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityShowing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityShowing = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventDispatcher.unregisterListener(this);
        dismissProgressDialog();
    }


    protected long genTimestamp() {
        long timestamp = System.currentTimeMillis();
        timestamps.add(timestamp);
        return timestamp;
    }

    protected boolean containTimestamp(long timestamp) {
        return timestamps.contains(timestamp);
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = showProgressDialog("数据加载中......", true);
        } else {
            progressDialog.show();
        }
    }

    protected void showProgressDialog(boolean isCancelable) {
        if (progressDialog == null) {
            progressDialog = showProgressDialog("数据加载中......", isCancelable);
        } else {
            progressDialog.show();
        }
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
            default:
                break;
        }
    }

    void onRequestSuccess(TaskData taskData) {
        // 一般情况，这里都是子类处理。
    }

    protected void onRequestSuccessButBusinessFail(TaskData taskData) {
        if (activityShowing) {
            if (taskData != null) {
                if (TextUtils.isEmpty(taskData.getMessage())) {
                    showToast("获取数据失败");
                } else {
                    showToast(taskData.getMessage());
                }
            } else {
                showToast("获取数据失败");
            }
        }
    }

    protected void onRequestError(TaskData taskData) {
        // 请求服务器错误类的提示。
        if (activityShowing) {
            showToast("获取数据失败");
        }
    }


    private Dialog showProgressDialog(String message, boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage(message);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(isCancelable);
        dialog.show();
        return dialog;
    }

    private Toast toast;

    private Toast showToast(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
        return toast;
    }

}
