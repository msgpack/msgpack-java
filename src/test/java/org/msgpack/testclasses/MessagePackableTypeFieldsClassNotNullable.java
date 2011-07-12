package org.msgpack.testclasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.msgpack.MessagePackable;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;


@Ignore @Message
public class MessagePackableTypeFieldsClassNotNullable {
    @NotNullable
    public String f0;
    @NotNullable
    public NestedClass f1;

    @Override
    public boolean equals(Object o) {
	if (! (o instanceof MessagePackableTypeFieldsClass)) {
	    return false;
	}
	MessagePackableTypeFieldsClass that = new MessagePackableTypeFieldsClass();
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

    public MessagePackableTypeFieldsClassNotNullable() {}

    @Ignore
    public static class NestedClass implements MessagePackable {
        public String f0;
        public int[] f1;
        public List<String> f2;

        public NestedClass() { }

        public void writeTo(Packer packer) throws IOException {
            packer.writeArrayBegin(3);
                packer.writeString(f0);
                packer.writeArrayBegin(f1.length);
                    for(int e : f1) {
                        packer.writeInt(e);
                    }
                packer.writeArrayEnd();
                packer.writeArrayBegin(f2.size());
                    for(String e : f2) {
                        packer.writeString(e);
                    }
                packer.writeArrayEnd();
            packer.writeArrayEnd();
        }

        public void readFrom(Unpacker uunpacker) throws IOException {
            uunpacker.readArrayBegin();
                f0 = uunpacker.readString();
                int nf1 = uunpacker.readArrayBegin();
                    f1 = new int[nf1];
                    for(int i=0; i < nf1; i++) {
                        f1[i] = uunpacker.readInt();
                    }
                uunpacker.readArrayEnd();
                int nf2 = uunpacker.readArrayBegin();
                    f2 = new ArrayList<String>(nf2);
                    for(int i=0; i < nf2; i++) {
                        f2.add(uunpacker.readString());
                    }
                uunpacker.readArrayEnd();
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
            if (! f0.equals(that.f0)) {
        	return false;
            }
            // f1
            if (f1 == null) {
        	if (that.f1 != null) {
        	    return false;
        	}
            }
            if (f1.length != that.f1.length) {
        	return false;
            }
            for (int i = 0; i < f1.length; ++i) {
        	if (f1[i] != that.f1[i]) {
        	    return false;
        	}
            }
            // f2
            if (f2 == null) {
        	if (that.f2 != null) {
        	    return false;
        	}
            }
            Iterator<String> this_f2_iter = f2.iterator();
            Iterator<String> that_f2_iter = that.f2.iterator();
            for (; this_f2_iter.hasNext(); ) {
        	if (! this_f2_iter.next().equals(that_f2_iter.next())) {
        	    return false;
        	}
            }
            return true;
        }
    }
}
