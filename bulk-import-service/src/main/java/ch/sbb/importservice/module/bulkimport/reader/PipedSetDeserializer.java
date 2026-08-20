package ch.sbb.importservice.module.bulkimport.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PipedSetDeserializer extends JsonDeserializer<Set<?>> implements ContextualDeserializer {

  private static final String PIPE_SEPARATOR = "\\|";

  private Class<?> contentType;

  @Override
  public Set<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
    String[] values = jsonParser.getText().split(PIPE_SEPARATOR);

    if (contentType != null && contentType.isEnum()) {
      return Arrays.stream(values).map(value -> toEnum(value, contentType, ctx)).collect(Collectors.toSet());
    }
    return Arrays.stream(values).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static Object toEnum(String value, Class<?> enumType, DeserializationContext ctx) {
    for (Object enumConstant : enumType.getEnumConstants()) {
      if (((Enum<?>) enumConstant).name().equals(value)) {
        return enumConstant;
      }
    }
    try {
      return ctx.handleWeirdStringValue(enumType, value, value + " is not a valid" + enumType.getName());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    PipedSetDeserializer deserializer = new PipedSetDeserializer();
    deserializer.contentType = property.getType().getContentType().getRawClass();
    return deserializer;
  }

  public static SimpleModule module() {
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Set.class, new PipedSetDeserializer());
    return module;
  }

}
