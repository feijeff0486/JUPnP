package com.jeff.upnp.netty;

import java.io.Serializable;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/8/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class FileUploadEntity implements Serializable {

    /**
     * content : {"byteStream":"aa"}
     * type : 1
     */

    private Content content;
    private int type;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Content {
        /**
         * byteStream : aa
         */

        private byte[] byteStream;

        public byte[] getByteStream() {
            return byteStream;
        }

        public void setByteStream(byte[] byteStream) {
            this.byteStream = byteStream;
        }
    }
}
