package org.msgpack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ray_ni
 * on 2014/5/24 0024.下午 5:33
 *
 * if your want to specify the key for field, and the filed can not be null,
 * please use this annotation
 * <p/>
 * serialized with key and value, can not be null
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullKey {
    String value();
}
