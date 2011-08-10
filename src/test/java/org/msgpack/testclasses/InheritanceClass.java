package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public class InheritanceClass extends SuperClass {
    public String f1;

    public int f2;

    public InheritanceClass() {}

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof InheritanceClass)) {
	    return false;
	}
	InheritanceClass that = (InheritanceClass) o;
	// f0
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (that.f0 != null) {
	    if (! f0.equals(that.f0)) {
		return false;
	    }
	}
	// f1
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
	// f2
	if (f2 != that.f2) {
	    return false;
	}
	return true;
    }
}
