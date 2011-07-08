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

import org.msgpack.template.FieldList;
import org.msgpack.template.Template;
import org.msgpack.packer.StreamPacker;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Unconverter;
import org.msgpack.unpacker.StreamUnpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.value.Value;


public class MessagePack {
    private TemplateRegistry registry;

    public MessagePack() {
	registry = new TemplateRegistry();
    }

    public MessagePack(MessagePack msgpack) {
	registry = new TemplateRegistry(msgpack.registry);
    }

    public byte[] pack(Object v) throws IOException {
	return pack(v, registry.lookup(v.getClass()));
    }

    public byte[] pack(Object v, Template tmpl) throws IOException {
	BufferPacker pk = new BufferPacker();
	tmpl.write(pk, v);
	return pk.toByteArray();
    }

    public void pack(OutputStream out, Object v) throws IOException {
	pack(out, v, registry.lookup(v.getClass()));
    }

    public void pack(OutputStream out, Object v, Template tmpl) throws IOException {
        StreamPacker pk = new StreamPacker(out);
        tmpl.write(pk, v);
    }

    public byte[] pack(Value v) throws IOException {
        // FIXME ValueTemplate should do this
        BufferPacker pk = new BufferPacker();
        pk.write(v);
        return pk.toByteArray();
    }

    public <T> T unpack(InputStream in, T v) throws IOException {
        Template tmpl = registry.lookup(v.getClass());
        return (T)tmpl.read(new StreamUnpacker(in), v);
    }

    public <T> T unpack(InputStream in, Class<T> c) throws IOException {
        Template tmpl = registry.lookup(c);
        return (T)tmpl.read(new StreamUnpacker(in), null);
    }

    public Value unpack(byte[] b) throws IOException {
        return unpack(b, 0, b.length);
    }

    public Value unpack(byte[] b, int off, int len) throws IOException {
        return new BufferUnpacker().wrap(b, off, len).readValue();
    }

    public Value unpack(ByteBuffer buf) throws IOException {
        return new BufferUnpacker().wrap(buf).readValue();
    }

    public <T> T unpack(byte[] b, T v) throws IOException {
        Template tmpl = registry.lookup(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T unpack(byte[] b, Class<T> c) throws IOException {
        Template tmpl = registry.lookup(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, null);
    }

    public <T> T unpack(ByteBuffer b, T v) throws IOException {
        Template tmpl = registry.lookup(v.getClass());
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return (T)tmpl.read(u, v);
    }

    public <T> T unpack(ByteBuffer b, Class<T> c) {
        Template tmpl = registry.lookup(c);
        BufferUnpacker u = new BufferUnpacker();
        u.wrap(b);
        return null;
    }

    public <T> T convert(Value v, T to) throws IOException {
        Template tmpl = registry.lookup(to.getClass());
        return (T)tmpl.read(new Converter(v), to);
    }

    public <T> T convert(Value v, Class<T> c) throws IOException {
        Template tmpl = registry.lookup(c);
        return (T) tmpl.read(new Converter(v), null);
    }

    public Value unconvert(Object v) throws IOException {
        Template tmpl = registry.lookup(v.getClass());
        Unconverter pk = new Unconverter();
        tmpl.write(pk, v);
        return pk.getResult();
    }

    public void register(Class<?> type) {
	registry.register(type);
    }

    // TODO #MN
    // public void forceRegister(Class<?> type);

    public void register(Class<?> type, Template tmpl) {
        registry.register(type, tmpl);
    }

    public void register(Class<?> type, FieldList flist) {
	registry.register(type, flist);
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

