package com.sandy.pagepanel.observe;

import android.os.Message;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EventDispatcher {

    private List<SoftReference<ObserverListener>> observerListeners;//观察者

    EventDispatcher() {
        observerListeners = new ArrayList<>();
    }

    /**
     * 注册数据的观察者
     *
     * @param listener 观察者
     */
    public synchronized void registerListener(ObserverListener listener) {
        if (listener != null) {
            Iterator<SoftReference<ObserverListener>> it = observerListeners.iterator();
            while (it.hasNext()) {
                ObserverListener l = it.next().get();
                if (l == listener) {
                    return;
                }
            }
            observerListeners.add(new SoftReference<>(listener));
        }
    }

    /**
     * 注销数据的观察者
     *
     * @param listener 观察者
     */
    public synchronized void unregisterListener(ObserverListener listener) {
        if (listener != null) {
            Iterator<SoftReference<ObserverListener>> it = observerListeners.iterator();
            while (it.hasNext()) {
                ObserverListener l = it.next().get();
                if (l == listener) {
                    it.remove();
                    return;
                }
            }
        }
    }

    /**
     * 数据分发给所有的观察者
     *
     * @param msg 数据
     */
    synchronized void processMessage(Message msg) {
        if (msg != null) {
            Iterator<SoftReference<ObserverListener>> it = observerListeners.iterator();
            while (it.hasNext()) {
                ObserverListener l = it.next().get();
                if (l == null) {
                    it.remove();
                } else {
                    l.onEvent(msg.what, msg);
                }
            }
        }
    }

}
