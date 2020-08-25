/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.jeff.upnp.clingdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 设备
 * <p>
 * @author Jeff
 * @date 2020/08/21 18:23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
// DOC:CLASS
public class LightDeviceActivity extends Activity implements PropertyChangeListener {

    // DOC:CLASS
    private static final Logger log = Logger.getLogger(LightDeviceActivity.class.getName());

    // DOC:SERVICE_BINDING
    private AndroidUpnpService upnpService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            LocalService<SwitchPower> switchPowerService = getSwitchPowerService();
//            LocalService<KeyEventControl> keyEventControlService = getKeyEventControlService();

            // Register the device when this activity binds to the service for the first time
            if (switchPowerService == null/*||keyEventControlService==null*/) {
                try {
                    LocalDevice binaryLightDevice = DeviceFactory.createDevice();

                    Toast.makeText(LightDeviceActivity.this, R.string.registeringDevice, Toast.LENGTH_SHORT).show();
                    upnpService.getRegistry().addDevice(binaryLightDevice);
                    //设置发现策略
                    upnpService.getRegistry().setDiscoveryOptions(DeviceFactory.sUDN,new DiscoveryOptions(true,true));

                    switchPowerService = getSwitchPowerService();
//                    keyEventControlService=getKeyEventControlService();
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Creating BinaryLight device failed", ex);
                    Toast.makeText(LightDeviceActivity.this, R.string.createDeviceFailed, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Obtain the state of the power switch and update the UI
            setLightbulb(switchPowerService.getManager().getImplementation().getStatus());

            // Start monitoring the power switch
            switchPowerService.getManager().getImplementation().getPropertyChangeSupport()
                    .addPropertyChangeListener(LightDeviceActivity.this);

//            keyEventControlService.getManager().getImplementation().getPropertyChangeSupport()
//                    .addPropertyChangeListener(LightDeviceActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DOC:LOGGING
        // Fix the logging integration between java.util.logging and Android internal logging
         org.seamless.util.logging.LoggingUtil.resetRootHandler(
            new FixedAndroidLogHandler()
         );
//         Logger.getLogger("org.fourthline.cling").setLevel(Level.FINE);
         Logger.getLogger("org.fourthline.cling").setLevel(Level.ALL);
//         Logger.getLogger("org.fourthline.cling.transport.spi.MulticastReceiver").setLevel(Level.FINE);
        // DOC:LOGGING

        setContentView(R.layout.activity_light_device);

        getApplicationContext().bindService(
                new Intent(this, LightDiscoveryService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop monitoring the power switch
        LocalService<SwitchPower> switchPowerService = getSwitchPowerService();
        if (switchPowerService != null)
            switchPowerService.getManager().getImplementation().getPropertyChangeSupport()
                    .removePropertyChangeListener(this);

        getApplicationContext().unbindService(serviceConnection);
    }

    protected LocalService<SwitchPower> getSwitchPowerService() {
        if (upnpService == null)
            return null;

        LocalDevice binaryLightDevice;
        if ((binaryLightDevice = upnpService.getRegistry().getLocalDevice(DeviceFactory.sUDN, true)) == null)
            return null;

        return (LocalService<SwitchPower>)
                binaryLightDevice.findService(new UDAServiceType("SwitchPower", 1));
    }
    // DOC:SERVICE_BINDING

    protected LocalService<KeyEventControl> getKeyEventControlService() {
        if (upnpService == null)
            return null;

        LocalDevice keyEventDevice;
        if ((keyEventDevice = upnpService.getRegistry().getLocalDevice(DeviceFactory.sUDN, true)) == null)
            return null;

        return (LocalService<KeyEventControl>)
                keyEventDevice.findService(new UDAServiceType("KeyEventControl", 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.switchRouter).setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, 1, 0, R.string.toggleDebugLogging).setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (upnpService != null) {
                    Router router = upnpService.get().getRouter();
                    try {
                        if (router.isEnabled()) {
                            Toast.makeText(this, R.string.disablingRouter, Toast.LENGTH_SHORT).show();
                            router.disable();
                        } else {
                            Toast.makeText(this, R.string.enablingRouter, Toast.LENGTH_SHORT).show();
                            router.enable();
                        }
                    } catch (RouterException ex) {
                        Toast.makeText(this, getText(R.string.errorSwitchingRouter) + ex.toString(), Toast.LENGTH_LONG).show();
                        ex.printStackTrace(System.err);
                    }
                }
                break;
            case 1:
                Logger logger = Logger.getLogger("org.fourthline.cling");
                if (logger.getLevel() != null && !logger.getLevel().equals(Level.INFO)) {
                    Toast.makeText(this, R.string.disablingDebugLogging, Toast.LENGTH_SHORT).show();
                    logger.setLevel(Level.INFO);
                } else {
                    Toast.makeText(this, R.string.enablingDebugLogging, Toast.LENGTH_SHORT).show();
                    logger.setLevel(Level.FINEST);
                }
                break;
        }
        return false;
    }

    // DOC:PROPERTY_CHANGE
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        // This is regular JavaBean eventing, not UPnP eventing!
        if ("status".equals(event.getPropertyName())) {
            log.info("Turning light: " + event.getNewValue());
            setLightbulb((Boolean) event.getNewValue());
        }
    }

    protected void setLightbulb(final boolean on) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.light_imageview);
                imageView.setImageResource(on ? R.drawable.light_on : R.drawable.light_off);
                // You can NOT externalize this color into /res/values/colors.xml. Go on, try it!
                imageView.setBackgroundColor(on ? Color.parseColor("#9EC942") : Color.WHITE);
            }
        });
    }
    // DOC:PROPERTY_CHANGE

}
// DOC:CLASS_END
