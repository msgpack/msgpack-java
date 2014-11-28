//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.core.example;

import org.msgpack.core.*;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.*;
import org.msgpack.value.holder.FloatHolder;
import org.msgpack.value.holder.IntegerHolder;
import org.msgpack.value.holder.ValueHolder;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.util.Random;

/**
 * This class describes the usage of MessagePack v07
 */
public class MessagePackExample {


    /**
     * Basic usage example
     * @throws IOException
     */
    public static void basicUsage() throws IOException {

        // Serialize with MessagePacker
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer
            .packInt(1)
            .packString("leo")
            .packArrayHeader(2)
            .packString("xxx-xxxx")
            .packString("yyy-yyyy");
        packer.close();

        // Deserialize with MessageUnpacker
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(out.toByteArray());
        int id = unpacker.unpackInt();             // 1
        String name = unpacker.unpackString();     // "leo"
        int numPhones = unpacker.unpackArrayHeader();  // 2
        String[] phones = new String[numPhones];
        for(int i=0; i < numPhones; ++i) {
            phones[i] = unpacker.unpackString();   // phones = {"xxx-xxxx", "yyy-yyyy"}
        }
        unpacker.close();

        System.out.println(String.format("id:%d, name:%s, phone:[%s]", id, name, join(phones)));
    }

    private static String join(String[] in) {
        StringBuilder s = new StringBuilder();
        for(int i=0; i<in.length; ++i) {
            if(i > 0) {
                s.append(", ");
            }
            s.append(in[i]);
        }
        return s.toString();
    }

    /**
     * Packing various types of data
     * @throws IOException
     */
    public static void packer() throws IOException {

        // Create a MesagePacker (encoder) instance
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);

        // pack (encode) primitive values in message pack format
        packer.packBoolean(true);
        packer.packShort((short) 34);
        packer.packInt(1);
        packer.packLong(33000000000L);

        packer.packFloat(0.1f);
        packer.packDouble(3.14159263);
        packer.packByte((byte) 0x80);

        packer.packNil();

        // pack strings (in UTF-8)
        packer.packString("hello message pack!");

        // [Advanced] write a raw UTF-8 string
        byte[] s = "utf-8 strings".getBytes(MessagePack.UTF8);
        packer.packRawStringHeader(s.length);
        packer.writePayload(s);

        // pack arrays
        int[] arr = new int[]{3, 5, 1, 0, -1, 255};
        packer.packArrayHeader(arr.length);
        for(int v : arr) {
            packer.packInt(v);
        }

        // pack map (key -> value) elements
        packer.packMapHeader(2); // the number of (key, value) pairs
        // Put "apple" -> 1
        packer.packString("apple");
        packer.packInt(1);
        // Put "banana" -> 2
        packer.packString("banana");
        packer.packInt(2);

        // pack binary data
        byte[] ba = new byte[]{1, 2, 3, 4};
        packer.packBinaryHeader(ba.length);
        packer.writePayload(ba);


        // Write ext type data: https://github.com/msgpack/msgpack/blob/master/spec.md#ext-format-family
        byte[] extData = "custom data type".getBytes(MessagePack.UTF8);
        packer.packExtendedTypeHeader(1, 10);  // type number [0, 127], data byte length
        packer.writePayload(extData);

        // Succinct syntax for packing
        packer
            .packInt(1)
            .packString("leo")
            .packArrayHeader(2)
            .packString("xxx-xxxx")
            .packString("yyy-yyyy");


        // [Advanced] write data using ByteBuffer
        ByteBuffer bb = ByteBuffer.wrap(new byte[] {'b', 'i', 'n', 'a', 'r', 'y', 'd', 'a', 't', 'a'});
        packer.packBinaryHeader(bb.remaining());
        packer.writePayload(bb);

    }


    /**
     * An example of reading and writing MessagePack data
     * @throws IOException
     */
    public static void readAndWriteFile() throws IOException {

        File tempFile = File.createTempFile("target/tmp", ".txt");
        tempFile.deleteOnExit();

        // Write packed data to a file. No need exists to wrap the file stream with BufferedOutputStream, since MessagePacker has its own buffer
        MessagePacker packer = MessagePack.newDefaultPacker(new FileOutputStream(tempFile));
        packer.packInt(1);
        packer.packString("Hello Message Pack!");
        packer.packArrayHeader(2).packFloat(0.1f).packDouble(0.342);
        packer.close();

        // Read packed data from a file. No need exists to wrap the file stream with an buffer
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile));

        while(unpacker.hasNext()) {
            // [Advanced] You can check the detailed data format with getNextFormat()
            // Here is a list of message pack data format: https://github.com/msgpack/msgpack/blob/master/spec.md#overview
            MessageFormat format = unpacker.getNextFormat();

            // Alternatively you can use ValueHolder to extract a value of any type
            // NOTE: Value interface is in a preliminary state, so the following code might change in future releases
            ValueHolder v = new ValueHolder();
            format = unpacker.unpackValue(v);
            switch(format.getValueType()) {
                case NIL:
                    Value nil = v.get();
                    nil.isNilValue(); // true
                    System.out.println("read nil");
                    break;
                case BOOLEAN:
                    boolean b = v.get().asBooleanValue().toBoolean();
                    System.out.println("read boolean: " + b);
                    break;
                case INTEGER:
                    IntegerHolder ih = v.getIntegerHolder();
                    if(ih.isValidInt()) { // int range check [-2^31-1, 2^31-1]
                        int i = ih.asInt();
                        System.out.println("read int: " + i);
                    }
                    else {
                        long l = ih.asLong();
                        System.out.println("read long: " + l);
                    }
                    break;
                case FLOAT:
                    FloatHolder fh = v.getFloatHolder();
                    float f = fh.toFloat();   // read as float
                    double d = fh.toDouble(); // read as double
                    System.out.println("read float: " + d);
                    break;
                case STRING:
                    String s = v.get().asStringValue().toString();
                    System.out.println("read string: " + s);
                    break;
                case BINARY:
                    // Message buffer is an efficient byte buffer
                    MessageBuffer mb = v.get().asBinaryValue().toMessageBuffer();
                    System.out.println("read binary: " + mb.toHexString(0, mb.size()));
                    break;
                case ARRAY:
                    ArrayValue arr = v.get().asArrayValue();
                    for(Value a : arr) {
                        System.out.println("read array element: " + a);
                    }
                    break;
                case EXTENDED:
                    ExtendedValue ev = v.get().asExtendedValue();
                    int extType = ev.getExtType();
                    byte[] extValue = ev.toByteArray();
                    break;
            }
        }

    }

    /**
     * Example of using custom MessagePack configuration
     * @throws IOException
     */
    public static void configuration() throws IOException {

        // Build a conifiguration
        MessagePack.Config config = new MessagePack.ConfigBuilder()
            .onMalFormedInput(CodingErrorAction.REPLACE)         // Drop malformed and unmappable UTF-8 characters
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
            .packerBufferSize(8192 * 2)
            .build();
        // Create a  that uses this configuration
        MessagePack msgpack = new MessagePack(config);

        // Pack data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = msgpack.newPacker(out);
        packer.packInt(10);
        packer.packBoolean(true);
        packer.close();

        // Unpack data
        byte[] packedData = out.toByteArray();
        MessageUnpacker unpacker = msgpack.newUnpacker(packedData);
        int i = unpacker.unpackInt();  // 10
        boolean b = unpacker.unpackBoolean(); // true
        unpacker.close();
    }





}
