package org.msgpack.template.builder;

import org.msgpack.template.TemplateRegistry;

public class JavassistBeansTemplateBuilder extends JavassistTemplateBuilder {

    public JavassistBeansTemplateBuilder(TemplateRegistry registry) {
	super(registry);
    }

    @Override
    public BuildContext createBuildContext() {
	return new BeansBuildContext(this);
    }
}