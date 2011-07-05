//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import org.msgpack.template.Template;
import org.msgpack.packer.Packer;
import org.msgpack.packer.StreamPacker;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.StreamUnpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.value.Value;
import org.msgpack.template.*;

public class MessagePack {
    private TemplateRegistry registry;

    public MessagePack() {
	registry = new TemplateRegistry();
    }

    public MessagePack(MessagePack parent) {
	registry = new TemplateRegistry(parent.registry);
    }

    public byte[] pack(Object v) throws IOException {
	return pack(v, getTemplate(v.getClass()));
    }

    public byte[] pack(Object v, Template tmpl) throws IOException { // TODO IOException
	BufferPacker pk = new BufferPacker();
	tmpl.write(pk, v);
	return pk.toByteArray();
    }

    public void pack(OutputStream out, Object v) throws IOException {
	pack(out, v, getTemplate(v.getClass()));
    }

    public void pack(OutputStream out, Object v, Template tmpl) throws IOException {
        StreamPacker pk = new StreamPacker(out);
        tmpl.write(pk, v);
        pk.flush();
    }

    public byte[] pack(Value v) throws IOException {  // TODO IOException
        // FIXME ValueTemplate should do this
        BufferPacker pk = new BufferPacker();
        pk.write(v);
        return pk.toByteArray();
    }

    public <T> T unpack(InputStream in, T v) throws IOException {
        // TODO
        Template tmpl = getTemplate(v.getClass());
        return (T)tmpl.read(new StreamUnpacker(in), v);
    }

    public <T> T unpack(InputStream in, Class<T> c) throws IOException {
        // TODO
        Template tmpl = getTemplate(c);
        return (T)tmpl.read(new StreamUnpacker(in), null);
    }

    public Value unpack(byte[] b) throws IOException {  // TODO IOException
        return unpack(b, 0, b.length);
    }

    public Value unpack(byte[] b, int off, int len) throws IOException {  // TODO IOException
        return new BufferUnpacker().wrap(b, off, len).readValue();
    }

    public Value unpack(ByteBuffer buf) throws IOException {  // TODO IOException
        return new BufferUnpacker().wrap(buf).readValue();
    }

    public <T> T unpack(byte[] b, T v) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T unpack(byte[] b, Class<T> c) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, null);
    }

    public <T> T unpack(ByteBuffer b, T v) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T unpack(ByteBuffer b, Class<T> c) {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return null;
    }

    protected Template getTemplate(Class<?> c) {
        Template tmpl = registry.lookup(c);
        if(tmpl == null) {
            throw new MessageTypeException("Can't find template for "+c+" class. Try to add @Message annotation to the class or call MessagePack.register(Type).");
        }
        return tmpl;
    }

    public void register(Class<?> type) {
	registry.register(type);
    }

    public void registerTemplate(Class<?> type, Template tmpl) {
        registry.register(type, tmpl);
    }

    /*
    // TODO
    private static final MessagePack globalMessagePack;

    @Deprecated
    public static <T> T unpack(InputStream in, T v) {
        return globalMessagePack.unpack(in, v);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, Class<T> c) {
        return globalMessagePack.unpack(in, c);
    }

    @Deprecated
    public static <T> T unpack(byte[] b, T v) {
        return globalMessagePack.unpack(b, v);
    }

    @Deprecated
    public static <T> T unpack(byte[] b, Class<T> c) {
        return globalMessagePack.unpack(b, c);
    }

    @Deprecated
    public static <T> T unpack(ByteBuffer b, T v) {
        return globalMessagePack.unpack(b, v);
    }

    @Deprecated
    public static <T> T unpack(ByteBuffer b, Class<T> c) {
        return globalMessagePack.unpack(b, c);
    }
    */


}

