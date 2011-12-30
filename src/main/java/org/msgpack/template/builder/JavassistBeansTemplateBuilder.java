package org.msgpack.template.builder;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.msgpack.template.TemplateRegistry;

@SuppressWarnings({ "rawtypes" })
public class JavassistBeansTemplateBuilder extends JavassistTemplateBuilder {
    private static final Logger LOG = Logger.getLogger(JavassistBeansTemplateBuilder.class.getName());

    public JavassistBeansTemplateBuilder(TemplateRegistry registry) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && LOG.isLoggable(Level.FINE)) {
            LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    protected BuildContext createBuildContext() {
        return new BeansBuildContext(this);
    }
}