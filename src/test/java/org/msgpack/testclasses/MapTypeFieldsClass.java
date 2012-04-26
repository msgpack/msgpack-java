package org.msgpack.testclasses;

import java.util.Map;

import org.junit.Ignore;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;


@Ignore @Message @Beans
public class MapTypeFieldsClass {
    public Map<Integer, Integer> f0;

    public Map<Integer, Integer> f1;

    public Map<String, Integer> f2;

    public Map<String, NestedClass> f3;

    public Map<String, int[]> f4;

    public Map<String, String[]> f5;

    public Map<String, NestedClass[]> f6;

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

    public Map<String, int[]> getF4() {
        return f4;
    }

    public void setF4(Map<String, int[]> f4) {
        this.f4 = f4;
    }

    public Map<String, String[]> getF5() {
        return f5;
    }

    public void setF5(Map<String, String[]> f5) {
        this.f5 = f5;
    }

    public Map<String, NestedClass[]> getF6() {
        return f6;
    }

    public void setF6(Map<String, NestedClass[]> f6) {
        this.f6 = f6;
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
	if (that.f0 != null) {
	    for (Map.Entry<Integer, Integer> e : f0.entrySet()) {
		Integer key = e.getKey();
		Integer val = that.f0.get(key);
		if (!e.getValue().equals(val)) {
		    return false;
		}
	    }
	}
	// f1
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (that.f1 != null) {
	    for (Map.Entry<Integer, Integer> e : f1.entrySet()) {
		Integer key = e.getKey();
		Integer val = that.f1.get(key);
		if (!e.getValue().equals(val)) {
		    return false;
		}
	    }
	}
	// f2
	if (f2 == null) {
	    if (that.f2 != null) {
		return false;
	    }
	}
	if (that.f2 != null) {
	    for (Map.Entry<String, Integer> e : f2.entrySet()) {
		String key = e.getKey();
		Integer val = that.f2.get(key);
		if (!e.getValue().equals(val)) {
		    return false;
		}
	    }
	}
	// f3
	if (f3 == null) {
	    if (that.f3 != null) {
		return false;
	    }
	}
	if (that.f3 != null) {
	    for (Map.Entry<String, NestedClass> e : f3.entrySet()) {
		String key = e.getKey();
		NestedClass val = that.f3.get(key);
		if (!e.getValue().equals(val)) {
		    return false;
		}
	    }
	}
	// f4
	if (f4 == null) {
	    if (that.f4 != null) {
	        return false;
	    }
	}
	if (that.f4 != null) {
	    for (Map.Entry<String, int[]> e : f4.entrySet()) {
	        String key = e.getKey();
	        int[] this_val = e.getValue();
	        int[] that_val = that.f4.get(key);
	        for (int i = 0; i < this_val.length; i++) {
	            if (this_val[i] != that_val[i]) {
	                return false;
	            }
	        }
	    }
	}
	// f5
        if (f5 == null) {
            if (that.f5 != null) {
                return false;
            }
        }
        if (that.f5 != null) {
            for (Map.Entry<String, String[]> e : f5.entrySet()) {
                String key = e.getKey();
                String[] this_val = e.getValue();
                String[] that_val = that.f5.get(key);
                for (int i = 0; i < this_val.length; i++) {
                    if (!this_val[i].equals(that_val[i])) {
                        return false;
                    }
                }
            }
        }
        // f6
        if (f6 == null) {
            if (that.f6 != null) {
                return false;
            }
        }
        if (that.f6 != null) {
            for (Map.Entry<String, NestedClass[]> e : f6.entrySet()) {
                String key = e.getKey();
                NestedClass[] this_val = e.getValue();
                NestedClass[] that_val = that.f6.get(key);
                for (int i = 0; i < this_val.length; i++) {
                    if (!this_val[i].equals(that_val[i])) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Ignore @Message @Beans
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
	    NestedClass that = (NestedClass) o;
	    // f0
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
}
