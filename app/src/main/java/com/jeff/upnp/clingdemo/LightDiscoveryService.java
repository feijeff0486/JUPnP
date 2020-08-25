package com.jeff.upnp.clingdemo;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.InetAddress;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/24
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class LightDiscoveryService extends AndroidUpnpServiceImpl {
    private static final String TAG = "LightDiscoveryService";

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration(){
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return super.getRegistryMaintenanceIntervalMillis();
            }

//            @Override
//            public ServiceType[] getExclusiveServiceTypes() {
//                return new ServiceType[]{new UDAServiceType("SwitchPower")};
//            }

            @Override
            public Integer getRemoteDeviceMaxAgeSeconds() {
                return 0;
            }

            // Alive messages at regular intervals
            //默认情况下，该方法返回0，禁用活动消息泛溢，并依赖于本地设备广告的定期触发(这取决于每个LocalDeviceIdentity的最大年龄)。
            //如果您返回一个非零值，则会在给定的时间间隔内反复发送活动通知消息，远程控制点应该能够在此期间发现您的设备。当然，缺点是你的网络上有更多的流量。
            @Override
            public int getAliveIntervalMillis() {
                return 5000;
            }


//            @Override
//            public StreamClient createStreamClient() {
//                return new org.fourthline.cling.transport.impl.jetty.StreamClientImpl(
//                        new org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl(
//                                ThreadUtils.getIoPool()
//                        )
//                );
//            }
//
//            @Override
//            public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
//                return new org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl(
//                        new org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl(
//                                org.fourthline.cling.transport.impl.jetty.JettyServletContainer.INSTANCE,
//                                networkAddressFactory.getStreamListenPort()
//                        )
//                );
//            }

            @Override
            public MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory) {
                InetAddress group=networkAddressFactory.getMulticastGroup();
                int port=networkAddressFactory.getMulticastPort();
//                Log.d("afei", "createMulticastReceiver: group= "+group.toString()+", port= "+port);
                return super.createMulticastReceiver(networkAddressFactory);
            }
        };
    }
}
