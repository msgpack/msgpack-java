package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class SuperClass {
    public String f0;
    protected int f1;

    public SuperClass() {}
}
