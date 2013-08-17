package org.msgpack.template;

import java.io.IOException;
import java.lang.reflect.Array;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectArrayTemplate extends AbstractTemplate {
    protected Class componentClass;

    protected Template componentTemplate;

    public ObjectArrayTemplate(Class componentClass, Template componentTemplate) {
        this.componentClass = componentClass;
        this.componentTemplate = componentTemplate;
    }

    @Override
    public void write(Packer packer, Object v, boolean required)
            throws IOException {
        if (v == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            packer.writeNil();
            return;
        }
        if (!(v instanceof Object[]) ||
                !componentClass.isAssignableFrom(v.getClass().getComponentType())) {
            throw new MessageTypeException();
        }

        Object[] array = (Object[]) v;
        int length = array.length;
        packer.writeArrayBegin(length);
        for (int i = 0; i < length; i++) {
            componentTemplate.write(packer, array[i], required);
        }
        packer.writeArrayEnd();
    }

    @Override
    public Object read(Unpacker unpacker, Object to, boolean required)
            throws IOException {
        if (!required && unpacker.trySkipNil()) {
            return null;
        }

        int length = unpacker.readArrayBegin();
        Object[] array = (Object[]) Array.newInstance(componentClass, length);
        for (int i = 0; i < length; i++) {
            array[i] = componentTemplate.read(unpacker, null, required);
        }
        unpacker.readArrayEnd();
        return array;
    }
}
