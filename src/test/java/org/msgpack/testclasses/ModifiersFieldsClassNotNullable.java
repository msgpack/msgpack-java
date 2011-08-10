package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message @Beans
public class ModifiersFieldsClassNotNullable {
    @NotNullable
    public int f0;

    @NotNullable
    private int f1;

    @NotNullable
    protected int f2;

    @NotNullable
    int f3;

    public ModifiersFieldsClassNotNullable() {}

    @NotNullable
    public int getF0() {
        return f0;
    }

    @NotNullable
    public void setF0(int f0) {
        this.f0 = f0;
    }

    @NotNullable
    public int getF2() {
        return f2;
    }

    @NotNullable
    public void setF2(int f2) {
        this.f2 = f2;
    }

    @NotNullable
    public int getF3() {
        return f3;
    }

    @NotNullable
    public void setF3(int f3) {
        this.f3 = f3;
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
	return true;
    }
}
