package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class InheritanceClass extends SuperClass {
    public String f1;
    public int f2;

    public InheritanceClass() {}

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
	if (! f0.equals(that.f0)) {
	    return false;
	}
	// f1
	if (f2 != that.f2) {
	    return false;
	}
	if (! f1.equals(that.f1)) {
	    return false;
	}
	// f2
	if (f2 != that.f2) {
	    return false;
	}
	return true;
    }
}
