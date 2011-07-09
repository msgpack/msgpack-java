package org.msgpack.testclasses;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class ListTypeFieldsClassNotNullable {
    @NotNullable
    public List<Integer> f0;
    @NotNullable
    public List<Integer> f1;
    @NotNullable
    public List<String> f2;
    @NotNullable
    public List<List<String>> f3;
    @NotNullable
    public List<NestedClass> f4;
    @NotNullable
    public List<ByteBuffer> f5;

    public ListTypeFieldsClassNotNullable() {
    }

    @Ignore @Message
    public static class NestedClass {
	@NotNullable
	public byte[] f0;
	@NotNullable
	public String f1;

	public NestedClass() {}
    }
}
