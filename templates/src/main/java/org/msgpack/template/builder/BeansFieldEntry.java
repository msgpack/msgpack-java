package org.msgpack.template.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.msgpack.MessageTypeException;
import org.msgpack.template.FieldOption;
import org.msgpack.template.builder.beans.PropertyDescriptor;

public class BeansFieldEntry extends FieldEntry {

    protected PropertyDescriptor desc;

    public BeansFieldEntry() {
        super();
    }

    public BeansFieldEntry(final BeansFieldEntry e) {
        super(e.option);
        this.desc = e.getPropertyDescriptor();
    }

    public BeansFieldEntry(final PropertyDescriptor desc) {
        this(desc, FieldOption.DEFAULT);
    }

    public BeansFieldEntry(final PropertyDescriptor desc, final FieldOption option) {
        super(option);
        this.desc = desc;
    }

    public String getGetterName() {
        return getPropertyDescriptor().getReadMethod().getName();
    }

    public String getSetterName() {
        return getPropertyDescriptor().getWriteMethod().getName();
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return desc;
    }

    @Override
    public String getName() {
        return getPropertyDescriptor().getDisplayName();
    }

    @Override
    public Class<?> getType() {
        return getPropertyDescriptor().getPropertyType();
    }

    @Override
    public Type getGenericType() {
        return getPropertyDescriptor().getReadMethod().getGenericReturnType();
    }

    @Override
    public Object get(Object target) {
        try {
            return getPropertyDescriptor().getReadMethod().invoke(target);
        } catch (IllegalArgumentException e) {
            throw new MessageTypeException(e);
        } catch (IllegalAccessException e) {
            throw new MessageTypeException(e);
        } catch (InvocationTargetException e) {
            throw new MessageTypeException(e);
        }
    }

    @Override
    public void set(Object target, Object value) {
        try {
            getPropertyDescriptor().getWriteMethod().invoke(target, value);
        } catch (IllegalArgumentException e) {
            throw new MessageTypeException(e);
        } catch (IllegalAccessException e) {
            throw new MessageTypeException(e);
        } catch (InvocationTargetException e) {
            throw new MessageTypeException(e);
        }
    }
}