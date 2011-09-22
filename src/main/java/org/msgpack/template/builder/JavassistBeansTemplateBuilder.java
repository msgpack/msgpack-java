package org.msgpack.template.builder;

import org.msgpack.template.TemplateRegistry;


@SuppressWarnings({ "rawtypes" })
public class JavassistBeansTemplateBuilder extends JavassistTemplateBuilder {

    public JavassistBeansTemplateBuilder(TemplateRegistry registry) {
	super(registry);
    }

    @Override
    protected BuildContext createBuildContext() {
	return new BeansBuildContext(this);
    }
}