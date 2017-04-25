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
package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class MessagePackDataformatTestBase
{
    protected MessagePackFactory factory;
    protected ByteArrayOutputStream out;
    protected ByteArrayInputStream in;
    protected ObjectMapper objectMapper;
    protected NormalPojo normalPojo;
    protected NestedListPojo nestedListPojo;
    protected NestedListComplexPojo nestedListComplexPojo;
    protected TinyPojo tinyPojo;
    protected ComplexPojo complexPojo;

    @Before
    public void setup()
    {
        factory = new MessagePackFactory();
        objectMapper = new ObjectMapper(factory);
        out = new ByteArrayOutputStream();
        in = new ByteArrayInputStream(new byte[4096]);

        normalPojo = new NormalPojo();
        normalPojo.setS("komamitsu");
        normalPojo.bool = true;
        normalPojo.i = Integer.MAX_VALUE;
        normalPojo.l = Long.MIN_VALUE;
        normalPojo.f = Float.MIN_VALUE;
        normalPojo.d = Double.MAX_VALUE;
        normalPojo.b = new byte[] {0x01, 0x02, (byte) 0xFE, (byte) 0xFF};
        normalPojo.bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        normalPojo.suit = Suit.HEART;

        nestedListPojo = new NestedListPojo();
        nestedListPojo.s = "a string";
        nestedListPojo.strs = Arrays.asList("string", "another string", "another string");

        tinyPojo = new TinyPojo();
        tinyPojo.t = "t string";

        nestedListComplexPojo = new NestedListComplexPojo();
        nestedListComplexPojo.s = "a string";
        nestedListComplexPojo.foos = new ArrayList<TinyPojo>();
        nestedListComplexPojo.foos.add(tinyPojo);

        complexPojo = new ComplexPojo();
        complexPojo.name = "komamitsu";
        complexPojo.age = 20;
        complexPojo.grades = Collections.singletonMap("math", 97);
        complexPojo.values = Arrays.asList("one", "two", "three");
    }

    @After
    public void teardown()
    {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum Suit
    {
        SPADE, HEART, DIAMOND, CLUB;
    }

    public static class NestedListPojo
    {
        public String s;
        public List<String> strs;
    }

    public static class ComplexPojo
    {
        public String name;
        public int age;
        public List<String> values;
        public Map<String, Integer> grades;
    }

    public static class TinyPojo
    {
        public String t;
    }

    public static class NestedListComplexPojo
    {
        public String s;
        public List<TinyPojo> foos;
    }

    public static class NormalPojo
    {
        String s;
        public boolean bool;
        public int i;
        public long l;
        public Float f;
        public Double d;
        public byte[] b;
        public BigInteger bi;
        public Suit suit;

        public String getS()
        {
            return s;
        }

        public void setS(String s)
        {
            this.s = s;
        }
    }

    public static class BinKeyPojo
    {
        public byte[] b;
        public String s;
    }

    public static class UsingCustomConstructorPojo
    {
        final String name;
        final int age;

        public UsingCustomConstructorPojo(@JsonProperty("name") String name, @JsonProperty("age") int age)
        {
            this.name = name;
            this.age = age;
        }

        public String getName()
        {
            return name;
        }

        public int getAge()
        {
            return age;
        }
    }

    @JsonIgnoreProperties({"foo", "bar"})
    public static class IgnoringPropertiesPojo
    {
        int code;

        // will not be written as JSON; nor assigned from JSON:
        @JsonIgnore
        public String internal;

        // no annotation, public field is read/written normally
        public String external;

        @JsonIgnore
        public void setCode(int c)
        {
            code = c;
        }

        // note: will also be ignored because setter has annotation!
        public int getCode()
        {
            return code;
        }
    }

    public static class ChangingPropertyNamesPojo
    {
        String name;

        // without annotation, we'd get "theName", but we want "name":
        @JsonProperty("name")
        public String getTheName()
        {
            return name;
        }

        // note: it is enough to add annotation on just getter OR setter;
        // so we can omit it here
        public void setTheName(String n)
        {
            name = n;
        }
    }

    protected interface FileSetup
    {
        void setup(File f)
                throws Exception;
    }

    protected File createTempFile()
            throws Exception
    {
        return createTempFile(null);
    }

    protected File createTempFile(FileSetup fileSetup)
            throws Exception
    {
        File tempFile = File.createTempFile("test", "msgpack");
        tempFile.deleteOnExit();
        if (fileSetup != null) {
            fileSetup.setup(tempFile);
        }
        return tempFile;
    }

    protected OutputStream createTempFileOutputStream()
            throws IOException
    {
        File tempFile = File.createTempFile("test", "msgpack");
        tempFile.deleteOnExit();
        return new FileOutputStream(tempFile);
    }
}
