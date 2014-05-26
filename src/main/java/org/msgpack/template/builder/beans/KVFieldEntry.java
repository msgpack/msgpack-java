package org.msgpack.template.builder.beans;

import org.msgpack.template.FieldOption;
import org.msgpack.template.builder.DefaultFieldEntry;

import java.lang.reflect.Field;

/**
 * Created by ray_ni
 * on 2014/5/24 0024.下午 4:44
 * <p/>
 * add key on DefaultFieldEntry
 */
public class KVFieldEntry extends DefaultFieldEntry {

    private String key;

    public KVFieldEntry(Field field, FieldOption fieldOption, String key) {
        super(field, fieldOption);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean isOptional() {
        if (option == FieldOption.NOT_NULL_KEY || option == FieldOption.NOTNULLABLE) {
            return false;
        } else {
            return true;
        }
    }
}
