package com.jeff.upnp.netty;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class NettyConnector {
    private static final String TAG = "SocketConnector";
    private Channel channel;
    private Bootstrap bootstrap;
    private OnServerConnectListener onServerConnectListener;

    public Channel connect(InetSocketAddress address, OnServerConnectListener listener) {
        doConnect(address, listener);
        return this.channel;
    }

    private void doConnect(InetSocketAddress address, OnServerConnectListener onServerConnectListener) {
        this.onServerConnectListener = onServerConnectListener;
        if (bootstrap == null) {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.SO_REUSEADDR, true);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("ping", new IdleStateHandler(60, 30, 60 * 10, TimeUnit.SECONDS));
                        ch.pipeline()
                                .addLast(
                                        new FileUploadClientHandler());

                    }
                });
                ChannelFuture f = bootstrap.connect(address);
                f.addListener(mConnectFutureListener);
                channel = f.channel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Channel mChannel;
    private ChannelFutureListener mConnectFutureListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture pChannelFuture) {
            if (pChannelFuture.isSuccess()) {
                mChannel = pChannelFuture.channel();
                NettyLifeWatcher.getInstance().onConnected();
                if (onServerConnectListener != null) {
                    onServerConnectListener.onConnectSuccess();
                }
                Log.i(TAG, "operationComplete: connected!");
            } else {
                if (onServerConnectListener != null) {
                    onServerConnectListener.onConnectFailed();
                }
                Log.i(TAG, "operationComplete: connect failed!");
            }
        }
    };

    public interface OnServerConnectListener {
        void onConnectSuccess();

        void onConnectFailed();
    }
}
