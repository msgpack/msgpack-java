package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class UserDefinedTypeFieldsClassNotNullable {
    @NotNullable
    public NestedClass1 f0;
    @NotNullable
    public NestedClass2 f1;

    public UserDefinedTypeFieldsClassNotNullable () {}

    @Ignore @Message
    public static class NestedClass1 {
	@NotNullable
	public int f0;
	@NotNullable
	public String f1;

	public NestedClass1() {}
    }

    @Ignore @Message
    public static class NestedClass2 {
	@NotNullable
	public int f0;
	@NotNullable
	public String f1;

	public NestedClass2() {}
    }
}
