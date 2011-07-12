package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class PrimitiveTypeFieldsClass {
    public byte f0;
    public short f1;
    public int f2;
    public long f3;
    public float f4;
    public double f5;
    public boolean f6;

    public PrimitiveTypeFieldsClass() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof PrimitiveTypeFieldsClass)) {
	    return false;
	}
	PrimitiveTypeFieldsClass that = (PrimitiveTypeFieldsClass) o;
	if (f0 != that.f0) {
	    return false;
	}
	if (f1 != that.f1) {
	    return false;
	}
	if (f2 != that.f2) {
	    return false;
	}
	if (f3 != that.f3) {
	    return false;
	}
	if (f4 != that.f4) {
	    return false;
	}
	if (f5 != that.f5) {
	    return false;
	}
	if (f6 != that.f6) {
	    return false;
	}
	return true;
    }
}
