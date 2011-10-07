package org.msgpack.annotation;

import static org.junit.Assert.assertEquals;

@org.junit.Ignore
public class TestSetOptional {

    @Message
    public static class MyMessage01 {
	public String f0;
	public int f1;
	public MyMessage01() {
	}
    }

    @Message
    public static class MyMessage02 {
	public String f0;
	public int f1;
	@Optional
	public int f2 = 20;
	public MyMessage02() {
	}
    }

    @Message
    public static class MyMessage03 {
	public String f0;
	public int f1;
	@Optional
	public int f2 = 20;
	@Optional
	public String f3 = "frsyuki";
	public MyMessage03() {
	}
    }

    public void testOptional0101() throws Exception {
	MyMessage01 src = new MyMessage01();
	src.f0 = "muga";
	src.f1 = 10;
	MyMessage01 dst = testOptional0101(src);
	assertEquals(src.f0, dst.f0);
	assertEquals(src.f1, dst.f1);
    }

    public MyMessage01 testOptional0101(MyMessage01 src) throws Exception {
	throw new UnsupportedOperationException();
    }

    public void testOptional0102() throws Exception {
	MyMessage01 src = new MyMessage01();
	src.f0 = "muga";
	src.f1 = 10;
	MyMessage02 dst = testOptional0102(src);
	assertEquals(src.f0, dst.f0);
	assertEquals(src.f1, dst.f1);
	assertEquals(20, dst.f2);
    }

    public MyMessage02 testOptional0102(MyMessage01 src) throws Exception {
	throw new UnsupportedOperationException();
    }

    public void testOptional0103() throws Exception {
	MyMessage01 src = new MyMessage01();
	src.f0 = "muga";
	src.f1 = 10;
	MyMessage03 dst = testOptional0103(src);
	assertEquals(src.f0, dst.f0);
	assertEquals(src.f1, dst.f1);
	assertEquals(20, dst.f2);
	assertEquals("frsyuki", dst.f3);
    }

    public MyMessage03 testOptional0103(MyMessage01 src) throws Exception {
	throw new UnsupportedOperationException();
    }

    public void testOptional0203() throws Exception {
	MyMessage02 src = new MyMessage02();
	src.f0 = "muga";
	src.f1 = 10;
	MyMessage03 dst = testOptional0202(src);
	assertEquals(src.f0, dst.f0);
	assertEquals(src.f1, dst.f1);
	assertEquals(src.f2, dst.f2);
	assertEquals("frsyuki", dst.f3);
    }

    public MyMessage03 testOptional0202(MyMessage02 src) throws Exception {
	throw new UnsupportedOperationException();
    }
}
