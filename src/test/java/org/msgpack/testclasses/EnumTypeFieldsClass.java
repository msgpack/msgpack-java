package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.OrdinalEnum;


@Ignore @Message
public class EnumTypeFieldsClass {
    public int f0;
    public SampleEnum f1;

    public EnumTypeFieldsClass() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof EnumTypeFieldsClass)) {
	    return false;
	}
	EnumTypeFieldsClass that = (EnumTypeFieldsClass) o;
	if (f0 != that.f0) {
	    return false;
	}
	if (f1 != that.f1) {
	    return false;
	}
	return true;
    }

    @Ignore @OrdinalEnum
    public static enum SampleEnum {
	ONE, TWO, THREE;
    }
}
