package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class ModifiersFieldsClass {
    public int f0;
    public final int f1 = 1;
    private int f2;
    protected int f3;
    int f4;

    public ModifiersFieldsClass() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof ModifiersFieldsClass)) {
	    return false;
	}
	ModifiersFieldsClass that = (ModifiersFieldsClass) o;
	if (f0 != that.f0) {
	    return false;
	}
	if (f1 != that.f1) {
	    return false;
	}
	if (f2 != that.f2) {
	    return false;
	}
	if (f3 != that.f3) {
	    return false;
	}
	if (f4 != that.f4) {
	    return false;
	}
	return true;
    }
}
