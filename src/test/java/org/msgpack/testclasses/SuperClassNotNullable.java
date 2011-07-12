package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class SuperClassNotNullable {
    @NotNullable
    public String f0;

    public SuperClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof SuperClassNotNullable)) {
	    return false;
	}
	SuperClassNotNullable that = (SuperClassNotNullable) o;
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (! f0.equals(that.f0)) {
	    return false;
	}
	return true;
    }
}
