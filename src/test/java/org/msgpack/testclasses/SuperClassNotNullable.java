package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class SuperClassNotNullable {
    @NotNullable
    public String f0;
    @NotNullable
    protected int f1;

    public SuperClassNotNullable() {}
}
