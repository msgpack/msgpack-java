package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.ARRAY;

/**
 * Provides the ability of serializing POJOs without their schema.
 * Similar to @JsonFormat annotation with JsonFormat.Shape.ARRAY, but in a programmatic
 * way.
 *
 * This also provides same behavior as msgpack-java 0.6.x serialization api.
 */
public class JsonArrayFormat extends JacksonAnnotationIntrospector {

  private final static JsonFormat.Value ARRAY_FORMAT = new JsonFormat.Value().withShape(ARRAY);

  @Override
  public JsonFormat.Value findFormat(Annotated ann)
  {
    // If the entity contains JsonFormat annotation, give it higher priority.
    JsonFormat.Value precedenceFormat = super.findFormat(ann);
    if (precedenceFormat != null) {
      return precedenceFormat;
    }

    return ARRAY_FORMAT;
  }
}
