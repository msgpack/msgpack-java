package org.msgpack.core;

import org.msgpack.core.buffer.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Factory for creating {@link org.msgpack.core.MessagePacker} and {@link org.msgpack.core.MessageUnpacker}
 */
public class MessagePackFactory {

    private final MessagePack.Config config;

    public MessagePackFactory() {
        this(MessagePack.DEFAULT_CONFIG);
    }

    public MessagePackFactory(MessagePack.Config config) {
        this.config = config;
    }

    /**
     * Default MessagePackFactory
     */
    public static final MessagePackFactory DEFAULT = new MessagePackFactory(MessagePack.DEFAULT_CONFIG);

    /**
     * Create an MessagePacker that outputs the packed data to the specified stream
     * @param out
     */
    public MessagePacker newPacker(OutputStream out) {
        return new MessagePacker(new OutputStreamBufferOutput(out), config);
    }

    /**
     * Create an MessagePacker that outputs the packed data to the specified channel
     * @param channel
     */
    public MessagePacker newPacker(WritableByteChannel channel) {
        return new MessagePacker(new ChannelBufferOutput(channel), config);
    }

    /**
     * Create an MessageUnpacker that reads data from the given InputStream.
     * For reading data efficiently from byte[], use {@link MessageUnpacker(byte[])} or {@link MessageUnpacker(byte[], int, int)} instead of this constructor.
     *
     * @param in
     */
    public MessageUnpacker newUnpacker(InputStream in) {
        return new MessageUnpacker(InputStreamBufferInput.newBufferInput(in), config);
    }

    /**
     * Create an MessageUnpacker that reads data from the given ReadableByteChannel.
     * @param in
     */
    public MessageUnpacker newUnpacker(ReadableByteChannel in) {
        return new MessageUnpacker(new ChannelBufferInput(in), config);
    }


    /**
     * Create an MessageUnpacker that reads data from the given byte array.
     *
     * @param arr
     */
    public MessageUnpacker newUnpacker(byte[] arr) {
        return new MessageUnpacker(new ArrayBufferInput(arr), config);
    }

    /**
     * Create an MessageUnpacker that reads data from the given byte array [offset, offset+length)
     * @param arr
     * @param offset
     * @param length
     */
    public MessageUnpacker newUnpacker(byte[] arr, int offset, int length) {
        return new MessageUnpacker(new ArrayBufferInput(arr, offset, length), config);
    }



}
