//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.msgpack.MessageTypeException;
import org.msgpack.template.OrdinalEnumTemplate;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrdinalEnum {

	/**
	 * Specify whether the ordinal index lookup should be handled strictly or
	 * not when mapping ordinal value to an enum value. By specifying true,
	 * {@link MessageTypeException} will be thrown if the enum specified by the
	 * ordinal value does not exist in this implementation. If false, then the
	 * missing ordinal value treated as null, gracefully handling the lookup.
	 * Default is true.
	 * 
	 * @since 0.6.8
	 * @see OrdinalEnumTemplate
	 */
	boolean strict() default true;

}
