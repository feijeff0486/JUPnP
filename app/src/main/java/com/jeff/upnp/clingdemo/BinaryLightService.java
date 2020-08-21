package com.jeff.upnp.clingdemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.LocalDevice;

import java.io.IOException;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/21
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class BinaryLightService extends AndroidUpnpServiceImpl {
    private static final String TAG = "BinaryLightService";
    private LocalDevice device;

    public static void start(Context context) {
        try {
            Intent intent = new Intent(context, BinaryLightService.class);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        try {
            device = DeviceFactory.createDevice();
            this.binder.getRegistry().addDevice(device);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
