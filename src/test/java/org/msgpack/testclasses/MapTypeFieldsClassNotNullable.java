package org.msgpack.testclasses;

import java.util.Map;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class MapTypeFieldsClassNotNullable {
    @NotNullable
    public Map<Integer, Integer> f0;
    @NotNullable
    public Map<Integer, Integer> f1;
    @NotNullable
    public Map<String, Integer> f2;
    @NotNullable
    public Map<String, NestedClass> f3;

    public MapTypeFieldsClassNotNullable() {
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof MapTypeFieldsClassNotNullable)) {
	    return false;
	}
	MapTypeFieldsClassNotNullable that = (MapTypeFieldsClassNotNullable) o;
	// f0
	if (f0 == null) {
	    if (that.f0 != null) {
		return false;
	    }
	}
	for (Map.Entry<Integer, Integer> e : f0.entrySet()) {
	    Integer key = e.getKey();
	    Integer val = that.f0.get(key);
	    if (! e.getValue().equals(val)) {
		return false;
	    }
	}
	// f1
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	for (Map.Entry<Integer, Integer> e : f1.entrySet()) {
	    Integer key = e.getKey();
	    Integer val = that.f1.get(key);
	    if (! e.getValue().equals(val)) {
		return false;
	    }
	}
	// f2
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	for (Map.Entry<String, Integer> e : f2.entrySet()) {
	    String key = e.getKey();
	    Integer val = that.f2.get(key);
	    if (! e.getValue().equals(val)) {
		return false;
	    }
	}
	// f3
	if (f3 == null) {
	    if (that.f3 != null) {
		return false;
	    }
	}
	for (Map.Entry<String, NestedClass> e : f3.entrySet()) {
	    String key = e.getKey();
	    NestedClass val = that.f3.get(key);
	    if (! e.getValue().equals(val)) {
		return false;
	    }
	}
	return true;
    }

    @Ignore @Message
    public static class NestedClass {
	@NotNullable
	public String f0;

	public NestedClass() {}

	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof NestedClass)) {
		return false;
	    }
	    // f0
	    if (f0 == null) {
		if (this.f0 != null) {
		    return false;
		}
	    }
	    return true;
	}
    }
}
