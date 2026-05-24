package org.msgpack.jackson.dataformat.benchmark;

import java.io.OutputStream;

public class NopOutputStream
        extends OutputStream
{
    private int size;

    @Override
    public void write(int b)
    {
        size++;
    }

    @Override
    public void write(byte[] b, int off, int len)
    {
        size += len;
    }

    public int size()
    {
        return size;
    }
}
