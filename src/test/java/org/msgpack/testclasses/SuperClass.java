package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class SuperClass {
    public String f0;

    public SuperClass() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof SuperClass)) {
	    return false;
	}
	SuperClass that = (SuperClass) o;
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
