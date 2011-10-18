package org.msgpack;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.annotation.Message;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: takeshita
 * Create: 11/10/17 23:17
 */
public class TestNestedList {

    MessagePack messagePack;

    @Before
    public void before(){
        messagePack = new MessagePack();
    }

    @Test
    public void testTestNestedList() throws IOException {
        NestedList obj = new NestedList();
        obj.list.add(list("aaa", "bbb"));
        obj.list.add(list(new MyClass("obj1"), new MyClass("obj2")));
        obj.list2.add((List<MyClass>)list(new MyClass("obj3")));

        byte[] bytes = messagePack.write(obj);

        // Can't unpack as NestedList
        Value unpacked = messagePack.read(bytes);
        ArrayValue root = unpacked.asArrayValue().getElementArray()[0].asArrayValue();
        ArrayValue list1 = root.getElementArray()[0].asArrayValue();
        ArrayValue list2 = root.getElementArray()[1].asArrayValue();
        ArrayValue list3 = unpacked.asArrayValue().getElementArray()[1].asArrayValue();
        list3 = list3.getElementArray()[0].asArrayValue();

        Assert.assertEquals("aaa",list1.getElementArray()[0].asRawValue().getString());
        Assert.assertEquals("bbb",list1.getElementArray()[1].asRawValue().getString());
        Assert.assertEquals("obj1",messagePack.convert(list2.getElementArray()[0],MyClass.class).name);
        Assert.assertEquals("obj2",messagePack.convert(list2.getElementArray()[1],MyClass.class).name);
        Assert.assertEquals("obj3",messagePack.convert(list3.getElementArray()[0],MyClass.class).name);

    }

    @Test
    public void testNestedListToValue() throws IOException {

        List values = list( list("hoge",4) , list(list(2,"aaa"),list("bbb")));

        Value value = messagePack.unconvert(values);

        Value[] rootArray = value.asArrayValue().getElementArray();
        Value[] list1 = rootArray[0].asArrayValue().getElementArray();
        Value[] list2 = rootArray[1].asArrayValue().getElementArray();
        Value[] list3 = list2[0].asArrayValue().getElementArray();
        Value[] list4 = list2[1].asArrayValue().getElementArray();
        Assert.assertEquals("hoge",list1[0].asRawValue().getString());
        Assert.assertEquals(4,list1[1].asIntegerValue().getInt());
        Assert.assertEquals(2,list3[0].asIntegerValue().getInt());
        Assert.assertEquals("aaa",list3[1].asRawValue().getString());
        Assert.assertEquals("bbb",list4[0].asRawValue().getString());

    }

    private List<?> list( Object ... elements){
        List<Object> list = new ArrayList();
        for(Object o : elements){
            list.add(o);
        }
        return list;
    }

    @Message
    public static class NestedList{
        public List<List> list = new ArrayList<List>();

        public List<List<MyClass>> list2 = new ArrayList<List<MyClass>>();

    }

    @Message
    public static class MyClass{
        String name;

        public MyClass(){}
        public MyClass(String n ){ name = n;}
    }

}
