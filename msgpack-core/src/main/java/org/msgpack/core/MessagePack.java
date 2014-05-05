package org.msgpack.core;

/**
 * Created on 2014/05/05.
 */
public class MessagePack {

    public static class ExtendedTypeHeader {
        private final int type;
        private final int length;
        ExtendedTypeHeader(int type, int length) {
            this.type = type;
            this.length = length;
        }

        public int getType() {
            return type;
        }

        public int getLength() {
            return length;
        }
    }

}
