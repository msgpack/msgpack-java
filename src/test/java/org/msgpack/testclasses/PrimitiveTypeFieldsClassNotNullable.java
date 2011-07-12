package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class PrimitiveTypeFieldsClassNotNullable {
    @NotNullable
    public byte f0;
    @NotNullable
    public short f1;
    @NotNullable
    public int f2;
    @NotNullable
    public long f3;
    @NotNullable
    public float f4;
    @NotNullable
    public double f5;
    @NotNullable
    public boolean f6;

    public PrimitiveTypeFieldsClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof PrimitiveTypeFieldsClassNotNullable)) {
	    return false;
	}
	PrimitiveTypeFieldsClassNotNullable that = (PrimitiveTypeFieldsClassNotNullable) o;
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
