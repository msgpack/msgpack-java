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

import java.io.Closeable;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

class CloserService
{
    private final Set<CloseableReference> references = new ConcurrentSkipListSet<CloseableReference>();
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();

    void addFinalizer(Object referent, Closeable closeable)
    {
        references.add(new CloseableReference(referent, referenceQueue, closeable));
    }

    CloserService start()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                processReferenceQueue();
            }
        });
        thread.setName("CloserService");
        thread.setDaemon(true);
        thread.start();

        return this;
    }

    private void processReferenceQueue()
    {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                CloseableReference reference = (CloseableReference) referenceQueue.remove();
                references.remove(reference);
                reference.closeble.close();
            }
            catch (InterruptedException e) {
                return;
            }
            catch (Throwable e) {
                // Not use a logger to avoid adding dependencies
                e.printStackTrace();
            }
        }
    }

    private static class CloseableReference
            extends PhantomReference<Object>
            implements Comparable
    {
        private final Closeable closeble;

        CloseableReference(Object referent, ReferenceQueue<Object> queue, Closeable closeable)
        {
            super(referent, queue);
            this.closeble = closeable;
        }

        @Override
        public int compareTo(Object o)
        {
            return hashCode() - o.hashCode();
        }
    }
}
