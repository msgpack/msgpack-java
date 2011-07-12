package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class UserDefinedTypeFieldsClassNotNullable {
    @NotNullable
    public NestedClass1 f0;
    @NotNullable
    public NestedClass2 f1;

    public UserDefinedTypeFieldsClassNotNullable () {}

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof UserDefinedTypeFieldsClassNotNullable)) {
	    return false;
	}
	UserDefinedTypeFieldsClassNotNullable that = (UserDefinedTypeFieldsClassNotNullable) o;
	// f0
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	if (! f0.equals(that.f0)) {
	    return false;
	}
	// f1
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

    @Ignore @Message
    public static class NestedClass1 {
	@NotNullable
	public int f0;
	@NotNullable
	public String f1;

	public NestedClass1() {}

	@Override
	public boolean equals(Object o) {
	    if (! (o instanceof NestedClass1)) {
		return false;
	    }
	    NestedClass1 that = (NestedClass1) o;
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

    @Ignore @Message
    public static class NestedClass2 {
	@NotNullable
	public int f0;
	@NotNullable
	public String f1;

	public NestedClass2() {}

	@Override
	public boolean equals(Object o) {
	    if (! (o instanceof NestedClass2)) {
		return false;
	    }
	    NestedClass2 that = (NestedClass2) o;
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
}
