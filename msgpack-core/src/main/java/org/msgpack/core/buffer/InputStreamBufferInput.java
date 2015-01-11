package org.msgpack.core.buffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            return new ChannelBufferInput(((FileInputStream) in).getChannel());
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

    public void reset(InputStream in) throws IOException {
        this.in.close();
        this.in = in;
        reachedEOF = false;
    }


    @Override
    public MessageBuffer next() throws IOException {
        if(reachedEOF)
            return null;

        byte[] buffer = null;
        int cursor = 0;
        while(!reachedEOF && cursor < bufferSize) {
            if(buffer == null)
                buffer = new byte[bufferSize];

            int readLen = in.read(buffer, cursor, bufferSize - cursor);
            if(readLen == -1) {
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
