package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public final class FinalClass {

    @Override
    public boolean equals(Object o) {
	return o instanceof FinalClass;
    }
}
