package org.msgpack.testclasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.msgpack.MessagePackable;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Message;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;


@Ignore @Message @Beans
public class MessagePackableTypeFieldsClass {
    public String f0;

    public NestedClass f1;

    public MessagePackableTypeFieldsClass() {}

    public String getF0() {
        return f0;
    }

    public void setF0(String f0) {
        this.f0 = f0;
    }

    public NestedClass getF1() {
        return f1;
    }

    public void setF1(NestedClass f1) {
        this.f1 = f1;
    }

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof MessagePackableTypeFieldsClass)) {
	    return false;
	}
	MessagePackableTypeFieldsClass that = (MessagePackableTypeFieldsClass) o;
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
	// f1
	if (f1 == null) {
	    if (that.f1 != null) {
		return false;
	    }
	}
	if (that.f1 != null) {
	    if (! f1.equals(that.f1)) {
		return false;
	    }
	}
	return true;
    }

    @Ignore
    public static class NestedClass implements MessagePackable {
        public String f0;

        public int[] f1;

        public List<String> f2;

        public NestedClass() { }

        public String getF0() {
	    return f0;
	}

	public void setF0(String f0) {
	    this.f0 = f0;
	}

	public int[] getF1() {
	    return f1;
	}

	public void setF1(int[] f1) {
	    this.f1 = f1;
	}

	public List<String> getF2() {
	    return f2;
	}

	public void setF2(List<String> f2) {
	    this.f2 = f2;
	}

	public void writeTo(Packer packer) throws IOException {
            packer.writeArrayBegin(3);
            {
                packer.write(f0);
                packer.writeArrayBegin(f1.length);
                {
                    for(int e : f1) {
                        packer.write(e);
                    }
                }
                packer.writeArrayEnd();
                packer.writeArrayBegin(f2.size());
                {
                    for(String e : f2) {
                        packer.write(e);
                    }
                }
                packer.writeArrayEnd();
            }
            packer.writeArrayEnd();
        }

        public void readFrom(Unpacker uunpacker) throws IOException {
            uunpacker.readArrayBegin();
            {
                f0 = uunpacker.readString();
                int nf1 = uunpacker.readArrayBegin();
                {
                    f1 = new int[nf1];
                    for(int i=0; i < nf1; i++) {
                        f1[i] = uunpacker.readInt();
                    }
                }
                uunpacker.readArrayEnd();
                int nf2 = uunpacker.readArrayBegin();
                {
                    f2 = new ArrayList<String>(nf2);
                    for(int i=0; i < nf2; i++) {
                        f2.add(uunpacker.readString());
                    }
                }
                uunpacker.readArrayEnd();
            }
            uunpacker.readArrayEnd();
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
            // f1
            if (f1 == null) {
        	if (that.f1 != null) {
        	    return false;
        	}
            }
            if (that.f1 != null) {
        	if (f1.length != that.f1.length) {
        	    return false;
        	}
        	for (int i = 0; i < f1.length; ++i) {
        	    if (f1[i] != that.f1[i]) {
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
        	Iterator<String> this_f2_iter = f2.iterator();
        	Iterator<String> that_f2_iter = that.f2.iterator();
        	for (; this_f2_iter.hasNext(); ) {
        	    if (! this_f2_iter.next().equals(that_f2_iter.next())) {
        		return false;
        	    }
        	}
            }
            return true;
        }
    }
}
