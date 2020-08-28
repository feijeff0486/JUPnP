package com.jeff.upnp.clingdemo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 控制点
 * <p>
 *
 * @author Jeff
 * @date 2020/08/21 18:23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class LightControlActivity extends ListActivity {
    private static final String TAG = "LightControlActivity";

    // DOC:CLASS
    // DOC:SERVICE_BINDING
    private ArrayAdapter<DeviceDisplay> listAdapter;

    private BrowseRegistryListener registryListener = new BrowseRegistryListener();

    private AndroidUpnpService upnpService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Clear the list
            listAdapter.clear();

            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);
            DeviceType deviceType = new UDADeviceType("BinaryLight");

            // Now add all devices to the list we already know about
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
//            upnpService.getControlPoint().search(new STAllHeader());
            upnpService.getControlPoint().search(new UDADeviceTypeHeader(deviceType));
//            upnpService.getControlPoint().search(new UDAServiceTypeHeader(new UDAServiceType("SwitchPower", 1)));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );
        // Now you can enable logging as needed for various categories of Cling:
//        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINE);
        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINE);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(listAdapter);

        // This will start the UPnP service if it wasn't already started
        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(serviceConnection);
    }
    // DOC:SERVICE_BINDING

    // DOC:MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.searchLAN).setIcon(android.R.drawable.ic_menu_search);
        // DOC:OPTIONAL
        menu.add(0, 1, 0, R.string.switchRouter).setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, 2, 0, R.string.toggleDebugLogging).setIcon(android.R.drawable.ic_menu_info_details);
        // DOC:OPTIONAL
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (upnpService == null)
                    break;
                Toast.makeText(this, R.string.searchingLAN, Toast.LENGTH_SHORT).show();
                upnpService.getRegistry().removeAllRemoteDevices();
                upnpService.getControlPoint().search(new STAllHeader());
                break;
            // DOC:OPTIONAL
            case 1:
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
            case 2:
                Logger logger = Logger.getLogger("org.fourthline.cling");
                if (logger.getLevel() != null && !logger.getLevel().equals(Level.INFO)) {
                    Toast.makeText(this, R.string.disablingDebugLogging, Toast.LENGTH_SHORT).show();
                    logger.setLevel(Level.INFO);
                } else {
                    Toast.makeText(this, R.string.enablingDebugLogging, Toast.LENGTH_SHORT).show();
                    logger.setLevel(Level.FINEST);
                }
                break;
            // DOC:OPTIONAL
        }
        return false;
    }
    // DOC:MENU

    private boolean targetValue = true;
    private int targetKeyCode = 24;

    private ServiceId serviceId = new UDAServiceId("SwitchPower");
    private ServiceId keyEventServiceId = new UDAServiceId("KeyEventControl");

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DeviceDisplay deviceDisplay = (DeviceDisplay) l.getItemAtPosition(position);

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(R.string.deviceDetails);
        dialog.setMessage(deviceDisplay.getDetailsMessage());
        dialog.setButton(
                getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Service switchPower;
                        if ((switchPower = deviceDisplay.getDevice().findService(serviceId)) != null) {
                            executeGetStatusAction(upnpService,switchPower);
                            executeAction(upnpService, switchPower, targetValue);
                        }
                        Service keyEvent;
                        if ((keyEvent = deviceDisplay.getDevice().findService(keyEventServiceId)) != null) {
                            executeKeyEventAction(upnpService, keyEvent, targetKeyCode);
                        }
                    }
                }
        );
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(12);
        super.onListItemClick(l, v, position, id);
    }

    protected class BrowseRegistryListener extends DefaultRegistryListener {
        private static final String TAG = "BrowseRegistryListener";

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
//            deviceAdded(device);

            StringBuilder deviceInfo = new StringBuilder("\nRemoteDevice={\nDisplayString=" + device.getDisplayString());
            // 遍历远程设备的服务Services
            for (RemoteService service : device.getServices()) {
                deviceInfo.append(",\nService={\nServiceId=" + service.getServiceId())
                        .append(",\nControlURI=" + service.getControlURI())
                        .append(",\nDescriptorURI=" + service.getDescriptorURI())
                        .append(",\nEventSubscriptionURI=" + service.getEventSubscriptionURI());
                for (Action action : service.getActions()) {
                    deviceInfo.append(",\nAction={").append(action.toString()).append("}");
                }
                deviceInfo.append("}");
            }
            deviceInfo.append("}");

            Log.d(TAG, "remoteDeviceDiscoveryStarted:" + deviceInfo.toString());
            Service switchPower;
            if ((switchPower = device.findService(serviceId)) != null) {
                Log.d(TAG, "Started remote device discover: " + switchPower);
                deviceAdded(device);
//                executeAction(upnpService, switchPower);
            }

            Service keyEvent;
            if ((keyEvent = device.findService(keyEventServiceId)) != null) {
                Log.d(TAG, "Started remote device discover: " + keyEvent);
                deviceAdded(device);
//                executeAction(upnpService, switchPower);
            }
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            Log.e(TAG, "Discovery failed of '" + device.getDisplayString() + "': "
                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            LightControlActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': "
                                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
            deviceRemoved(device);
        }
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

//        @Override
//        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
//            deviceAdded(device);
//        }
//
//        @Override
//        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
//            deviceRemoved(device);
//        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Service switchPower;
            if ((switchPower = device.findService(serviceId)) != null) {
                Log.d(TAG, "Service discovered: " + switchPower);
                deviceAdded(device);
//                executeAction(upnpService, switchPower);
            }

            Service keyEvent;
            if ((keyEvent = device.findService(keyEventServiceId)) != null) {
                Log.d(TAG, "Service discovered: " + keyEvent);
                deviceAdded(device);
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            Service switchPower;
            if ((switchPower = device.findService(serviceId)) != null) {
                Log.d(TAG, "Service disappeared: " + switchPower);
            }

            Service keyEvent;
            if ((keyEvent = device.findService(keyEventServiceId)) != null) {
                Log.d(TAG, "Service disappeared: " + keyEvent);
            }
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.remove(new DeviceDisplay(device));
                }
            });
        }
    }

    protected class DeviceDisplay {

        Device device;

        public DeviceDisplay(Device device) {
            this.device = device;
        }

        public Device getDevice() {
            return device;
        }

        // DOC:DETAILS
        public String getDetailsMessage() {
            StringBuilder sb = new StringBuilder();
//            if (getDevice().isFullyHydrated()) {
            sb.append(getDevice().getDisplayString());
            sb.append("\n\n");
            for (Service service : getDevice().getServices()) {
                sb.append(service.getServiceType()).append("\n");
            }
//            } else {
//                sb.append(getString(R.string.deviceDetailsNotYetAvailable));
//            }
            return sb.toString();
        }
        // DOC:DETAILS

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceDisplay that = (DeviceDisplay) o;
            return device.equals(that.device);
        }

        @Override
        public int hashCode() {
            return device.hashCode();
        }

        @Override
        public String toString() {
            String name =
                    getDevice().getDetails() != null && getDevice().getDetails().getFriendlyName() != null
                            ? getDevice().getDetails().getFriendlyName()
                            : getDevice().getDisplayString();
            // Display a little star while the device is being loaded (see performance optimization earlier)
            return device.isFullyHydrated() ? name + "[" + getDevice().getIpAddress() + "]" : name + "[" + getDevice().getIpAddress() + "] *";
        }
    }
    // DOC:CLASS_END
    // ...

    void executeAction(AndroidUpnpService upnpService, Service switchPowerService, boolean value) {
        Action action = switchPowerService.getAction("SetTarget");
        if (action == null) {
            Log.e(TAG, "executeAction: action is null return!");
            return;
        }
        ActionInvocation setTargetInvocation =
                new SetTargetActionInvocation(action, value);

        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(
                new ActionCallback(setTargetInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
                        Log.d(TAG, "executeAction::ActionCallback::success Successfully called action!");
//                        targetValue = !targetValue;
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Log.e(TAG, "executeAction::ActionCallback::failure " + defaultMsg);
                    }
                }
        );
    }

    void executeKeyEventAction(AndroidUpnpService upnpService, Service keyEventService, int keyCode) {
        Action action = keyEventService.getAction("AcceptKeyEvent");
        if (action == null) {
            Log.e(TAG, "executeKeyEventAction: action is null return!");
            return;
        }
        ActionInvocation keyEventActionInvocation =
                new KeyEventActionInvocation(action, keyCode);

        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(
                new ActionCallback(keyEventActionInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
                        Log.d(TAG, "executeKeyEventAction::ActionCallback::success Successfully called action!");
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Log.e(TAG, "executeKeyEventAction::ActionCallback::failure " + defaultMsg);
                    }
                }
        );
    }

    void executeGetStatusAction(AndroidUpnpService upnpService, Service keyEventService) {
        Action action = keyEventService.getAction("GetTarget");
        if (action == null) {
            Log.e(TAG, "executeGetStatusAction: action is null return!");
            return;
        }
        ActionInvocation getTargetActionInvocation =
                new GetTargetActionInvocation(action,targetValue);

        // Executes asynchronous in the background
        upnpService.getControlPoint().execute(
                new ActionCallback(getTargetActionInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
                        targetValue=!targetValue;
                        Log.d(TAG, "executeGetStatusAction::ActionCallback::success Successfully called action!");
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Log.e(TAG, "executeGetStatusAction::ActionCallback::failure " + defaultMsg);
                    }
                }
        );
    }

    static class SetTargetActionInvocation extends ActionInvocation {

        SetTargetActionInvocation(Action action, boolean value) {
            super(action);
            try {

                // Throws InvalidValueException if the value is of wrong type
                setInput("NewTargetValue", value);
            } catch (InvalidValueException ex) {
                Log.e(TAG, "SetTargetActionInvocation: " + ex.getMessage());
            }
        }
    }

    static class KeyEventActionInvocation extends ActionInvocation {

        KeyEventActionInvocation(Action action, int keyCode) {
            super(action);
            try {

                // Throws InvalidValueException if the value is of wrong type
                setInput("NewKeyEvent", keyCode);
            } catch (InvalidValueException ex) {
                Log.e(TAG, "KeyEventActionInvocation: " + ex.getMessage());
            }
        }
    }

    static class GetTargetActionInvocation extends ActionInvocation {

        GetTargetActionInvocation(Action action, boolean value) {
            super(action);
            try {

                // Throws InvalidValueException if the value is of wrong type
                setOutput("RetTargetValue", value);
            } catch (InvalidValueException ex) {
                Log.e(TAG, "GetTargetActionInvocation: " + ex.getMessage());
            }
        }
    }
}
// DOC:CLASS_END
