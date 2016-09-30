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
package org.msgpack.core.buffer;

import java.io.IOException;
import java.util.Enumeration;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link MessageBufferInput} Enumeration
 */
public class SequenceMessageBufferInput
        implements MessageBufferInput
{
    private Enumeration<? extends MessageBufferInput> sequence;
    private MessageBufferInput input;

    public SequenceMessageBufferInput(Enumeration<? extends MessageBufferInput> sequence)
    {
        this.sequence = checkNotNull(sequence, "input sequence is null");
        try {
            nextInput();
        }
        catch (IOException ignore) {
        }
    }

    @Override
    public MessageBuffer next() throws IOException
    {
        if (input == null) {
            return null;
        }
        MessageBuffer buffer = input.next();
        if (buffer == null) {
            nextInput();
            return next();
        }

        return buffer;
    }

    private void nextInput() throws IOException
    {
        if (input != null) {
            input.close();
        }

        if (sequence.hasMoreElements()) {
            input = sequence.nextElement();
            if (input == null) {
                throw new NullPointerException("An element in the MessageBufferInput sequence is null");
            }
        }
        else {
            input = null;
        }
    }

    @Override
    public void close() throws IOException
    {
        do {
            nextInput();
        } while (input != null);
    }
}
