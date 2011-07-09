package org.msgpack.testclasses;

import java.util.Map;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class MapTypeFieldsClassNotNullable {
    @NotNullable
    public Map<Integer, Integer> f0;
    @NotNullable
    public Map<Integer, Integer> f1;
    @NotNullable
    public Map<String, Integer> f2;
    @NotNullable
    public Map<String, NestedClass> f3;

    public MapTypeFieldsClassNotNullable() {
    }

    @Ignore @Message
    public static class NestedClass {
	@NotNullable
	public String f0;

	public NestedClass() {}
    }
}
