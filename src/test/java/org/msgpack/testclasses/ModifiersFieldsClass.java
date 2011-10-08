package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public class ModifiersFieldsClass {
    public int f0;

    private int f1 = 5;

    protected int f2 = 10;

    int f3 = 15;

    public String f4;

    private String f5 = "nishizawa";

    protected String f6 = "muga";

    String f7 = "hello";

    public ModifiersFieldsClass() {}

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

    public String getF4() {
	return f4;
    }

    public void setF4(String f4) {
	this.f4 = f4;
    }

    public String getF5() {
	return f5;
    }

    public void setF5(String f5) {
	this.f5 = f5;
    }

    public String getF6() {
	return f6;
    }

    public void setF6(String f6) {
	this.f6 = f6;
    }

    public String getF7() {
	return f7;
    }

    public void setF7(String f7) {
	this.f7 = f7;
    }

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
