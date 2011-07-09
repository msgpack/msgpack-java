package org.msgpack.testclasses;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class ListTypeFieldsClass {
    public List<Integer> f0;
    public List<Integer> f1;
    public List<String> f2;
    public List<List<String>> f3;
    public List<NestedClass> f4;
    public List<ByteBuffer> f5;

    public ListTypeFieldsClass() {
    }

    @Ignore @Message
    public static class NestedClass {
	public byte[] f0;
	public String f1;

	public NestedClass() {}
    }
}
