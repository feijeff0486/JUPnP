package com.jeff.upnp.netty;

import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 *
 * <p>
 * @author Jeff
 * @date 2020/08/23 14:22
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class NettyUtils {
    public static final String TAG = "NettyUtils";
    private static volatile NettyUtils sInstance;
    private Channel channel;
    private NettyConnector.OnServerConnectListener onServerConnectListener = null;

    private NettyUtils() {
    }

    public static NettyUtils getInstance() {
        if (sInstance == null) {
            synchronized (NettyUtils.class) {
                if (sInstance == null) {
                    sInstance = new NettyUtils();
                }
            }
        }
        return sInstance;
    }

    public void setServerConnectListener(NettyConnector.OnServerConnectListener serverConnectListener){
        this.onServerConnectListener = serverConnectListener;
    }

    public void removeConnectListener() {
        onServerConnectListener = null;
    }

    public boolean isActive(){
        return null != channel && channel.isActive();
    }

    public void write(byte[] data){
        if(null != channel){
            //发送消息到服务端
            channel.writeAndFlush(Unpooled.wrappedBuffer(data));
        }
    }

    public void write(String s){
        try {
            if(null != channel){
                //发送消息到服务端
                channel.writeAndFlush(Unpooled.wrappedBuffer(s.getBytes("UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        if(null != channel){
            channel.disconnect();
            channel.close();
            channel.closeFuture();
        }
    }
    public void start(File file) {
        if(null != channel){
            FileUploadUtils.getInstance().startByte(file,channel);
        }
    }
    /**
     * 通过IP去连接
     * @param string
     */
    public void connectNettyServer(String string, String tag) {
        Log.i(TAG,"connectNettyServer ip :" + string + "tag:" + tag);
        channel = new NettyConnector().connect(new InetSocketAddress(string, Constants.DEFAULT_SOCKET_PORT), this.onServerConnectListener);
    }
}
