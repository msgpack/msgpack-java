package org.msgpack.testclasses;

import java.util.Map;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class MapTypeFieldsClass {
    public Map<Integer, Integer> f0;

    public Map<Integer, Integer> f1;

    public Map<String, Integer> f2;

    public Map<String, NestedClass> f3;

    public MapTypeFieldsClass() {
    }

    public Map<Integer, Integer> getF0() {
        return f0;
    }

    public void setF0(Map<Integer, Integer> f0) {
        this.f0 = f0;
    }

    public Map<Integer, Integer> getF1() {
        return f1;
    }

    public void setF1(Map<Integer, Integer> f1) {
        this.f1 = f1;
    }

    public Map<String, Integer> getF2() {
        return f2;
    }

    public void setF2(Map<String, Integer> f2) {
        this.f2 = f2;
    }

    public Map<String, NestedClass> getF3() {
        return f3;
    }

    public void setF3(Map<String, NestedClass> f3) {
        this.f3 = f3;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof MapTypeFieldsClass)) {
	    return false;
	}
	MapTypeFieldsClass that = (MapTypeFieldsClass) o;
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
	public String f0;

	public NestedClass() {}

	public String getF0() {
	    return f0;
	}

	public void setF0(String f0) {
	    this.f0 = f0;
	}

	@Override
	public boolean equals(Object o) {
	    if (! (o instanceof NestedClass)) {
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
