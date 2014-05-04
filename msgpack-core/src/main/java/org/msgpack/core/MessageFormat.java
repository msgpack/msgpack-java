package org.msgpack.core;

/**
 * Created on 2014/05/04.
 */
public enum MessageFormat {

    INT64(ValueType.INTEGER),
    INT32(ValueType.INTEGER),
    UINT64(ValueType.INTEGER),
    UINT32(ValueType.INTEGER)
    // TODO
    ;

    private final ValueType type;

    private MessageFormat(ValueType type) {
        this.type = type;
    }

    public ValueType getType() {
        return type;
    }

}
