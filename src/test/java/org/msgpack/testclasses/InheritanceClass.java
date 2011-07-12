package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class InheritanceClass extends SuperClass {
    public String f2;
    public int f3;

    public InheritanceClass() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof InheritanceClass)) {
	    return false;
	}
	InheritanceClass that = (InheritanceClass) o;
	// f2
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	if (! f2.equals(that.f2)) {
	    return false;
	}
	// f3
	if (f3 != that.f3) {
	    return false;
	}
	return true;
    }
}
