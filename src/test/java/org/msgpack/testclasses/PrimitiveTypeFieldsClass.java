package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public class PrimitiveTypeFieldsClass {
    public byte f0;

    public short f1;

    public int f2;

    public long f3;

    public float f4;

    public double f5;

    public boolean f6;

    public PrimitiveTypeFieldsClass() {}

    public byte getF0() {
        return f0;
    }

    public void setF0(byte f0) {
        this.f0 = f0;
    }

    public short getF1() {
        return f1;
    }

    public void setF1(short f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    public long getF3() {
        return f3;
    }

    public void setF3(long f3) {
        this.f3 = f3;
    }

    public float getF4() {
        return f4;
    }

    public void setF4(float f4) {
        this.f4 = f4;
    }

    public double getF5() {
        return f5;
    }

    public void setF5(double f5) {
        this.f5 = f5;
    }

    public boolean isF6() {
        return f6;
    }

    public void setF6(boolean f6) {
        this.f6 = f6;
    }

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
