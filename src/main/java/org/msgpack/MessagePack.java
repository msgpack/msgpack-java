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

/**
 * <p>
 * This is basic class to use MessagePack for Java. It creates serializers and
 * deserializers for objects of classes.
 * </p>
 * 
 * <h3>Overview of MessagePack</h3>
 * 
 * <p>
 * MessagePack is a binary-based efficient object serialization library for
 * cross languages. It enables to exchange structured objects between many
 * languages like JSON. But unlike JSON, it is very fast and small.
 * </p>
 * 
 * <p>
 * MessagePack for Java is one implementation of MessagePack libraries in pure
 * Java. See <a
 * href="https://github.com/eishay/jvm-serializers/wiki">jvm-serializers</a>,
 * which is one of well-known benchmarks for comparing Java libraries of data
 * serialization.
 * </p>
 * 
 * <h3>How to Install</h3>
 * 
 * <p>
 * The official Maven2 repository for MessagePack for Java is located here. <a
 * href="http://msgpack.org/maven2/">http://msgpack.org/maven2/</a>
 * </p>
 * 
 * <p>
 * You could use the following <code>pom.xml</code> to install MessagePack for
 * Java via Maven.
 * </p>
 * 
 * <pre>
 * &lt;project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd"&gt;
 * 
 *   &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
 *   &lt;groupId&gt;org.sample&lt;/groupId&gt;
 *   &lt;artifactId&gt;sample&lt;/artifactId&gt;
 *   &lt;packaging&gt;jar&lt;/packaging&gt;
 *   &lt;version&gt;0.0.1&lt;/version&gt;
 * 
 *   &lt;repositories&gt;
 *     &lt;repository&gt;
 *     &lt;id&gt;msgpack.org&lt;/id&gt;
 *     &lt;name&gt;MessagePack Repository for Maven&lt;/name&gt;
 *     &lt;url&gt;http://msgpack.org/maven2/&lt;/url&gt;
 *     &lt;/repository&gt;
 *   &lt;/repositories&gt;
 * 
 *   &lt;dependencies&gt;
 *     &lt;dependency&gt;
 *       &lt;groupId&gt;org.msgpack&lt;/groupId&gt;
 *       &lt;artifactId&gt;msgpack&lt;/artifactId&gt;
 *       &lt;version&gt;0.6.0-devel&lt;/version&gt;
 *     &lt;/dependency&gt;
 *   &lt;/dependencies&gt;
 * &lt;/project&gt;
 * </pre>
 * 
 * <p>
 * You can build and install latest source code of MessagePack for Java as
 * follows. The source code resides in GitHub. <a
 * href="https://github.com/msgpack/msgpack-java/"
 * >https://github.com/msgpack/msgpack-java/</a>.
 * </p>
 * 
 * <pre>
 * $ git clone git@github.com:msgpack/msgpack-java.git
 * $ cd msgpack-java
 * $ mvn package
 * </pre>
 * 
 * <p>
 * Then you'll get the jar file of MessagePack for Java in <code>target</code>
 * directory.
 * </p>
 * 
 * <h3>Quick Start MessagePack for Java</h3>
 * 
 * <p>
 * <code>@Message</code> enables you to serialize objects of your own classes
 * like this.
 * </p>
 * 
 * <pre>
 * <code>@Message</code>
 * public class MyMessage {
 *     public String name;
 *     public double version;
 * }
 * </pre>
 * 
 * <p>
 * MessagePack recommends that serialized class and field declarations include
 * <code>public</code> modifier. The following is code to serialize objects of
 * class annotated by <code>@Message</code>.
 * </p>
 * 
 * <pre>
 * public class Main {
 *     public static void main(String[] args) {
 * 	MyMessage src = new MyMessage();
 * 	src.name = &quot;msgpack&quot;;
 * 	src.version = 0.6;
 * 
 * 	MessagePack msgpack = new MessagePack();
 * 	// serialize src data to byte array
 * 	byte[] bytes = msgpack.write(src);
 * 	// deserialize byte array to MyMessage object
 * 	MyMessage dst = msgpack.read(bytes, MyMessage.class);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * If you want to serialize multiple objects sequentially, MessagePack
 * recommends use of {@link org.msgpack.packer.Packer} and
 * {@link org.msgpack.unpacker.Unpacker} objects. Because
 * {@link MessagePack#write(Object)} and {@link #read(byte[])} method
 * invocations create {@link org.msgpack.packer.Packer} and
 * {@link org.msgpack.unpacker.Unpacker} objects every times. To use
 * <code>Packer</code> and <code>Unpacker</code> objects, you call
 * {@link #createPacker(OutputStream)} and {@link #createUnpacker(byte[])}.
 * </p>
 * 
 * <pre>
 * public class Main2 {
 *     public static void main(String[] args) {
 * 	MyMessage src1 = new MyMessage();
 * 	src1.name = &quot;msgpack&quot;;
 * 	src1.version = 0.6;
 * 	MyMessage src2 = new MyMessage();
 * 	src2.name = &quot;muga&quot;;
 * 	src2.version = 10.0;
 * 	MyMessage src3 = new MyMessage();
 * 	src3.name = &quot;frsyukik&quot;;
 * 	src3.version = 1.0;
 * 
 * 	MessagePack msgpack = new MessagePack();
 * 	// serialize src data to byte array
 * 	ByteArrayOutputStream out = new ByteArrayOutputStream();
 * 	Packer packer = msgpack.createPacker(out);
 * 	packer.write(src1);
 * 	packer.write(src2);
 * 	packer.write(src3);
 * 	byte[] bytes = out.toByteArray();
 * 	// deserialize byte array to MyMessage object
 * 	ByteArrayInputStream in = new ByteArrayInputStream(bytes);
 * 	Unpacker unpacker = msgpack.createUnpacker(in);
 * 	MyMessage dst1 = unpacker.read(bytes, MyMessage.class);
 * 	MyMessage dst2 = unpacker.read(bytes, MyMessage.class);
 * 	MyMessage dst3 = unpacker.read(bytes, MyMessage.class);
 *     }
 * }
 * </pre>
 * 
 * <h3>Various Types of Values Serialization/Deserialization</h3>
 * 
 * <p>
 * <code>Packer</code>/<code>Unpacker</code> allows serializing/deserializing
 * values of various types as follows. They enable serializing/deserializing
 * values of various types like values of primitive types, values of primitive
 * wrapper classes, <code>String</code> objects, <code>byte[]</code> objects,
 * <code>ByteBuffer</code> objects and so on. As mentioned above, they also
 * enable serializing/deserizing objects of your own classes annotated by
 * <code>@Message</code>.
 * </p>
 * 
 * <pre>
 * public class Main3 {
 *     public static void main(String[] args) {
 * 	MessagePack msgpack = new MessagePack();
 * 
 * 	//
 * 	// serialization
 * 	//
 * 
 * 	ByteArrayOutputStream out = new ByteArrayOutputStream();
 * 	Packer packer = msgpack.createPacker(out);
 * 
 * 	// serialize values of primitive types
 * 	packer.write(true); // boolean value
 * 	packer.write(10) // int value
 * 	packer.write(10.5); // double value
 * 
 * 	// serialize objects of primitive wrapper types
 * 	packer.write(Boolean.TRUE);
 * 	packer.write(new Integer(10));
 * 	packer.write(new Double(10.5));
 * 
 * 	// serialize various types of arrays
 * 	packer.write(new int[] { 1, 2, 3, 4 });
 * 	packer.write(new Double[] { 10.5, 20.5 });
 * 	packer.write(new String[] { &quot;msg&quot;, &quot;pack&quot;, &quot;for&quot;, &quot;java&quot; });
 * 	packer.write(new byte[] { 0x30, 0x31, 0x32 }); // byte array
 * 
 * 	// serialize various types of other reference values
 * 	packer.write(&quot;MesagePack&quot;); // String object
 * 	packer.write(java.nio.ByteBufer.wrap(new byte[] { 0x30, 0x31, 0x32 })); // ByteBuffer object
 * 	packer.write(java.math.BigInteger.ONE); // BigInteger object
 * 
 * 	//
 * 	// deserialization
 * 	//
 * 
 * 	byte[] bytes = out.toByteArray();
 * 	ByteArrayInputStream in = new ByteArrayInputStream(bytes);
 * 	Unpacker unpacker = msgpack.createUnpacker(in);
 * 
 * 	// to primitive values
 * 	boolean b = unpacker.readBoolean(); // boolean value
 * 	int i = unpacker.readInt(); // int value
 * 	double d = unpacker.readDouble(); // double value
 * 
 * 	// to primitive wrapper value
 * 	Boolean wb = unpacker.read(Boolean.class);
 * 	Integer wi = unpacker.read(Integer.class);
 * 	Double wd = unpacker.read(Double.class);
 * 
 * 	// to arrays
 * 	int[] ia = unpacker.read(int[].class);
 * 	Double[] da = unpacker.read(Double[].class);
 * 	String[] sa = unpacker.read(String[].class);
 * 	byte[] ba = unpacker.read(byte[].class);
 * 
 * 	// to String object, ByteBuffer object, BigInteger object, List object and Map object
 * 	String ws = unpacker.read(String.class);
 * 	java.nio.ByteBuffer buf = unpacker.read(java.nio.ByteBuffer.class);
 * 	java.math.BigInteger bi = unpacker.read(java.math.BigInteger.class);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * <code>write</code> methods provided by <code>Packer</code> allows serializing
 * various types of data.
 * </p>
 * 
 * <p>
 * <code>Unpacker</code> provides deserialization methods for deserializing
 * binary to primitive values. For example, if you want to deserialize binary to
 * value of <code>boolean</code> (or <code>int</code>) type, you can use
 * <code>readBoolean</code> (or <code>readInt</code>) method in
 * <code>Unpacker</code>.
 * </p>
 * 
 * <p>
 * <code>Unpacker</code> also provides <code>read</code> methods for reference
 * values. Its methods allow deserializing binary to values of references which
 * types you specified as parameters. For example, if you want to deserialize
 * binary to <code>String</code> (or <code>byte[]) object, 
 * you have to describe a call of <code>read(String.class)</code> (or
 * <code>read(byte[].class)</code>) method.
 * </p>
 * 
 * <h3><code>List</code>, <code>Map</code> objects Serialization/Deserialization
 * </h3>
 * 
 * <p>
 * You can serialize <code>List</code> and <code>Map</code> objects with
 * {@link org.msgpack.template.Template} objects, which are pairs of
 * serializer/deserializer. The type of elements in <code>List</code> object
 * needs to be specified to <code>Template</code> object for effective
 * serialization/deserialization. Types of keys and values in <code>Map</code>
 * object also need to be specified to <code>Template</code> object.
 * </p>
 * 
 * <pre>
 * import static org.msgpack.template.Templates.tList;
 * import static org.msgpack.template.Templates.tMap;
 * import static org.msgpack.template.Templates.TString;
 * 
 * public class Main4 {
 *     public static void main(String[] args) {
 * 	MessagePack msgpack = new MessagePack();
 * 
 * 	// create templates for serializing/deserializing List and Map objects
 * 	org.msgpack.template.Template listTmpl = tList(TString);
 * 	org.msgpack.template.Template mapTmpl = tMap(TString, TString);
 * 
 * 	//
 * 	// serialization
 * 	//
 * 
 * 	ByteArrayOutputStream out = new ByteArrayOutputStream();
 * 	Packer packer = msgpack.createPacker(out);
 * 
 * 	// serialize List object
 * 	java.util.List&lt;String&gt; list = new java.util.ArrayList&lt;String&gt;();
 * 	list.add(&quot;msgpack&quot;);
 * 	list.add(&quot;for&quot;);
 * 	list.add(&quot;java&quot;);
 * 	packer.write(list, listTmpl); // List object
 * 
 * 	// serialize Map object
 * 	java.util.Map&lt;String, String&gt; map = new java.util.HashMap&lt;String, String&gt;();
 * 	map.put(&quot;sadayuki&quot;, &quot;furuhashi&quot;);
 * 	map.put(&quot;muga&quot;, &quot;nishizawa&quot;);
 * 	packer.write(map, mapTmpl); // Map object
 * 
 * 	//
 * 	// deserialization
 * 	//
 * 
 * 	byte[] bytes = out.toByteArray();
 * 	ByteArrayInputStream in = new ByteArrayInputStream(bytes);
 * 	Unpacker unpacker = msgpack.createUnpacker(in);
 * 
 * 	// to List object
 * 	dstList = unpacker.read(dstList, listTmpl);
 * 
 * 	// to Map object
 * 	dstMap = unpacker.read(dstMap, mapTmpl);
 *     }
 * }
 * </pre>
 * 
 * <h3>Without Annotations</h3>
 * 
 * <p>
 * If you cannot append <code>@Message</code> to classes representing objects
 * that you want to serialize, {@link #register} method enables you to serialize
 * the objects of the classes.
 * </p>
 * </p>
 * 
 * <pre>
 * MessagePack msgpack = new MessagePack();
 * msgpack.register(MyMessage2.class);
 * </pre>
 * 
 * <p>
 * For example, if <code>MyMessage2</code> class is included in external
 * library, you cannot easily modify the class declaration and append
 * <code>@Message</code> to it. {@link #register} method allows to generate
 * serializer/deserializer of <code>MyMessage2</code> class automatically. You
 * can serialize objects of <code>MyMessage2</code> class after executing the
 * method.
 * </p>
 * 
 * <h3>Optional Fields</h3>
 * 
 * <p>
 * You can add new fields maintaining the compatibility. Use the
 * <code>@Optional</code> in the new fields.
 * </p>
 * 
 * <pre>
 * <code>@Message</code>
 * public class MyMessage {
 *     public String name;
 *     public double version;
 *     // new field
 *     <code>@Optional</code>
 *     public int flag = 0;
 * }
 * </pre>
 * 
 * <p>
 * If you try to deserialize the old version data, optional fields will be
 * ignored.
 * </p>
 * 
 */
public class MessagePack {
    private TemplateRegistry registry;

    /**
     * 
     * @since 0.6.0
     */
    public MessagePack() {
	registry = new TemplateRegistry(null);
    }

    /**
     * 
     * @since 0.6.0
     * @param msgpack
     */
    public MessagePack(MessagePack msgpack) {
	registry = new TemplateRegistry(msgpack.registry);
    }

    /**
     * 
     * @since 0.6.0
     * @param cl
     */
    public void setClassLoader(final ClassLoader cl) {
	registry.setClassLoader(cl);
    }

    /**
     * Returns serializer that enables serializing objects into
     * {@link java.io.OutputStream} object.
     * 
     * @since 0.6.0
     * @param out
     *            output stream
     * @return stream-based serializer
     */
    public Packer createPacker(OutputStream out) {
	return new MessagePackPacker(this, out);
    }

    /**
     * Returns serializer that enables serializing objects into buffer.
     * 
     * @since 0.6.0
     * @return buffer-based serializer
     */
    public BufferPacker createBufferPacker() {
	return new MessagePackBufferPacker(this);
    }

    /**
     * Returns serializer that enables serializing objects into buffer.
     * 
     * @since 0.6.0
     * @param bufferSize
     *            initial size of buffer
     * @return buffer-based serializer
     */
    public BufferPacker createBufferPacker(int bufferSize) {
	return new MessagePackBufferPacker(this, bufferSize);
    }

    /**
     * Returns deserializer that enables deserializing
     * {@link java.io.InputStream} object.
     * 
     * @since 0.6.0
     * @param in
     *            input stream
     * @return stream-based deserializer
     */
    public Unpacker createUnpacker(InputStream in) {
	return new MessagePackUnpacker(this, in);
    }

    /**
     * Returns empty deserializer that enables deserializing buffer.
     * 
     * @since 0.6.0
     * @return buffer-based deserializer
     */
    public BufferUnpacker createBufferUnpacker() {
	return new MessagePackBufferUnpacker();
    }

    /**
     * Returns deserializer that enables deserializing buffer.
     * 
     * @since 0.6.0
     * @param bytes
     *            input byte array
     * @return buffer-based deserializer
     */
    public BufferUnpacker createBufferUnpacker(byte[] bytes) {
	return createBufferUnpacker().wrap(bytes);
    }

    /**
     * Returns deserializer that enables deserializing buffer.
     * 
     * @since 0.6.0
     * @param bytes
     * @param off
     * @param len
     * @return buffer-based deserializer
     */
    public BufferUnpacker createBufferUnpacker(byte[] bytes, int off, int len) {
	return createBufferUnpacker().wrap(bytes, off, len);
    }

    /**
     * Returns deserializer that enables deserializing buffer.
     * 
     * @since 0.6.0
     * @param buffer
     *            input {@link java.nio.ByteBuffer} object
     * @return buffer-based deserializer
     */
    public BufferUnpacker createBufferUnpacker(ByteBuffer buffer) {
	return createBufferUnpacker().wrap(buffer);
    }

    /**
     * Serializes specified object.
     * 
     * @since 0.6.0
     * @param v
     *            serialized object
     * @return output byte array
     * @throws IOException
     */
    public <T> byte[] write(T v) throws IOException {
	BufferPacker pk = createBufferPacker();
	if (v == null) {
	    pk.writeNil();
	} else {
	    @SuppressWarnings("unchecked")
	    Template<T> tmpl = registry.lookup(v.getClass());
	    tmpl.write(pk, v);
	}
	return pk.toByteArray();
    }

    /**
     * Serializes specified object. It allows serializing object by specified
     * template.
     * 
     * @since 0.6.0
     * @param v
     * @param template
     * @return
     * @throws IOException
     */
    public <T> byte[] write(T v, Template<T> template) throws IOException {
	BufferPacker pk = createBufferPacker();
	template.write(pk, v);
	return pk.toByteArray();
    }

    /**
     * Serializes specified object to output stream.
     * 
     * @since 0.6.0
     * @param out
     *            output stream
     * @param v
     *            serialized object
     * @throws IOException
     */
    public <T> void write(OutputStream out, T v) throws IOException {
	Packer pk = createPacker(out);
	if (v == null) {
	    pk.writeNil();
	} else {
	    @SuppressWarnings("unchecked")
	    Template<T> tmpl = registry.lookup(v.getClass());
	    tmpl.write(pk, v);
	}
    }

    /**
     * Serializes object to output stream by specified template.
     * 
     * @since 0.6.0
     * @param out
     *            output stream
     * @param v
     *            serialized object
     * @param template
     *            serializer/deserializer for the object
     * @throws IOException
     */
    public <T> void write(OutputStream out, T v, Template<T> template)
	    throws IOException {
	Packer pk = createPacker(out);
	template.write(pk, v);
    }

    /**
     * Serializes {@link org.msgpack.type.Value} object to byte array.
     * 
     * @since 0.6.0
     * @param v
     *            serialized {@link org.msgpack.type.Value} object
     * @return output byte array
     * @throws IOException
     */
    public byte[] write(Value v) throws IOException {
	// FIXME ValueTemplate should do this
	BufferPacker pk = createBufferPacker();
	pk.write(v);
	return pk.toByteArray();
    }

    /**
     * Deserializes specified byte array to {@link org.msgpack.type.Value}
     * object.
     * 
     * @since 0.6.0
     * @param bytes
     *            input byte array
     * @return
     * @throws IOException
     */
    public Value read(byte[] bytes) throws IOException {
	return read(bytes, 0, bytes.length);
    }

    /**
     * Deserializes byte array to {@link org.msgpack.type.Value} object.
     * 
     * @since 0.6.0
     * @param bytes
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public Value read(byte[] bytes, int off, int len) throws IOException {
	return createBufferUnpacker(bytes, off, len).readValue();
    }

    /**
     * Deserializes {@link java.nio.ByteBuffer} object to
     * {@link org.msgpack.type.Value} object.
     * 
     * @since 0.6.0
     * @param buffer
     *            input buffer
     * @return
     * @throws IOException
     */
    public Value read(ByteBuffer buffer) throws IOException {
	return createBufferUnpacker(buffer).readValue();
    }

    /**
     * Deserializes input stream to {@link org.msgpack.type.Value} object.
     * 
     * @since 0.6.0
     * @param in
     *            input stream
     * @return deserialized {@link org.msgpack.type.Value} object
     * @throws IOException
     */
    public Value read(InputStream in) throws IOException {
	return createUnpacker(in).readValue();
    }

    /**
     * Deserializes byte array to object.
     * 
     * @since 0.6.0
     * @param bytes
     *            input byte array
     * @param v
     * @return
     * @throws IOException
     */
    public <T> T read(byte[] bytes, T v) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(v.getClass());
	return read(bytes, v, tmpl);
    }

    /**
     * Deserializes byte array to object according to template.
     * 
     * @since 0.6.0
     * @param bytes	input byte array
     * @param tmpl	template
     * @return
     * @throws IOException
     */
    public <T> T read(byte[] bytes, Template<T> tmpl) throws IOException {
	return read(bytes, null, tmpl);
    }

    /**
     * Deserializes byte array to object of specified class.
     * 
     * @since 0.6.0
     * @param bytes	input byte array
     * @param c
     * @return
     * @throws IOException
     */
    public <T> T read(byte[] bytes, Class<T> c) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(c);
	return read(bytes, null, tmpl);
    }

    /**
     * Deserializes byte array to object according to specified template.
     * 
     * @since 0.6.0
     * @param bytes	input byte array
     * @param v
     * @param tmpl	template
     * @return
     * @throws IOException
     */
    public <T> T read(byte[] bytes, T v, Template<T> tmpl) throws IOException {
	BufferUnpacker u = createBufferUnpacker(bytes);
	return (T) tmpl.read(u, v);
    }

    /**
     * Deserializes buffer to object.
     * 
     * @since 0.6.0
     * @param b
     *            input {@link java.nio.ByteBuffer} object
     * @param v
     * @return
     * @throws IOException
     */
    public <T> T read(ByteBuffer b, T v) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(v.getClass());
	return read(b, v, tmpl);
    }

    /**
     * Deserializes buffer to object according to template.
     * 
     * @since 0.6.0
     * @param b		input buffer object
     * @param tmpl
     * @return
     * @throws IOException
     */
    public <T> T read(ByteBuffer b, Template<T> tmpl) throws IOException {
	return read(b, null, tmpl);
    }

    /**
     * Deserializes buffer to object of specified class.
     * 
     * @since 0.6.0
     * @param b
     * @param c
     * @return
     * @throws IOException 
     */
    public <T> T read(ByteBuffer b, Class<T> c) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(c);
	return read(b, null, tmpl);
    }

    /**
     * Deserializes buffer to object according to template.
     * 
     * @since 0.6.0
     * @param b		input buffer object
     * @param v
     * @param tmpl
     * @return
     * @throws IOException
     */
    public <T> T read(ByteBuffer b, T v, Template<T> tmpl) throws IOException {
	BufferUnpacker u = createBufferUnpacker(b);
	return tmpl.read(u, v);
    }

    /**
     * Deserializes input stream to object.
     * 
     * @since 0.6.0
     * @param in
     *            input stream
     * @param v
     * @return
     * @throws IOException
     */
    public <T> T read(InputStream in, T v) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(v.getClass());
	return read(in, v, tmpl);
    }

    /**
     * Deserializes input stream to object according to template.
     * 
     * @since 0.6.0
     * @param in	input stream
     * @param tmpl
     * @return
     * @throws IOException
     */
    public <T> T read(InputStream in, Template<T> tmpl) throws IOException {
	return read(in, null, tmpl);
    }

    /**
     * Deserializes input stream to object of specified class.
     * 
     * @since 0.6.0
     * @param in
     * @param c
     * @return
     * @throws IOException
     */
    public <T> T read(InputStream in, Class<T> c) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(c);
	return read(in, null, tmpl);
    }

    /**
     * Deserializes input stream to object according to template
     * 
     * @since 0.6.0
     * @param in	input stream
     * @param v
     * @param tmpl
     * @return
     * @throws IOException
     */
    public <T> T read(InputStream in, T v, Template<T> tmpl) throws IOException {
	Unpacker u = createUnpacker(in);
	return tmpl.read(u, v);
    }

    /**
     * Converts specified {@link org.msgpack.type.Value} object to object.
     * 
     * @since 0.6.0
     * @param v
     * @param to
     * @return
     * @throws IOException
     */
    public <T> T convert(Value v, T to) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(to.getClass());
	return tmpl.read(new Converter(this, v), to);
    }

    /**
     * Converts {@link org.msgpack.type.Value} object to object specified class.
     * 
     * @since 0.6.0
     * @param v
     * @param c
     * @return
     * @throws IOException
     */
    public <T> T convert(Value v, Class<T> c) throws IOException {
	@SuppressWarnings("unchecked")
	Template<T> tmpl = registry.lookup(c);
	return tmpl.read(new Converter(this, v), null);
    }

    /**
     * Unconverts specified object to {@link org.msgpack.type.Value} object.
     * 
     * @since 0.6.0
     * @param v
     * @return
     * @throws IOException
     */
    public <T> Value unconvert(T v) throws IOException {
	Unconverter pk = new Unconverter(this);
	if (v == null) {
	    pk.writeNil();
	} else {
	    @SuppressWarnings("unchecked")
	    Template<T> tmpl = registry.lookup(v.getClass());
	    tmpl.write(pk, v);
	}
	return pk.getResult();
    }

    /**
     * Registers {@link org.msgpack.template.Template} object for objects of
     * specified class. <tt>Template</tt> object is a pair of serializer and
     * deserializer for object serialization. It is generated automatically.
     * 
     * @since 0.6.0
     * @param type
     */
    public void register(Class<?> type) {
	registry.register(type);
    }

    /**
     * Registers specified {@link org.msgpack.template.Template} object
     * associated by class.
     * 
     * @see #register(Class)
     * @since 0.6.0
     * @param type
     * @param template
     */
    public <T> void register(Class<T> type, Template<T> template) {
	registry.register(type, template);
    }

    /**
     * Unregisters {@link org.msgpack.template.Template} object for objects of
     * specified class.
     * 
     * @since 0.6.0
     * @param type
     * @return
     */
    public boolean unregister(Class<?> type) {
	return registry.unregister(type);
    }

    /**
     * Unregisters all {@link org.msgpack.template.Template} objects that have
     * been registered in advance.
     * 
     * @since 0.6.0
     */
    public void unregister() {
	registry.unregister();
    }

    /**
     * Looks up a {@link org.msgpack.template.Template} object, which is
     * serializer/deserializer associated by specified class.
     * 
     * @since 0.6.0
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Template<T> lookup(Class<T> type) {
	return registry.lookup(type);
    }

    private static final MessagePack globalMessagePack = new MessagePack();

    /**
     * Serializes specified object and returns the byte array.
     * 
     * @deprecated {@link MessagePack#write(Object)}
     * @param v
     * @return
     * @throws IOException
     */
    @Deprecated
    public static byte[] pack(Object v) throws IOException {
	return globalMessagePack.write(v);
    }

    /**
     * Serializes specified object to output stream.
     * 
     * @deprecated {@link MessagePack#write(OutputStream, Object)}
     * @param out
     * @param v
     * @throws IOException
     */
    @Deprecated
    public static void pack(OutputStream out, Object v) throws IOException {
	globalMessagePack.write(out, v);
    }

    /**
     * Serializes object by specified template and return the byte array.
     * 
     * @deprecated {@link MessagePack#write(Object, Template)}
     * @param v
     * @param template
     * @return
     * @throws IOException
     */
    @Deprecated
    public static <T> byte[] pack(T v, Template<T> template) throws IOException {
	return globalMessagePack.write(v, template);
    }

    /**
     * Serializes object to output stream. The object is serialized by specified
     * template.
     * 
     * @deprecated {@link MessagePack#write(OutputStream, Object, Template)}
     * @param out
     * @param v
     * @param template
     * @throws IOException
     */
    @Deprecated
    public static <T> void pack(OutputStream out, T v, Template<T> template)
	    throws IOException {
	globalMessagePack.write(out, v, template);
    }

    /**
     * Converts byte array to {@linke org.msgpack.type.Value} object.
     * 
     * @deprecated {@linke MessagePack#read(byte[])}
     * @param bytes
     * @return
     * @throws IOException
     */
    @Deprecated
    public static Value unpack(byte[] bytes) throws IOException {
	return globalMessagePack.read(bytes);
    }

    @Deprecated
    public static <T> T unpack(byte[] bytes, Template<T> template)
	    throws IOException {
	BufferUnpacker u = new MessagePackBufferUnpacker(globalMessagePack)
		.wrap(bytes);
	return template.read(u, null);
    }

    @Deprecated
    public static <T> T unpack(byte[] bytes, Template<T> template, T to)
	    throws IOException {
	BufferUnpacker u = new MessagePackBufferUnpacker(globalMessagePack)
		.wrap(bytes);
	return template.read(u, to);
    }

    /**
     * Deserializes byte array to object of specified class.
     * 
     * @deprecated {@link MessagePack#read(byte[], Class)}
     * @param bytes
     * @param klass
     * @return
     * @throws IOException
     */
    @Deprecated
    public static <T> T unpack(byte[] bytes, Class<T> klass) throws IOException {
	return globalMessagePack.read(bytes, klass);
    }

    /**
     * Deserializes byte array to object.
     * 
     * @param bytes
     * @param to
     * @return
     * @throws IOException
     */
    @Deprecated
    public static <T> T unpack(byte[] bytes, T to) throws IOException {
	return globalMessagePack.read(bytes, to);
    }

    /**
     * Converts input stream to {@link org.msgpack.type.Value} object.
     * 
     * @deprecated {@link MessagePack#read(InputStream)}
     * @param in
     * @return
     * @throws IOException
     */
    @Deprecated
    public static Value unpack(InputStream in) throws IOException {
	return globalMessagePack.read(in);
    }

    /**
     * @deprecated
     * @param in
     * @param tmpl
     * @return
     * @throws IOException
     * @throws MessageTypeException
     */
    @Deprecated
    public static <T> T unpack(InputStream in, Template<T> tmpl)
	    throws IOException, MessageTypeException {
	return tmpl.read(new MessagePackUnpacker(globalMessagePack, in), null);
    }

    /**
     * @deprecated
     * @param in
     * @param tmpl
     * @param to
     * @return
     * @throws IOException
     * @throws MessageTypeException
     */
    @Deprecated
    public static <T> T unpack(InputStream in, Template<T> tmpl, T to)
	    throws IOException, MessageTypeException {
	return (T) tmpl
		.read(new MessagePackUnpacker(globalMessagePack, in), to);
    }

    /**
     * Deserializes input stream to object of specified class.
     * 
     * @deprecated {@link MessagePack#read(InputStream, Class)}
     * @param in
     * @param klass
     * @return
     * @throws IOException
     */
    @Deprecated
    public static <T> T unpack(InputStream in, Class<T> klass)
	    throws IOException {
	return globalMessagePack.read(in, klass);
    }

    /**
     * Deserializes input stream to object.
     * 
     * @deprecated {@link MessagePack#read(InputStream, Object)}
     * @param in
     * @param to
     * @return
     * @throws IOException
     */
    @Deprecated
    public static <T> T unpack(InputStream in, T to) throws IOException {
	return globalMessagePack.read(in, to);
    }
}
