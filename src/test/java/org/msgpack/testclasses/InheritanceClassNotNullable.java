package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class InheritanceClassNotNullable extends SuperClass {
    @NotNullable
    public String f2;
    @NotNullable
    public int f3;

    public InheritanceClassNotNullable() {}
}
