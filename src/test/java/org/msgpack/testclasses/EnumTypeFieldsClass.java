package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.OrdinalEnum;


@Ignore @Message
public class EnumTypeFieldsClass {
    public int f0;
    public SampleEnum f1;

    public EnumTypeFieldsClass() {}

    @Ignore @OrdinalEnum
    public static enum SampleEnum {
	ONE, TWO, THREE;
    }
}
