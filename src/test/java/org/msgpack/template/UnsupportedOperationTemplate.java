package org.msgpack.template;

import java.io.IOException;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class UnsupportedOperationTemplate<T> implements Template<T> {

    @Override
    public void write(Packer pk, T v) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(Packer pk, T v, boolean required) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T read(Unpacker u, T to) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T read(Unpacker u, T to, boolean required) throws IOException {
        throw new UnsupportedOperationException();
    }
}
