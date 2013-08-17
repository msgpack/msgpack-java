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
    private int f1 = 5;

    @NotNullable
    protected int f2 = 10;

    @NotNullable
    int f3 = 15;

    @NotNullable
    public String f4;

    @NotNullable
    private String f5 = "nishizawa";

    @NotNullable
    protected String f6 = "hello";

    @NotNullable
    String f7 = "muga";

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

    @NotNullable
    public String getF4() {
	return f4;
    }

    @NotNullable
    public void setF4(String f4) {
	this.f4 = f4;
    }

    @NotNullable
    public String getF5() {
	return f5;
    }

    @NotNullable
    public void setF5(String f5) {
	this.f5 = f5;
    }

    @NotNullable
    public String getF6() {
	return f6;
    }

    @NotNullable
    public void setF6(String f6) {
	this.f6 = f6;
    }

    @NotNullable
    public String getF7() {
	return f7;
    }

    @NotNullable
    public void setF7(String f7) {
	this.f7 = f7;
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
	if (f4 == null) {
	    if (that.f4 != null) {
		return false;
	    }
	}
	if (that.f4 != null && ! f4.equals(that.f4)) {
	    return false;
	}
	if (f5 == null) {
	    if (that.f5 != null) {
		return false;
	    }
	}
	if (that.f5 != null && ! f5.equals(that.f5)) {
	    return false;
	}
	if (f6 == null) {
	    if (that.f6 != null) {
		return false;
	    }
	}
	if (that.f6 != null && ! f6.equals(that.f6)) {
	    return false;
	}
	if (f7 == null) {
	    if (that.f7 != null) {
		return false;
	    }
	}
	if (that.f7 != null && ! f7.equals(that.f7)) {
	    return false;
	}
	return true;
    }
}
