package com.sandy.pagepanel.model;

import android.text.TextUtils;
import android.util.Log;

import com.sandy.pagepanel.base.ResponseCode;
import com.sandy.pagepanel.observe.TaskDataHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class BaseModel {
    private static final String TAG = BaseModel.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 记录异步操作的标识。
     */
    private final Set<String> requestingSet;


    BaseModel() {
        requestingSet = new HashSet<>();
    }

    /**
     * 清除所有的任务记录，runTask不在避免执行重复的正在执行的任务
     */
    public void clearRequestingSet() {
        synchronized (requestingSet) {
            if (requestingSet != null) {
                requestingSet.clear();
            }
        }
    }

    public static synchronized void purge() {
        if (executor != null && !executor.isTerminated()) {
            executor.shutdown();
        }
    }

    /**
     * 运行一个task任务
     *
     * @param listener  任务
     * @param event     请求任务的事件
     * @param timestamp 请求任务的时间戳
     * @param key       任务的唯一标识，用来标记任务，分辨任务是否已经完成，</p>
     *                  如果填null，则表示该任务不用受只能运行一次的限制，可以多次运行
     * @return 如果key不为null，且已经有标识为key的任务正在执行，则返回false，否则返回true
     */
    boolean runTask(final TaskListener listener, final int event, final long timestamp, final String key) {
        synchronized (requestingSet) {
            if (!TextUtils.isEmpty(key)) {
                // 如果该请求正在请求，就不再请求。
                if (requestingSet.contains(key)) {
                    Log.w(TAG, "It is running with task key : " + key);
                    return false;
                }
                requestingSet.add(key);
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    listener.request();
                } catch (Exception e) {
                    e.printStackTrace();
                    TaskDataHelper.handleError(event, ResponseCode.CODE_EXCEPTION, timestamp);
                }
                if (!TextUtils.isEmpty(key)) {
                    synchronized (requestingSet) {
                        requestingSet.remove(key);
                    }
                }
            }
        };
        executor.submit(r);

        return true;
    }

    public interface TaskListener {
        void request() throws Exception;
    }
}
