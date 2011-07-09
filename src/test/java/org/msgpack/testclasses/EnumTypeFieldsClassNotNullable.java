package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.OrdinalEnum;


@Ignore @Message
public class EnumTypeFieldsClassNotNullable {
    @NotNullable
    public int f0;
    @NotNullable
    public SampleEnum f1;

    public EnumTypeFieldsClassNotNullable() {}

    @Ignore @OrdinalEnum
    public static enum SampleEnum {
	ONE, TWO, THREE;
    }
}
