package com.jeff.upnp.clingdemo;

import android.app.Instrumentation;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.jeff.jframework.core.CustomHandlerThread;
import com.jeff.jframework.tools.LogUtils;


/**
 * 命令执行按键事件
 *
 * @author Jeff
 * @date 2020/5/21
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 * <p>
 */
public final class KeyEventExecutor {
    private static final String TAG = "KeyEventExecutor";
    private Instrumentation inst = new Instrumentation();
    private CustomHandlerThread mHandlerThread;

    public KeyEventExecutor() {
        this.mHandlerThread = new CustomHandlerThread(TAG) {
            @Override
            public void processMessage(Message msg) {
            }
        };
    }

    public void clickHome() {
        click(KeyEvent.KEYCODE_HOME);
    }

    public void clickBack() {
        click(KeyEvent.KEYCODE_BACK);
    }

    public void clickMenu() {
        click(KeyEvent.KEYCODE_MENU);
    }

    public void click(final int keycode) {
        LogUtils.iTag(TAG, "click-" + keycode);
        if (this.mHandlerThread != null) {
            this.mHandlerThread.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        KeyEventExecutor.this.inst.sendKeyDownUpSync(keycode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void touch(int pozx,int pozy){
        //pozx goes from 0 to SCREEN WIDTH , pozy goes from 0 to SCREEN HEIGHT
        this.inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,pozx, pozy, 0));
        this.inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,pozx, pozy, 0));
    }

    public void longPress(final int keycode) {
        LogUtils.iTag(TAG, "longPress-" + keycode);
        if (this.mHandlerThread != null) {
            this.mHandlerThread.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        KeyEventExecutor.this.inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        KeyEventExecutor.this.inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                        KeyEventExecutor.this.inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP, keycode));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void destroy() {
        mHandlerThread.destroy();
    }
}
