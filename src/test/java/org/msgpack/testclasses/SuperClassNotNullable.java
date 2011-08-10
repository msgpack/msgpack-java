package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message @Beans
public class SuperClassNotNullable {
    @NotNullable
    public String f0;

    public SuperClassNotNullable() {}

    @NotNullable
    public String getF0() {
        return f0;
    }

    @NotNullable
    public void setF0(String f0) {
        this.f0 = f0;
    }

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
	if (that.f0 != null) {
	    if (! f0.equals(that.f0)) {
		return false;
	    }
	}
	return true;
    }
}
