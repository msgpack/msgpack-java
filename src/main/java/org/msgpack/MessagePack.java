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
import org.msgpack.packer.Unconverter;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.StreamUnpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
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

    public byte[] write(Object v) throws IOException {
        return write(v, getTemplate(v.getClass()));
    }

    public byte[] write(Object v, Template tmpl) throws IOException { // TODO IOException
        BufferPacker pk = new BufferPacker();
        tmpl.write(pk, v);
        return pk.toByteArray();
    }

    public void write(OutputStream out, Object v) throws IOException {
        write(out, v, getTemplate(v.getClass()));
    }

    public void write(OutputStream out, Object v, Template tmpl) throws IOException {
        StreamPacker pk = new StreamPacker(out);
        tmpl.write(pk, v);
    }

    public byte[] write(Value v) throws IOException {  // TODO IOException
        // FIXME ValueTemplate should do this
        BufferPacker pk = new BufferPacker();
        pk.write(v);
        return pk.toByteArray();
    }

    public Value read(byte[] b) throws IOException {  // TODO IOException
        return read(b, 0, b.length);
    }

    public Value read(byte[] b, int off, int len) throws IOException {  // TODO IOException
        return new BufferUnpacker().wrap(b, off, len).readValue();
    }

    public Value read(ByteBuffer buf) throws IOException {  // TODO IOException
        return new BufferUnpacker().wrap(buf).readValue();
    }

    public Value read(InputStream in) throws IOException {
        return new StreamUnpacker(in).readValue();
    }

    public <T> T read(byte[] b, T v) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T read(byte[] b, Class<T> c) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, null);
    }

    public <T> T read(ByteBuffer b, T v) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T read(ByteBuffer b, Class<T> c) {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return null;
    }

    public <T> T read(InputStream in, T v) throws IOException {
        // TODO
        Template tmpl = getTemplate(v.getClass());
        return (T)tmpl.read(new StreamUnpacker(in), v);
    }

    public <T> T read(InputStream in, Class<T> c) throws IOException {
        // TODO
        Template tmpl = getTemplate(c);
        return (T)tmpl.read(new StreamUnpacker(in), null);
    }

    public <T> T convert(Value v, T to) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(to.getClass());
        return (T)tmpl.read(new Converter(v), to);
    }

    public <T> T convert(Value v, Class<T> c) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = getTemplate(c);
        return (T)tmpl.read(new Converter(v), null);
    }

    public Value unconvert(Object v) throws IOException {  // TODO IOException
        Template tmpl = getTemplate(v.getClass());
        Unconverter pk = new Unconverter();
        tmpl.write(pk, v);
        return pk.getResult();
    }

    public Template getTemplate(Class<?> c) {
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


    private static final MessagePack globalMessagePack = new MessagePack();

    @Deprecated
    public static byte[] pack(Object obj) throws IOException {  // TODO IOException
        return globalMessagePack.write(obj);
    }

    @Deprecated
    public static void pack(OutputStream out, Object obj) throws IOException {
        globalMessagePack.write(out, obj);
    }

    @Deprecated
    public static byte[] pack(Object obj, Template tmpl) throws IOException {  // TODO IOException
        BufferPacker pk = new BufferPacker();
        tmpl.write(pk, obj);
        return pk.toByteArray();
    }

    @Deprecated
    public static void pack(OutputStream out, Object obj, Template tmpl) throws IOException {
        StreamPacker pk = new StreamPacker(out);
        tmpl.write(pk, obj);
    }

    @Deprecated
    public static Value unpack(byte[] buffer) throws IOException {
        return globalMessagePack.read(buffer);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, Template tmpl) throws IOException {
        BufferUnpacker u = new BufferUnpacker().wrap(buffer);
        return (T)tmpl.read(u, null);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, Template tmpl, T to) throws IOException {
        BufferUnpacker u = new BufferUnpacker().wrap(buffer);
        return (T)tmpl.read(u, to);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, Class<T> klass) throws IOException {
        return globalMessagePack.read(buffer, klass);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, T to) throws IOException {
        return globalMessagePack.read(buffer, to);
    }

    @Deprecated
    public static Value unpack(InputStream in) throws IOException {
        return globalMessagePack.read(in);
    }

    @Deprecated
    public static Object unpack(InputStream in, Template tmpl) throws IOException, MessageTypeException {
        return tmpl.read(new StreamUnpacker(in), null);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, Template tmpl, T to) throws IOException, MessageTypeException {
        return (T)tmpl.read(new StreamUnpacker(in), to);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, Class<T> klass) throws IOException {
        return globalMessagePack.read(in, klass);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, T to) throws IOException {
        return globalMessagePack.read(in, to);
    }
}

