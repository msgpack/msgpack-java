package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class InheritanceClassNotNullable extends SuperClass {
    @NotNullable
    public String f2;
    @NotNullable
    public int f3;

    public InheritanceClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof InheritanceClassNotNullable)) {
	    return false;
	}
	InheritanceClassNotNullable that = (InheritanceClassNotNullable) o;
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
