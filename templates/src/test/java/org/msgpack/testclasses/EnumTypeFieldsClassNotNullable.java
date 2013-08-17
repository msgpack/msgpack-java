package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.OrdinalEnum;


@Ignore @Message @Beans
public class EnumTypeFieldsClassNotNullable {
    @NotNullable
    public int f0;

    @NotNullable
    public SampleEnum f1;

    public EnumTypeFieldsClassNotNullable() {}

    @NotNullable
    public int getF0() {
        return f0;
    }

    @NotNullable
    public void setF0(int f0) {
        this.f0 = f0;
    }

    @NotNullable
    public SampleEnum getF1() {
        return f1;
    }

    @NotNullable
    public void setF1(SampleEnum f1) {
        this.f1 = f1;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof EnumTypeFieldsClassNotNullable)) {
	    return false;
	}
	EnumTypeFieldsClassNotNullable that = (EnumTypeFieldsClassNotNullable) o;
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
