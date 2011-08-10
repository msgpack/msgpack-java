package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public final class FinalClass {
    public int f0;

    public String f1;

    public FinalClass() {
    }

    public int getF0() {
        return f0;
    }

    public void setF0(int f0) {
        this.f0 = f0;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof FinalClass)) {
	    return false;
	}
	FinalClass that = (FinalClass) o;
	if (f0 != that.f0) {
	    return false;
	}
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (that.f1 != null) {
	    if (! f1.equals(that.f1)) {
		return false;
	    }
	}
	return true;
    }
}
