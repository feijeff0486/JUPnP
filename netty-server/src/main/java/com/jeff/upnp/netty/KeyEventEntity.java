package com.jeff.upnp.netty;

import java.io.Serializable;

/**
 * 按键事件
 * <p>
 *
 * @author Jeff
 * @date 2020/8/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class KeyEventEntity implements Serializable {

    /**
     * type : 1
     * content : {"keyCode":"22","keyEvent":"KEYCODE_VOLUME_UP, Volume Up key"}
     */

    private int type;
    private Message content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Message getContent() {
        return content;
    }

    public void setContent(Message content) {
        this.content = content;
    }

    public static class Message implements Serializable{
        /**
         * keyCode : 22
         * keyEvent : KEYCODE_VOLUME_UP, Volume Up key
         */

        private int keyCode;
        private String keyEvent;
        private int streamlength;

        public int getStreamlength() {
            return streamlength;
        }

        public void setStreamlength(int streamlength) {
            this.streamlength = streamlength;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public void setKeyCode(int keyCode) {
            this.keyCode = keyCode;
        }

        public String getKeyEvent() {
            return keyEvent;
        }

        public void setKeyEvent(String keyEvent) {
            this.keyEvent = keyEvent;
        }
    }
}