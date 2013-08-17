package org.msgpack.annotation;

import org.junit.Test;
import org.msgpack.MessagePack;


public class TestOptionalPackBufferUnpack {

    @org.junit.Ignore
    public static class TestMessagePack extends TestSetOptional {
	public void testOptional0101() throws Exception {
	    super.testOptional0101();
	}

	public MyMessage01 testOptional0101(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(src);
	    return msgpack.read(bytes, MyMessage01.class);
	}

	public void testOptional0102() throws Exception {
	    super.testOptional0102();
	}

	public MyMessage02 testOptional0102(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(src);
	    return msgpack.read(bytes, MyMessage02.class);
	}

	public void testOptional0103() throws Exception {
	    super.testOptional0103();
	}

	public MyMessage03 testOptional0103(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(src);
	    return msgpack.read(bytes, MyMessage03.class);
	}

	public void testOptional0203() throws Exception {
	    super.testOptional0203();
	}

	public MyMessage03 testOptional0202(MyMessage02 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(src);
	    return msgpack.read(bytes, MyMessage03.class);
	}
    }

    @Test
    public void test0101() throws Exception {
	new TestMessagePack().testOptional0101();
    }
    @Test
    public void test0102() throws Exception {
	new TestMessagePack().testOptional0102();
    }
    @Test
    public void test0103() throws Exception {
	new TestMessagePack().testOptional0103();
    }
    @Test
    public void test0203() throws Exception {
	new TestMessagePack().testOptional0203();
    }
}
