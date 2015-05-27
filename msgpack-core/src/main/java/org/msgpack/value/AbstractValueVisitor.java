package org.msgpack.value;

/**
 * Empty visitor that does nothing
 */
public class AbstractValueVisitor implements ValueVisitor {

    @Override
    public void visitNil() {

    }
    @Override
    public void visitBoolean(boolean v) {

    }
    @Override
    public void visitInteger(IntegerValue v) {

    }
    @Override
    public void visitFloat(FloatValue v) {

    }
    @Override
    public void visitBinary(BinaryValue v) {

    }
    @Override
    public void visitString(StringValue v) {

    }
    @Override
    public void visitArray(ArrayValue v) {

    }
    @Override
    public void visitMap(MapValue v) {

    }
    @Override
    public void visitExtended(ExtendedValue v) {

    }
    @Override
    public void onError(Exception e) {

    }
}
