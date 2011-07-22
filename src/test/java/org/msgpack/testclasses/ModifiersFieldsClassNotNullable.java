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

    public int getF0() {
        return f0;
    }

    public void setF0(int f0) {
        this.f0 = f0;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    public int getF3() {
        return f3;
    }

    public void setF3(int f3) {
        this.f3 = f3;
    }

    public int getF4() {
        return f4;
    }

    public void setF4(int f4) {
        this.f4 = f4;
    }

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
