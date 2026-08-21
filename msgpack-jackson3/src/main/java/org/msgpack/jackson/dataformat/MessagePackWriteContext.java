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
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.json.DupDetector;

class MessagePackWriteContext extends TokenStreamContext
{
    private final MessagePackWriteContext parent;
    private MessagePackWriteContext childToRecycle;
    private DupDetector dups;
    private String currentName;
    private Object currentValue;
    // For TYPE_OBJECT: true after writeName (expecting value), false after writeValue (expecting name)
    private boolean gotName;

    private MessagePackWriteContext(int type, MessagePackWriteContext parent, DupDetector dups)
    {
        super(type, -1);
        this.parent = parent;
        this.dups = dups;
    }

    private MessagePackWriteContext reset(int type, Object value)
    {
        _type = type;
        _index = -1;
        gotName = false;
        currentName = null;
        currentValue = value;
        if (dups != null) {
            dups.reset();
        }
        return this;
    }

    static MessagePackWriteContext createRootContext(DupDetector dups)
    {
        return new MessagePackWriteContext(TYPE_ROOT, null, dups);
    }

    MessagePackWriteContext createChildArrayContext(Object value)
    {
        MessagePackWriteContext ctx = childToRecycle;
        if (ctx == null) {
            ctx = new MessagePackWriteContext(TYPE_ARRAY, this, dups == null ? null : dups.child());
            childToRecycle = ctx;
        }
        return ctx.reset(TYPE_ARRAY, value);
    }

    MessagePackWriteContext createChildObjectContext(Object value)
    {
        MessagePackWriteContext ctx = childToRecycle;
        if (ctx == null) {
            ctx = new MessagePackWriteContext(TYPE_OBJECT, this, dups == null ? null : dups.child());
            childToRecycle = ctx;
        }
        return ctx.reset(TYPE_OBJECT, value);
    }

    @Override
    public MessagePackWriteContext getParent()
    {
        return parent;
    }

    boolean isExpectingValue()
    {
        return _type == TYPE_OBJECT && gotName;
    }

    boolean writeValue()
    {
        if (_type == TYPE_OBJECT) {
            if (!gotName) {
                return false;
            }
            gotName = false;
        }
        ++_index;
        return true;
    }

    boolean writeName(String name) throws StreamWriteException
    {
        if (_type != TYPE_OBJECT || gotName) {
            return false;
        }
        currentName = name;
        gotName = true;
        if (dups != null) {
            _checkDup(name);
        }
        return true;
    }

    private void _checkDup(String name) throws StreamWriteException
    {
        // Null names (nil map keys) cannot be duplicate-checked via DupDetector
        // because DupDetector.isDup(null) NPEs when a prior non-null name exists.
        if (name != null && dups.isDup(name)) {
            throw new StreamWriteException(null, "Duplicate Object property \"" + name + "\"");
        }
    }

    @Override
    public String currentName()
    {
        return currentName;
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
}
