package org.msgpack.template;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestTemplateRegistry {

    private static final TemplateRegistry root = new TemplateRegistry(null);

    static class MyArrayList<E> extends ArrayList<E> {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testTraverseInterface() throws Exception {
        Template<?> template = root.lookup(List.class);
        assertThat(template, is(instanceOf(ListTemplate.class)));
    }

    @Test
    public void testTraverseDescentInterface() throws Exception {
        Template<?> template = root.lookup(MyArrayList.class);
        assertThat(template, is(instanceOf(ListTemplate.class)));
    }

    @Test
    public void testArraysAsListIsPackable() throws Exception {
        Template<?> template = root.lookup(Arrays.asList().getClass());
        assertThat(template, is(instanceOf(ListTemplate.class)));
    }

}
