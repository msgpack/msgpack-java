package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.OrdinalEnum;


@Ignore @Message @Beans
public class EnumTypeFieldsClass {
    public int f0;

    public SampleEnum f1;

    public EnumTypeFieldsClass() {}

    public int getF0() {
        return f0;
    }

    public void setF0(int f0) {
        this.f0 = f0;
    }

    public SampleEnum getF1() {
        return f1;
    }

    public void setF1(SampleEnum f1) {
        this.f1 = f1;
    }

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
