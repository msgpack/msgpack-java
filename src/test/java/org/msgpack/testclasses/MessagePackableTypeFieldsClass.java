package org.msgpack.testclasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.msgpack.MessagePackable;
import org.msgpack.annotation.Message;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;


@Ignore @Message
public class MessagePackableTypeFieldsClass {
    public String f0;
    public NestedClass f1;

    public MessagePackableTypeFieldsClass() {}

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
    }
}
