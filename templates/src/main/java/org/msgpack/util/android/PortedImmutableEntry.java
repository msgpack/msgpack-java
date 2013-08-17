/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.msgpack.util.android;

import java.io.Serializable;
import java.util.Map;

/**
 * An immutable key-value mapping. Despite the name, this class is non-final
 * and its subclasses may be mutable.
 * 
 * This class is ported from java.util.AbstractMap$SimpleImmutableEntry of
 * https://github.com/OESF/OHA-Android-4.0.3_r1.0 (Apache License).
 */
public class PortedImmutableEntry<K, V> implements Map.Entry<K, V>, Serializable {
    private static final long serialVersionUID = -4564047655287765373L;

    private final K key;
    private final V value;

    public PortedImmutableEntry(K theKey, V theValue) {
        key = theKey;
        value = theValue;
    }

    /**
     * Constructs an instance with the key and value of {@code copyFrom}.
     */
    public PortedImmutableEntry(Map.Entry<? extends K, ? extends V> copyFrom) {
        key = copyFrom.getKey();
        value = copyFrom.getValue();
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    /**
     * This base implementation throws {@code UnsupportedOperationException}
     * always.
     */
    public V setValue(V object) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
            return (key == null ? entry.getKey() == null : key.equals(entry
                    .getKey()))
                    && (value == null ? entry.getValue() == null : value
                            .equals(entry.getValue()));
        }
        return false;
    }

    @Override public int hashCode() {
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }

    @Override public String toString() {
        return key + "=" + value;
    }
}
