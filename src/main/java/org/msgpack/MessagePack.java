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
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.packer.Packer;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.packer.MessagePackBufferPacker;
import org.msgpack.packer.Unconverter;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.MessagePackUnpacker;
import org.msgpack.unpacker.MessagePackBufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;


public class MessagePack {
    private TemplateRegistry registry;

    public MessagePack() {
        registry = new TemplateRegistry();
    }

    public MessagePack(MessagePack msgpack) {
        registry = new TemplateRegistry(msgpack.registry);
    }

    public Packer createPacker(OutputStream stream) {
        return new MessagePackPacker(this, stream);
    }

    public BufferPacker createBufferPacker() {
        return new MessagePackBufferPacker(this);
    }

    public BufferPacker createBufferPacker(int bufferSize) {
        return new MessagePackBufferPacker(this, bufferSize);
    }

    public Unpacker createUnpacker(InputStream stream) {
        return new MessagePackUnpacker(this, stream);
    }

    public BufferUnpacker createBufferUnpacker() {
        return new MessagePackBufferUnpacker();
    }

    public BufferUnpacker createBufferUnpacker(byte[] b) {
        return createBufferUnpacker().wrap(b);
    }

    public BufferUnpacker createBufferUnpacker(byte[] b, int off, int len) {
        return createBufferUnpacker().wrap(b, off, len);
    }

    public BufferUnpacker createBufferUnpacker(ByteBuffer bb) {
        return createBufferUnpacker().wrap(bb);
    }


    public byte[] write(Object v) throws IOException {
        return write(v, registry.lookup(v.getClass()));
    }

    public <T> byte[] write(T v, Template<T> tmpl) throws IOException { // TODO IOException
        BufferPacker pk = createBufferPacker();
        tmpl.write(pk, v);
        return pk.toByteArray();
    }

    public void write(OutputStream out, Object v) throws IOException {
        write(out, v, registry.lookup(v.getClass()));
    }

    public <T> void write(OutputStream out, T v, Template<T> tmpl) throws IOException {
        Packer pk = createPacker(out);
        tmpl.write(pk, v);
    }

    public byte[] write(Value v) throws IOException {  // TODO IOException
        // FIXME ValueTemplate should do this
        BufferPacker pk = createBufferPacker();
        pk.write(v);
        return pk.toByteArray();
    }

    public Value read(byte[] b) throws IOException {  // TODO IOException
        return read(b, 0, b.length);
    }

    public Value read(byte[] b, int off, int len) throws IOException {  // TODO IOException
        return createBufferUnpacker(b, off, len).readValue();
    }

    public Value read(ByteBuffer buf) throws IOException {  // TODO IOException
        return createBufferUnpacker(buf).readValue();
    }

    public Value read(InputStream in) throws IOException {
        return createUnpacker(in).readValue();
    }

    public <T> T read(byte[] b, T v) throws IOException {  // TODO IOException
        // TODO
        Template tmpl = registry.lookup(v.getClass());
        BufferUnpacker u = createBufferUnpacker(b);
        return (T) tmpl.read(u, v);
    }

    public <T> T read(byte[] b, Class<T> c) throws IOException {  // TODO IOException
        Template<T> tmpl = registry.lookup(c);
        BufferUnpacker u = createBufferUnpacker(b);
        return tmpl.read(u, null);
    }

    public <T> T read(ByteBuffer b, T v) throws IOException {  // TODO IOException
        Template<T> tmpl = registry.lookup(v.getClass());
        BufferUnpacker u = createBufferUnpacker(b);
        return tmpl.read(u, v);
    }

    public <T> T read(ByteBuffer b, Class<T> c) {  // TODO IOException
        Template<T> tmpl = registry.lookup(c);
        BufferUnpacker u = createBufferUnpacker(b);
        return null;
    }

    public <T> T read(InputStream in, T v) throws IOException {
        Template<T> tmpl = registry.lookup(v.getClass());
        return tmpl.read(createUnpacker(in), v);
    }

    public <T> T read(InputStream in, Class<T> c) throws IOException {
        Template<T> tmpl = registry.lookup(c);
        return tmpl.read(createUnpacker(in), null);
    }

    public <T> T convert(Value v, T to) throws IOException {  // TODO IOException
        Template<T> tmpl = registry.lookup(to.getClass());
        return tmpl.read(new Converter(this, v), to);
    }

    public <T> T convert(Value v, Class<T> c) throws IOException {
        Template<T> tmpl = registry.lookup(c);
        return tmpl.read(new Converter(this, v), null);
    }

    public <T> Value unconvert(T v) throws IOException {
        Template<T> tmpl = registry.lookup(v.getClass());
        Unconverter pk = new Unconverter(this);
        tmpl.write(pk, v);
        return pk.getResult();
    }


    public void register(Class<?> type) {
        registry.register(type);
    }

    public <T> void register(Class<T> type, Template<T> tmpl) {
        registry.register(type, tmpl);
    }

    public boolean unregister(Class<?> type) {
	return registry.unregister(type);
    }

    public void unregister() {
	registry.unregister();
    }

    public <T> Template<T> lookup(Class<T> type) {
        return registry.lookup(type);
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
    public static <T> byte[] pack(T obj, Template<T> tmpl) throws IOException {  // TODO IOException
        return globalMessagePack.write(obj, tmpl);
    }

    @Deprecated
    public static <T> void pack(OutputStream out, T obj, Template<T> tmpl) throws IOException {
        globalMessagePack.write(out, obj, tmpl);
    }

    @Deprecated
    public static Value unpack(byte[] buffer) throws IOException {
        return globalMessagePack.read(buffer);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, Template<T> tmpl) throws IOException {
        BufferUnpacker u = new MessagePackBufferUnpacker(globalMessagePack).wrap(buffer);
        return tmpl.read(u, null);
    }

    @Deprecated
    public static <T> T unpack(byte[] buffer, Template<T> tmpl, T to) throws IOException {
        BufferUnpacker u = new MessagePackBufferUnpacker(globalMessagePack).wrap(buffer);
        return tmpl.read(u, to);
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
    public static <T> T unpack(InputStream in, Template<T> tmpl) throws IOException, MessageTypeException {
        return tmpl.read(new MessagePackUnpacker(globalMessagePack, in), null);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, Template<T> tmpl, T to) throws IOException, MessageTypeException {
        return (T) tmpl.read(new MessagePackUnpacker(globalMessagePack, in), to);
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

