package org.msgpack.jackson.dataformat;

import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import com.fasterxml.jackson.annotation.JsonFormat;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.ARRAY;

/**
 * Provides the ability of serializing POJOs without their schema.
 * Similar to @JsonFormat annotation with JsonFormat.Shape.ARRAY, but in a programmatic
 * way.
 *
 * This also provides same behavior as msgpack-java 0.6.x serialization api.
 */
public class JsonArrayFormat extends JacksonAnnotationIntrospector
{
    private static final JsonFormat.Value ARRAY_FORMAT = new JsonFormat.Value().withShape(ARRAY);

    @Override
    public JsonFormat.Value findFormat(MapperConfig<?> config, Annotated ann)
    {
        JsonFormat.Value precedenceFormat = super.findFormat(config, ann);
        if (precedenceFormat != null) {
            return precedenceFormat;
        }

        return ARRAY_FORMAT;
    }
}
