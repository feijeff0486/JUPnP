package com.jeff.upnp.netty;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.jeff.jframework.tools.StringUtils;
import com.jeff.jframework.tools.preference.PreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
    public static final String TAG = "FileUploadClientHandler";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                Log.e(TAG, "长期没收到服务器推送数据");
                //可以选择重新连接
                String deviceIp=PreferencesUtils.getPreference().getString(Constants.SP_FLAG_DEVICE_IP_CACHED);
                if (!StringUtils.isEmpty(deviceIp) && !NettyUtils.getInstance().isActive()) {
                    NettyUtils.getInstance().connectNettyServer(deviceIp, "4");
                }
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                Log.i(TAG, "长期未向服务器发送数据");
                //发送心跳包
                try {
                    //发送消息到服务端
                    ctx.writeAndFlush(Unpooled.wrappedBuffer("心跳包".getBytes("UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                Log.i(TAG, "ALL");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        super.channelInactive(ctx);
        Log.i(TAG, "客户端结束传递文件channelInactive()");
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        super.channelActive(channelHandlerContext);
        Log.i(TAG, "正在执行channelActive()方法.....");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyLifeWatcher.getInstance().onHeartBeat();
        ByteBuf buf = (ByteBuf) msg;
        int byteRead = buf.readableBytes();
        byte[] req = new byte[byteRead];
        buf.readBytes(req);
        String content = new String(req, StandardCharsets.UTF_8);
        Log.i(TAG, "clientMsg=" + content);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        super.exceptionCaught(channelHandlerContext, throwable);
        Log.i(TAG, "exceptionCaught");
        throwable.printStackTrace();
        channelHandlerContext.close();
    }
}
