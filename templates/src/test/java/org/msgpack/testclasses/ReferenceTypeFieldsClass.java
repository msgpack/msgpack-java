package org.msgpack.testclasses;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.template.builder.TestSet;


@Ignore @Message @Beans
public class ReferenceTypeFieldsClass {
    public Byte f0;

    public Short f1;

    public Integer f2;

    public Long f3;

    public Float f4;

    public Double f5;

    public Boolean f6;

    public BigInteger f7;

    public String f8;

    public byte[] f9;

    public ByteBuffer f10;

    public ReferenceTypeFieldsClass() {}

    public Byte getF0() {
        return f0;
    }

    public void setF0(Byte f0) {
        this.f0 = f0;
    }

    public Short getF1() {
        return f1;
    }

    public void setF1(Short f1) {
        this.f1 = f1;
    }

    public Integer getF2() {
        return f2;
    }

    public void setF2(Integer f2) {
        this.f2 = f2;
    }

    public Long getF3() {
        return f3;
    }

    public void setF3(Long f3) {
        this.f3 = f3;
    }

    public Float getF4() {
        return f4;
    }

    public void setF4(Float f4) {
        this.f4 = f4;
    }

    public Double getF5() {
        return f5;
    }

    public void setF5(Double f5) {
        this.f5 = f5;
    }

    public Boolean getF6() {
        return f6;
    }

    public void setF6(Boolean f6) {
        this.f6 = f6;
    }

    public BigInteger getF7() {
        return f7;
    }

    public void setF7(BigInteger f7) {
        this.f7 = f7;
    }

    public String getF8() {
        return f8;
    }

    public void setF8(String f8) {
        this.f8 = f8;
    }

    public byte[] getF9() {
        return f9;
    }

    public void setF9(byte[] f9) {
        this.f9 = f9;
    }

    public ByteBuffer getF10() {
        return f10;
    }

    public void setF10(ByteBuffer f10) {
        this.f10 = f10;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof ReferenceTypeFieldsClass)) {
	    return false;
	}
	ReferenceTypeFieldsClass that = (ReferenceTypeFieldsClass) o;
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (that.f0 != null && ! f0.equals(that.f0)) {
	    return false;
	}
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (that.f1 != null && ! f1.equals(that.f1)) {
	    return false;
	}
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	if (that.f2 != null && ! f2.equals(that.f2)) {
	    return false;
	}
	if (f3 == null) {
	    if (that.f3 != null) {
		return false;
	    }
	}
	if (that.f3 != null && ! f3.equals(that.f3)) {
	    return false;
	}
	if (f4 == null) {
	    if (that.f4 != null) {
		return false;
	    }
	}
	if (that.f4 != null && ! f4.equals(that.f4)) {
	    return false;
	}
	if (f5 == null) {
	    if (that.f5 != null) {
		return false;
	    }
	}
	if (that.f5 != null && ! f5.equals(that.f5)) {
	    return false;
	}
	if (f6 == null) {
	    if (that.f6 != null) {
		return false;
	    }
	}
	if (that.f6 != null && ! f6.equals(that.f6)) {
	    return false;
	}
	if (f7 == null) {
	    if (that.f7 != null) {
		return false;
	    }
	}
	if (that.f7 != null && ! f7.equals(that.f7)) {
	    return false;
	}
	if (f8 == null) {
	    if (that.f8 != null) {
		return false;
	    }
	}
	if (that.f8 != null && ! f8.equals(that.f8)) {
	    return false;
	}
	if (f9 == null) {
	    if (that.f9 != null) {
		return false;
	    }
	}
	if (that.f9 != null) {
	    for (int i = 0; i < f9.length; ++i) {
		if (f9[i] != that.f9[i]) {
		    return false;
		}
	    }
	}
	if (f10 == null) {
	    if (that.f10 != null) {
		return false;
	    }
	}
	if (that.f10 != null) {
	    byte[] b0 = TestSet.toByteArray(f10);
	    byte[] b1 = TestSet.toByteArray(that.f10);
	    for (int i = 0; i < b0.length; ++i) {
		if (b0[i] != b1[i]) {
		    return false;
		}
	    }
	}
	return true;
    }
}
