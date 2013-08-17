package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public class ReferenceCycleTypeFieldsClass {

    public ReferenceCycleTypeFieldsClass f0;

    public NestedClass f1;

    public String f2;

    public ReferenceCycleTypeFieldsClass() {}

    public void setF0(ReferenceCycleTypeFieldsClass f0) {
	this.f0 = f0;
    }

    public ReferenceCycleTypeFieldsClass getF0() {
	return f0;
    }

    public void setF1(NestedClass f1) {
	this.f1 = f1;
    }

    public NestedClass getF1() {
	return f1;
    }

    public void setF2(String f2) {
	this.f2 = f2;
    }

    public String getF2() {
	return f2;
    }

    @Override
    public boolean equals(Object o) {
	if (!(o instanceof ReferenceCycleTypeFieldsClass)) {
	    return false;
	}
	ReferenceCycleTypeFieldsClass that = (ReferenceCycleTypeFieldsClass) o;
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (that.f0 != null) {
	    if (!f0.equals(that.f0)) {
		return false;
	    }
	}
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (that.f1 != null) {
	    if (!f1.equals(that.f1)) {
		return false;
	    }
	}
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	if (that.f2 != null) {
	    if (!f2.equals(that.f2)) {
		return false;
	    }
	}
	return true;
    }

    @Ignore @Message @Beans
    public static class NestedClass {
	public ReferenceCycleTypeFieldsClass f0;

	public NestedClass() {}

	public ReferenceCycleTypeFieldsClass getF0() {
	    return f0;
	}

	public void setF0(ReferenceCycleTypeFieldsClass f0) {
	    this.f0 = f0;
	}

	@Override
	public boolean equals(Object o) {
	    if (! (o instanceof NestedClass)) {
		return false;
	    }
	    NestedClass that = (NestedClass) o;
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
	    return true;
	}
    }
}
