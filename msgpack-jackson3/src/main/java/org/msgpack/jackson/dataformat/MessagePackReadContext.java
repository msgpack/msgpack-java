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

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.json.DupDetector;

/**
 * Replacement of {@link tools.jackson.core.json.JsonReadContext}
 * to support features needed by MessagePack format.
 */
public final class MessagePackReadContext
    extends TokenStreamContext
{
    protected final MessagePackReadContext parent;

    protected final DupDetector dups;

    /**
     * For fixed-size Arrays, Objects, this indicates expected number of entries.
     */
    protected int expEntryCount;

    protected String currentName;

    protected Object currentValue;

    protected MessagePackReadContext child = null;

    public MessagePackReadContext(MessagePackReadContext parent, DupDetector dups,
                                  int type, int expEntryCount)
    {
        super();
        this.parent = parent;
        this.dups = dups;
        _type = type;
        this.expEntryCount = expEntryCount;
        _index = -1;
        _nestingDepth = parent == null ? 0 : parent._nestingDepth + 1;
    }

    protected void reset(int type, int expEntryCount)
    {
        _type = type;
        this.expEntryCount = expEntryCount;
        _index = -1;
        currentName = null;
        currentValue = null;
        if (dups != null) {
            dups.reset();
        }
    }

    @Override
    public Object currentValue()
    {
        return currentValue;
    }

    @Override
    public void assignCurrentValue(Object v)
    {
        currentValue = v;
    }

    public static MessagePackReadContext createRootContext(DupDetector dups)
    {
        return new MessagePackReadContext(null, dups, TYPE_ROOT, -1);
    }

    public MessagePackReadContext createChildArrayContext(int expEntryCount)
    {
        MessagePackReadContext ctxt = child;
        if (ctxt == null) {
            ctxt = new MessagePackReadContext(this,
                    (dups == null) ? null : dups.child(),
                            TYPE_ARRAY, expEntryCount);
            child = ctxt;
        }
        else {
            ctxt.reset(TYPE_ARRAY, expEntryCount);
        }
        return ctxt;
    }

    public MessagePackReadContext createChildObjectContext(int expEntryCount)
    {
        MessagePackReadContext ctxt = child;
        if (ctxt == null) {
            ctxt = new MessagePackReadContext(this,
                    (dups == null) ? null : dups.child(),
                    TYPE_OBJECT, expEntryCount);
            child = ctxt;
            return ctxt;
        }
        ctxt.reset(TYPE_OBJECT, expEntryCount);
        return ctxt;
    }

    @Override
    public String currentName()
    {
        return currentName;
    }

    @Override
    public MessagePackReadContext getParent()
    {
        return parent;
    }

    public boolean hasExpectedLength()
    {
        return (expEntryCount >= 0);
    }

    public int getExpectedLength()
    {
        return expEntryCount;
    }

    public boolean isEmpty()
    {
        return expEntryCount == 0;
    }

    public int getRemainingExpectedLength()
    {
        int diff = expEntryCount - _index;
        return Math.max(0, diff);
    }

    public boolean acceptsBreakMarker()
    {
        return (expEntryCount < 0) && _type != TYPE_ROOT;
    }

    public boolean expectMoreValues()
    {
        if (++_index == expEntryCount) {
            return false;
        }
        return true;
    }

    public TokenStreamLocation startLocation(ContentReference srcRef)
    {
        return new TokenStreamLocation(srcRef, 1L, -1, -1);
    }

    public void setCurrentName(String name)
    {
        currentName = name;
        if (dups != null) {
            _checkDup(dups, name);
        }
    }

    private void _checkDup(DupDetector dd, String name)
    {
        // Null names (nil map keys) cannot be duplicate-checked via DupDetector
        // because DupDetector.isDup(null) NPEs when a prior non-null name exists.
        if (name != null && dd.isDup(name)) {
            throw new StreamReadException(null,
                    "Duplicate field '" + name + "'", dd.findLocation());
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(64);
        switch (_type) {
        case TYPE_ROOT:
            sb.append("/");
            break;
        case TYPE_ARRAY:
            sb.append('[');
            sb.append(getCurrentIndex());
            sb.append(']');
            break;
        case TYPE_OBJECT:
            sb.append('{');
            if (currentName != null) {
                sb.append('"');
                sb.append(currentName.replace("\\", "\\\\").replace("\"", "\\\""));
                sb.append('"');
            }
            else {
                sb.append('?');
            }
            sb.append('}');
            break;
        }
        return sb.toString();
    }
}
