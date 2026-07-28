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

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TokenStreamContext;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MessagePackWriteContextTest
{
    private final MessagePackFactory factory = new MessagePackFactory();

    @Test
    public void testRootContext()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            TokenStreamContext ctx = gen.streamWriteContext();
            assertTrue(ctx.inRoot());
            assertFalse(ctx.inArray());
            assertFalse(ctx.inObject());
            assertNull(ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(0, ctx.getEntryCount());
            assertNull(ctx.getParent());
            gen.writeNumber(1);
        }
    }

    @Test
    public void testArrayContext()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            gen.writeStartArray();
            TokenStreamContext ctx = gen.streamWriteContext();

            assertFalse(ctx.inRoot());
            assertTrue(ctx.inArray());
            assertFalse(ctx.inObject());
            assertNull(ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(0, ctx.getEntryCount());
            assertNotNull(ctx.getParent());
            assertTrue(ctx.getParent().inRoot());

            gen.writeNumber(1);
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(1, ctx.getEntryCount());

            gen.writeNumber(2);
            assertEquals(1, ctx.getCurrentIndex());
            assertEquals(2, ctx.getEntryCount());

            gen.writeNumber(3);
            assertEquals(2, ctx.getCurrentIndex());
            assertEquals(3, ctx.getEntryCount());

            gen.writeEndArray();
            assertTrue(gen.streamWriteContext().inRoot());
        }
    }

    @Test
    public void testObjectContext()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            gen.writeStartObject();
            TokenStreamContext ctx = gen.streamWriteContext();

            assertFalse(ctx.inRoot());
            assertFalse(ctx.inArray());
            assertTrue(ctx.inObject());
            assertNull(ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(0, ctx.getEntryCount());

            gen.writeName("first");
            assertEquals("first", ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(0, ctx.getEntryCount());

            gen.writeNumber(1);
            assertEquals("first", ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(1, ctx.getEntryCount());

            gen.writeName("second");
            assertEquals("second", ctx.currentName());
            assertEquals(0, ctx.getCurrentIndex());
            assertEquals(1, ctx.getEntryCount());

            gen.writeString("value");
            assertEquals(1, ctx.getCurrentIndex());
            assertEquals(2, ctx.getEntryCount());

            gen.writeEndObject();
            assertTrue(gen.streamWriteContext().inRoot());
        }
    }

    @Test
    public void testNestedObjectInArray()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            gen.writeStartArray();
            TokenStreamContext arrayCtx = gen.streamWriteContext();
            assertTrue(arrayCtx.inArray());

            gen.writeStartObject();
            TokenStreamContext objectCtx = gen.streamWriteContext();
            assertTrue(objectCtx.inObject());
            assertSame(arrayCtx, objectCtx.getParent());

            gen.writeName("key");
            assertEquals("key", objectCtx.currentName());
            assertEquals("key", gen.streamWriteContext().currentName());

            gen.writeNumber(42);
            gen.writeEndObject();

            assertSame(arrayCtx, gen.streamWriteContext());
            assertEquals(0, arrayCtx.getCurrentIndex());
            assertEquals(1, arrayCtx.getEntryCount());

            gen.writeEndArray();
        }
    }

    @Test
    public void testCurrentValue()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            Object pojo = new Object();
            gen.writeStartObject(pojo);
            TokenStreamContext ctx = gen.streamWriteContext();
            assertEquals(pojo, ctx.currentValue());

            Object inner = new Object();
            gen.writeName("arr");
            gen.writeStartArray(inner);
            assertEquals(inner, gen.streamWriteContext().currentValue());
            gen.writeEndArray();

            gen.writeEndObject();
        }
    }

    @Test
    public void testChildContextIsReused()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            gen.writeStartArray();

            gen.writeStartObject();
            TokenStreamContext first = gen.streamWriteContext();
            gen.writeName("k");
            gen.writeNumber(1);
            gen.writeEndObject();

            gen.writeStartObject();
            TokenStreamContext second = gen.streamWriteContext();
            assertSame(first, second);
            assertNull(second.currentName());
            assertEquals(0, second.getCurrentIndex());
            assertEquals(0, second.getEntryCount());

            gen.writeName("k2");
            gen.writeNumber(2);
            gen.writeEndObject();

            gen.writeEndArray();
        }
    }

    @Test
    public void testAssignCurrentValue()
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), baos)) {
            gen.writeStartObject();
            TokenStreamContext ctx = gen.streamWriteContext();
            assertNull(ctx.currentValue());

            Object v = new Object();
            gen.assignCurrentValue(v);
            assertSame(v, ctx.currentValue());
            assertSame(v, gen.currentValue());

            gen.writeName("k");
            gen.writeNumber(1);
            gen.writeEndObject();
        }
    }
}
