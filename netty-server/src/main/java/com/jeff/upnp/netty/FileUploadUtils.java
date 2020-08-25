package com.jeff.upnp.netty;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class FileUploadUtils {
    private static final String TAG = "FileUploadUtils";

    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
    private FileUploadEntity fileUploadFile;

    private static volatile FileUploadUtils sInstance;
    private KeyEventEntity keyCodeEntity;

    private FileUploadUtils() {
    }

    public static FileUploadUtils getInstance() {
        if (sInstance == null) {
            synchronized (FileUploadUtils.class) {
                if (sInstance == null) {
                    sInstance = new FileUploadUtils();
                }
            }
        }
        return sInstance;
    }

    public int startByte(File file , Channel channel){
        randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file,
                    "r");
            randomAccessFile.seek(0);
            Log.i(TAG,"11randomAccessFile length：" + randomAccessFile.length());
            lastLength = (int)randomAccessFile.length();
            byte[] bytes = new byte[lastLength];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {

                if(null == keyCodeEntity){
                    keyCodeEntity = new KeyEventEntity();
                }
                keyCodeEntity.setType(2);
                KeyEventEntity.Message message = new KeyEventEntity.Message();
                message.setKeyCode(8);
                message.setKeyEvent("voiceStart");
                message.setStreamlength(byteRead);
                keyCodeEntity.setContent(message);
                String json = new Gson().toJson(keyCodeEntity);
                channel.writeAndFlush(Unpooled.wrappedBuffer(json.getBytes("UTF-8")));//发送消息到服务端

                Thread.sleep(1000);

                channel.writeAndFlush(Unpooled.wrappedBuffer(bytes));//发送消息到服务端
            }
            Log.i(TAG,"channelActive()文件已经读完 " + byteRead);
            randomAccessFile.close();
        }catch (Exception e){
            Log.i(TAG,"start Exception ");
            e.printStackTrace();
            channel.close();
        }finally {
            if(file.exists()){
                file.delete();
            }
        }
        return byteRead;
    }

    public void start(FileUploadEntity file , Channel channel){
        /*this.fileUploadFile = file;
        start = 0;
        lastLength= 0;
        byteRead = 0;
        randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(),
                    "r");
            randomAccessFile.seek(fileUploadFile.getStarPos());
            Log.i(TAG,"11randomAccessFile length：" + randomAccessFile.length());
            lastLength = 1024 * 10;
            byte[] bytes = new byte[lastLength];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                fileUploadFile.setEndPos(byteRead);
                fileUploadFile.setBytes(bytes);
                String json = new Gson().toJson(fileUploadFile);
                channel.writeAndFlush(Unpooled.wrappedBuffer(json.getBytes("UTF-8")));//发送消息到服务端
            }
            Log.i(TAG,"channelActive()文件已经读完 " + byteRead);
        }catch (Exception e){
            Log.i(TAG,"start Exception ");
            e.printStackTrace();
            channel.close();
        }*/
    }


    public void loadfile(int msg, ChannelHandlerContext ctx){
        /*try{
            start = msg;
            Log.i(TAG,"start：" + start);
            if (start != -1) {
                randomAccessFile = new RandomAccessFile(this.fileUploadFile.getFile(), "r");
                randomAccessFile.seek(start); //将文件定位到start
                Log.i(TAG,"randomAccessFile length：" + randomAccessFile.length());
                Log.i(TAG,"长度：" + (randomAccessFile.length() - start));
                int a = (int) (randomAccessFile.length() - start);
                int b = (int) (randomAccessFile.length() / 1024 * 2);
                if (a < lastLength) {
                    lastLength = a;
                }
                Log.i(TAG,"文件长度：" + (randomAccessFile.length()) +
                        ",start:" + start + ",a:" + a + ",b:" + b + ",lastLength:" + lastLength);
                byte[] bytes = new byte[lastLength];
                Log.i(TAG,"bytes的长度是="+bytes.length);
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                    Log.i(TAG,"byteRead = "  + byteRead);
                    fileUploadFile.setEndPos(byteRead);
                    fileUploadFile.setBytes(bytes);
                    try {
                        String json = new Gson().toJson(fileUploadFile);
                        ctx.writeAndFlush(Unpooled.wrappedBuffer(json.getBytes("UTF-8")));//发送消息到服务端
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    randomAccessFile.close();
                    Log.i(TAG,"文件已经读完channelRead()--------" + byteRead);
                }
            }
        }catch (Exception e){
            exceptionCaught(ctx,e);
        }
*/
    }
    public void loadafile(int msg, ChannelHandlerContext ctx){
        /*if(msg == fileUploadFile.getBytes().length){
            ctx.writeAndFlush(-1);
        }*/
    }
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        Log.i(TAG,"exceptionCaught");
        throwable.printStackTrace();
        channelHandlerContext.close();
    }

    public void loadafilebyte(int count, ChannelHandlerContext ctx) {
        try {
            if(count >= byteRead){
                //发送消息到服务端
                ctx.writeAndFlush(Unpooled.buffer().writeBytes("finish".getBytes("UTF-8")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
