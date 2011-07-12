package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class ModifiersFieldsClassNotNullable {
    @NotNullable
    public int f0;
    @NotNullable
    public final int f1 = 1;
    @NotNullable
    private int f2;
    @NotNullable
    protected int f3;
    @NotNullable
    int f4;

    public ModifiersFieldsClassNotNullable() {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof ModifiersFieldsClassNotNullable)) {
	    return false;
	}
	ModifiersFieldsClassNotNullable that = (ModifiersFieldsClassNotNullable) o;
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
