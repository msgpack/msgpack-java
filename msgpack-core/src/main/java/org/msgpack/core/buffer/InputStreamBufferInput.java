package org.msgpack.core.buffer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link InputStream}
 */
public class InputStreamBufferInput implements MessageBufferInput {

    private final InputStream in;
    private final int bufferSize;

    private static Field bufField;
    private static Field bufPosField;
    private static Field bufCountField;

    private static Field getField(String name) {
        Field f = null;
        try {
            f = ByteArrayInputStream.class.getDeclaredField(name);
            f.setAccessible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    static {
        bufField = getField("buf");
        bufPosField = getField("pos");
        bufCountField = getField("count");
    }

    public static MessageBufferInput newBufferInput(InputStream in) {
        if(in instanceof ByteArrayInputStream) {
            ByteArrayInputStream b = (ByteArrayInputStream) in;
            try {
                // Extract a raw byte array from the ByteArrayInputStream
                byte[] buf = (byte[]) bufField.get(b);
                int pos = (Integer) bufPosField.get(b);
                int length = (Integer) bufCountField.get(b);
                return new ArrayBufferInput(buf, pos, length);
            }
            catch(Exception e) {
                // Failed to retrieve the raw byte array
            }
        } else if (in instanceof FileInputStream) {
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

    @Override
    public MessageBuffer next() throws IOException {
        byte[] buffer = null;
        int cursor = 0;
        while(cursor < bufferSize) {
            if(buffer == null)
                buffer = new byte[bufferSize];
            int readLen = in.read(buffer, cursor, bufferSize - cursor);
            if(readLen == -1) {
                break;
            }
            cursor += readLen;
        }
        if(buffer == null)
            return null;
        else {
            MessageBuffer m = MessageBuffer.wrap(buffer);
            return cursor == bufferSize ? m : m.slice(0, cursor);
        }
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
