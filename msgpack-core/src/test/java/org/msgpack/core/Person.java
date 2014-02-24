package org.msgpack.core;

import org.msgpack.annotation.Message;

public class Person {
    public int id;
    public String name;

    public Person() {}


    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
