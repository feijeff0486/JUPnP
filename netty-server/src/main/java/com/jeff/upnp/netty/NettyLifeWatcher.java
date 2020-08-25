package com.jeff.upnp.netty;

import android.os.Message;
import android.util.Log;

import com.jeff.jframework.core.CustomHandlerThread;
import com.jeff.jframework.core.UIHandler;

import java.util.LinkedList;

/**
 * 监听DLNA的Netty连接状态
 * <p>
 *
 * @author afei
 * @date 2020/07/08 20:32
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class NettyLifeWatcher {
    private static final String TAG = "NettyLifeWatcher";
    private static volatile NettyLifeWatcher sInstance;
    private final LinkedList<OnNettyLifeCallback> onNettyLifeCallbacks;
    private CustomHandlerThread mTimerHandlerThread;
    /**
     * 每隔32秒检测下是否还在运行，心跳包间隔为30秒
     */
    private static final int DELAY_ALIVE_CHECK = 32000;
    private static final int MSG_HEART_BEAT = 0x001;
    private static final int MSG_ON_DIE = 0x002;

    private NettyLifeWatcher() {
        onNettyLifeCallbacks = new LinkedList<>();
        setupTimeHandlerThread();
    }

    private void setupTimeHandlerThread() {
        if (mTimerHandlerThread == null) {
            mTimerHandlerThread = new CustomHandlerThread("netty-life-watcher") {
                @Override
                protected void processMessage(Message message) {
                    if (message.what == MSG_HEART_BEAT) {
                        //heart beat
                        removeMessage(MSG_ON_DIE);
                        sendMessageDelayed(obtainMessage(MSG_ON_DIE), DELAY_ALIVE_CHECK);
                    } else if (message.what == MSG_ON_DIE) {
                        //die
                        onDied();
                    }
                }
            };
        }
    }

    public static NettyLifeWatcher getInstance() {
        if (sInstance == null) {
            synchronized (NettyLifeWatcher.class) {
                if (sInstance == null) {
                    sInstance = new NettyLifeWatcher();
                }
            }
        }
        return sInstance;
    }

    void onConnected(){
        Log.d(TAG, "onConnected: ");
        mTimerHandlerThread.sendMessage(mTimerHandlerThread.obtainMessage(MSG_HEART_BEAT));
    }

    void onHeartBeat() {
        mTimerHandlerThread.sendMessage(mTimerHandlerThread.obtainMessage(MSG_HEART_BEAT));
        UIHandler.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onNettyLifeCallbacks != null) {
                    for (OnNettyLifeCallback callback : onNettyLifeCallbacks) {
                        callback.onHeatBeat();
                    }
                }
            }
        });
    }

    void onDied() {
        Log.e(TAG, "onDied: ");
        //主动调断开服务
        NettyUtils.getInstance().disconnect();
        UIHandler.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onNettyLifeCallbacks != null) {
                    for (OnNettyLifeCallback callback : onNettyLifeCallbacks) {
                        callback.onDied();
                    }
                }
            }
        });
    }

    public void release() {
        if (onNettyLifeCallbacks != null) {
            onNettyLifeCallbacks.clear();
        }
        if (mTimerHandlerThread != null) {
            mTimerHandlerThread.destroy();
        }
    }

    public void addCallback(OnNettyLifeCallback callback) {
        if (callback == null) return;
        synchronized (onNettyLifeCallbacks) {
            if (!onNettyLifeCallbacks.contains(callback)) {
                onNettyLifeCallbacks.add(callback);
            }
        }
    }

    public void removeCallback(OnNettyLifeCallback callback) {
        if (callback == null) return;
        synchronized (onNettyLifeCallbacks) {
            onNettyLifeCallbacks.remove(callback);
        }
    }

    public interface OnNettyLifeCallback {
        /**
         * 心跳
         */
        void onHeatBeat();

        /**
         * 连接断开
         */
        void onDied();
    }
}
