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

import android.util.Log;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

import java.beans.PropertyChangeSupport;

// DOC:CLASS
@UpnpService(
        serviceId = @UpnpServiceId("KeyEventControl"),
        serviceType = @UpnpServiceType(value = "KeyEventControl", version = 1)
)
public class KeyEventControl {
    private static final String TAG = "KeyEventControl";

    private final PropertyChangeSupport propertyChangeSupport;
    private final KeyEventExecutor eventExecutor = new KeyEventExecutor();

    public KeyEventControl() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "0")
    private int newKeyEvent = 0;

    @UpnpAction
    public void acceptKeyEvent(@UpnpInputArgument(name = "NewKeyEvent") int keyCode) {
        Log.d(TAG, "acceptKeyEvent: "+keyCode);
        eventExecutor.click(keyCode);
        // This will send a UPnP event, it's the name of a state variable that sends events
        getPropertyChangeSupport().firePropertyChange("KeyCode", 0, keyCode);
    }
}
// DOC:CLASS