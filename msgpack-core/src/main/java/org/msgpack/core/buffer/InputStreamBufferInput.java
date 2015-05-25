package org.msgpack.core.buffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link InputStream}
 */
public class InputStreamBufferInput implements MessageBufferInput {

    private InputStream in;
    private final int bufferSize;
    private boolean reachedEOF = false;

    public static MessageBufferInput newBufferInput(InputStream in) {
        checkNotNull(in, "InputStream is null");
        if (in instanceof FileInputStream) {
            FileChannel channel = ((FileInputStream) in).getChannel();
            if(channel != null) {
                return new ChannelBufferInput(channel);
            }
        }
        return new InputStreamBufferInput(in);
    }

    public InputStreamBufferInput(InputStream in) {
        this(in, 8192);
    }

    public InputStreamBufferInput(InputStream in, int bufferSize) {
        this.in = checkNotNull(in, "input is null");
        this.bufferSize = bufferSize;
    }

    /**
     * Reset Stream. This method doesn't close the old resource.
     * @param in new stream
     * @return the old resource
     */
    public InputStream reset(InputStream in) throws IOException {
        InputStream old = this.in;
        this.in = in;
        reachedEOF = false;
        return old;
    }


    @Override
    public MessageBuffer next() throws IOException {
        if(reachedEOF)
            return null;

        byte[] buffer = null;
        int cursor = 0;
        while(!reachedEOF && cursor < bufferSize) {
            if(buffer == null) {
                buffer = new byte[bufferSize];
            }

            int readLen = -1;
            // available() == 0 means, it reached the end of the stream
            if(in.available() == 0 ||
                (readLen = in.read(buffer, cursor, bufferSize - cursor)) == -1) {
                reachedEOF = true;
                break;
            }
            cursor += readLen;
        }

        return buffer == null ? null : MessageBuffer.wrap(buffer).slice(0, cursor);
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        }
        finally {

        }
    }
}
