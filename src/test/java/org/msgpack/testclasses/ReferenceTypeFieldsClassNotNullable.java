package org.msgpack.testclasses;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.template.builder.TestSet;


@Ignore @Message
public class ReferenceTypeFieldsClassNotNullable {
    @NotNullable
    public Byte f0;
    @NotNullable
    public Short f1;
    @NotNullable
    public Integer f2;
    @NotNullable
    public Long f3;
    @NotNullable
    public Float f4;
    @NotNullable
    public Double f5;
    @NotNullable
    public Boolean f6;
    @NotNullable
    public BigInteger f7;
    @NotNullable
    public String f8;
    @NotNullable
    public byte[] f9;
    @NotNullable
    public ByteBuffer f10;

    public ReferenceTypeFieldsClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof ReferenceTypeFieldsClassNotNullable)) {
	    return false;
	}
	ReferenceTypeFieldsClassNotNullable that = (ReferenceTypeFieldsClassNotNullable) o;
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (! f0.equals(that.f0)) {
	    return false;
	}
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (! f1.equals(that.f1)) {
	    return false;
	}
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	if (! f2.equals(that.f2)) {
	    return false;
	}
	if (f3 == null) {
	    if (that.f3 != null) {
		return false;
	    }
	}
	if (! f3.equals(that.f3)) {
	    return false;
	}
	if (f4 == null) {
	    if (that.f4 != null) {
		return false;
	    }
	}
	if (! f4.equals(that.f4)) {
	    return false;
	}
	if (f5 == null) {
	    if (that.f5 != null) {
		return false;
	    }
	}
	if (! f5.equals(that.f5)) {
	    return false;
	}
	if (f6 == null) {
	    if (that.f6 != null) {
		return false;
	    }
	}
	if (! f6.equals(that.f6)) {
	    return false;
	}
	if (f7 == null) {
	    if (that.f7 != null) {
		return false;
	    }
	}
	if (! f7.equals(that.f7)) {
	    return false;
	}
	if (f8 == null) {
	    if (that.f8 != null) {
		return false;
	    }
	}
	if (! f8.equals(that.f8)) {
	    return false;
	}
	if (f9 == null) {
	    if (that.f9 != null) {
		return false;
	    }
	}
	for (int i = 0; i < f9.length; ++i) {
	    if (f9[i] != that.f9[i]) {
		return false;
	    }
	}
	if (f10 == null) {
	    if (that.f10 != null) {
		return false;
	    }
	}
	byte[] b0 = TestSet.toByteArray(f10);
	byte[] b1 = TestSet.toByteArray(that.f10);
	for (int i = 0; i < b0.length; ++i) {
	    if (b0[i] != b1[i]) {
		return false;
	    }
	}
	return true;
    }
}
