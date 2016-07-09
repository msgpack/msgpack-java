package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

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

  /**
   * Defines array format for serialized entities with ObjectMapper, without actually
   * including the schema
   */
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

  /**
   * Defines that unknown properties will be ignored, and won't fail the un-marshalling process.
   * Happens in case of de-serialization of a payload that contains more properties than the actual
   * value type
   */
  @Override
  public Boolean findIgnoreUnknownProperties(AnnotatedClass ac)
  {
    // If the entity contains JsonIgnoreProperties annotation, give it higher priority.
    final Boolean precedenceIgnoreUnknownProperties = super.findIgnoreUnknownProperties(ac);
    if (precedenceIgnoreUnknownProperties != null) {
      return precedenceIgnoreUnknownProperties;
    }

    return true;
  }
}
