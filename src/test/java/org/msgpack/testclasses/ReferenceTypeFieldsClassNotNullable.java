package org.msgpack.testclasses;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.template.builder.TestSet;
import org.msgpack.type.RubySymbol;


@Ignore @Message @Beans
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
    
    @NotNullable
    public RubySymbol f11;
    
    @NotNullable
    public Date f12;

    public ReferenceTypeFieldsClassNotNullable() {}

    @NotNullable
    public Byte getF0() {
        return f0;
    }

    @NotNullable
    public void setF0(Byte f0) {
        this.f0 = f0;
    }

    @NotNullable
    public Short getF1() {
        return f1;
    }

    @NotNullable
    public void setF1(Short f1) {
        this.f1 = f1;
    }

    @NotNullable
    public Integer getF2() {
        return f2;
    }

    @NotNullable
    public void setF2(Integer f2) {
        this.f2 = f2;
    }

    @NotNullable
    public Long getF3() {
        return f3;
    }

    @NotNullable
    public void setF3(Long f3) {
        this.f3 = f3;
    }

    @NotNullable
    public Float getF4() {
        return f4;
    }

    @NotNullable
    public void setF4(Float f4) {
        this.f4 = f4;
    }

    @NotNullable
    public Double getF5() {
        return f5;
    }

    @NotNullable
    public void setF5(Double f5) {
        this.f5 = f5;
    }

    @NotNullable
    public Boolean getF6() {
        return f6;
    }

    @NotNullable
    public void setF6(Boolean f6) {
        this.f6 = f6;
    }

    @NotNullable
    public BigInteger getF7() {
        return f7;
    }

    @NotNullable
    public void setF7(BigInteger f7) {
        this.f7 = f7;
    }

    @NotNullable
    public String getF8() {
        return f8;
    }

    @NotNullable
    public void setF8(String f8) {
        this.f8 = f8;
    }

    @NotNullable
    public byte[] getF9() {
        return f9;
    }

    @NotNullable
    public void setF9(byte[] f9) {
        this.f9 = f9;
    }

    @NotNullable
    public ByteBuffer getF10() {
        return f10;
    }

    @NotNullable
    public void setF10(ByteBuffer f10) {
        this.f10 = f10;
    }
    
    @NotNullable
    public RubySymbol getF11() {
    	return f11;
    }
    
    @NotNullable
    public void setF11(RubySymbol f11) {
    	this.f11 = f11;
    }

    @NotNullable
    public Date getF12() {
    	return f12;
    }
    
    @NotNullable
    public void setF12(Date f12) {
    	this.f12 = f12;
    }
    
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
	if (f11 == null) {
		if (that.f11 != null) {
			return false;
		}
	}
	if (that.f11 != null && ! f11.equals(that.f11)) {
	    return false;
	}
	if (f12 == null) {
		if (that.f12 != null) {
			return false;
		}
	}
	if (that.f12 != null && ! f12.equals(that.f12)) {
	    return false;
	}
	return true;
    }
}
