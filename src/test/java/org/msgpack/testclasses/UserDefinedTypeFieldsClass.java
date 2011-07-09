package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class UserDefinedTypeFieldsClass {
    public NestedClass1 f0;
    public NestedClass2 f1;

    public UserDefinedTypeFieldsClass () {}

    @Ignore @Message
    public static class NestedClass1 {
	public int f0;
	public String f1;

	public NestedClass1() {}
    }

    @Ignore @Message
    public static class NestedClass2 {
	public int f0;
	public String f1;

	public NestedClass2() {}
    }
}
