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

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class CloserServiceTest
{
    static class Container
    {
        private final CloseableObject closeableObject;

        static class CloseableObject
            implements Closeable
        {
            private final AtomicBoolean closed;

            CloseableObject(AtomicBoolean closed)
            {
                this.closed = closed;
            }

            @Override
            public void close()
                    throws IOException
            {
                closed.set(true);
            }
        }

        Container(CloserService closerService, AtomicBoolean closed)
        {
            closeableObject = new CloseableObject(closed);
            closerService.addFinalizer(this, closeableObject);
        }
    }

    @Test
    public void test()
            throws InterruptedException
    {
        AtomicBoolean closed = new AtomicBoolean(false);
        CloserService closerService = new CloserService().start();

        Container container = new Container(closerService, closed);

        container = null;

        System.gc();

        TimeUnit.MILLISECONDS.sleep(500);

        assertTrue(closed.get());
    }
}
