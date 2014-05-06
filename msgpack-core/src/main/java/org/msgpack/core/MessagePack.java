package org.msgpack.core;

import java.io.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

/**
 * Entry point for creating MessagePacker and MessageUnpacker
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

    /**
     * Create a new MessagePacker that writes the message packed data to a file
     * @param outputFile
     * @return MessagePacker
     * @throws IOException if the target file cannot be created or opened
     */
    public static MessagePacker newPacker(File outputFile) throws IOException {
        return newPacker(new FileOutputStream(outputFile));
    }

    public static MessagePacker newPacker(OutputStream out) {
        // TODO
        return null;
    }

    public static MessagePacker newPacker(WritableByteChannel out) {
        // TODO
        return null;
    }

    /**
     * Create a new MessageUnpacker that decodes the message packed data in a file
     * @param inputFile
     * @return MessageUnpacker
     * @throws IOException if the input file is not found
     */
    public static MessageUnpacker newUnpacker(File inputFile) throws IOException {
        return newUnpacker(new FileInputStream(inputFile));
    }

    public static MessageUnpacker newUnpacker(InputStream in) {
        // TODO
        return null;
    }

    public static MessageUnpacker newUnpacker(ReadableByteChannel in) {
        // TODO
        return null;
    }

}
