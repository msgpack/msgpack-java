package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public final class FinalClass {
    public int f0;
    public String f1;

    public FinalClass() {
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
	if (! f1.equals(that.f1)) {
	    return false;
	}
	return true;
    }
}
