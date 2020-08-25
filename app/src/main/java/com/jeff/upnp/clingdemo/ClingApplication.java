package com.jeff.upnp.clingdemo;

import android.app.Application;

import com.jeff.jframework.core.ContextUtils;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/24
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class ClingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.init(this);
    }
}
