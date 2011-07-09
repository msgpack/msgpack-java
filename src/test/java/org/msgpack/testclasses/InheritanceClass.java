package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class InheritanceClass extends SuperClass {
    public String f2;
    public int f3;

    public InheritanceClass() {}
}
