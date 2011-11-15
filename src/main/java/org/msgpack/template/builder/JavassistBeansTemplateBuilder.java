package org.msgpack.template.builder;

import java.lang.reflect.Type;

import org.msgpack.template.TemplateRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "rawtypes" })
public class JavassistBeansTemplateBuilder extends JavassistTemplateBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(JavassistBeansTemplateBuilder.class);

    public JavassistBeansTemplateBuilder(TemplateRegistry registry) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched) {
            LOG.debug("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    protected BuildContext createBuildContext() {
        return new BeansBuildContext(this);
    }
}