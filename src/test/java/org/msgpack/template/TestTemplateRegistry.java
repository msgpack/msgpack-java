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

    // Iface2 < Iface1
    static interface Iface0 {}
    static interface Iface1 extends Iface0 {}
    static interface Iface2 extends Iface1 {}

    static class ImplementsIface0 implements Iface0 {}
    static class ImplementsIface1 implements Iface1 {}
    static class ImplementsIface2 implements Iface2 {}

    static class Iface0Template extends UnsupportedOperationTemplate<Iface0> {}
    static class Iface1Template extends UnsupportedOperationTemplate<Iface1> {}
    static class Iface2Template extends UnsupportedOperationTemplate<Iface2> {}

    @SuppressWarnings("unchecked")
    @Test
    public void testCanLookupByInheritanceHierarchy() throws Exception {
        TemplateRegistry registry = new TemplateRegistry(null);
        registry.register(Iface0.class, new Iface0Template());

        // Iface0 == Iface0
        Template<Iface0> template = registry.lookup(Iface0.class);
        assertThat(template, is(instanceOf(Iface0Template.class)));

        // ImplementsIface2 (< Iface2 < Iface1) < Iface0
        template = registry.lookup(ImplementsIface2.class);
        assertThat(template, is(instanceOf(Iface0Template.class)));

        // ImplementsIface1 (< Iface1) < Iface0
        template = registry.lookup(ImplementsIface1.class);
        assertThat(template, is(instanceOf(Iface0Template.class)));

        // ImplementsIface0 < Iface0
        template = registry.lookup(ImplementsIface0.class);
        assertThat(template, is(instanceOf(Iface0Template.class)));

        // register specialized (override) templates and lookup again
        registry.register(ImplementsIface1.class, new Iface1Template());
        registry.register(ImplementsIface2.class, new Iface2Template());

        template = registry.lookup(ImplementsIface2.class);
        assertThat(template, is(instanceOf(Iface2Template.class)));

        template = registry.lookup(ImplementsIface1.class);
        assertThat(template, is(instanceOf(Iface1Template.class)));

        template = registry.lookup(ImplementsIface0.class);
        assertThat(template, is(instanceOf(Iface0Template.class)));
    }
}
