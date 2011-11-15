package org.msgpack.template;

import java.io.IOException;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class NotNullableTemplate<T> extends AbstractTemplate<T> {

    private Template<T> tmpl;

    public NotNullableTemplate(Template<T> elementTemplate) {
        tmpl = elementTemplate;
    }

    @Override
    public void write(Packer pk, T v, boolean required) throws IOException {
        tmpl.write(pk, v, required);
    }

    @Override
    public void write(Packer pk, T v) throws IOException {
        write(pk, v, true);
    }

    @Override
    public T read(Unpacker u, T to, boolean required) throws IOException {
        return tmpl.read(u, to, required);
    }

    @Override
    public T read(Unpacker u, T to) throws IOException {
        return read(u, to, true);
    }
}
