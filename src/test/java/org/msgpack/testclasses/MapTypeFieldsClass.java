package org.msgpack.testclasses;

import java.util.Map;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class MapTypeFieldsClass {
    public Map<Integer, Integer> f0;
    public Map<Integer, Integer> f1;
    public Map<String, Integer> f2;
    public Map<String, NestedClass> f3;

    public MapTypeFieldsClass() {
    }

    @Ignore @Message
    public static class NestedClass {
	public String f0;

	public NestedClass() {}
    }
}
