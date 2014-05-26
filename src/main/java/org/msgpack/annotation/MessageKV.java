package org.msgpack.annotation;

import org.msgpack.template.FieldOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ray_ni
 * this annotation is used for serialized by key and value
 * on 2014/5/24 0024.下午 4:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageKV {
    FieldOption value() default FieldOption.KEY_VALUE;
}
