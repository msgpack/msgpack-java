package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class InheritanceClassNotNullable extends SuperClassNotNullable {
    @NotNullable
    public String f1;
    @NotNullable
    public int f2;

    public InheritanceClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof InheritanceClassNotNullable)) {
	    return false;
	}
	InheritanceClassNotNullable that = (InheritanceClassNotNullable) o;
	// f1
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
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
