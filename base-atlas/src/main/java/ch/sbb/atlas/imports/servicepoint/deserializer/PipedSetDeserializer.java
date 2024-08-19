package ch.sbb.atlas.imports.servicepoint.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PipedSetDeserializer<T extends Enum<T>> extends JsonDeserializer<Set<T>> implements ContextualDeserializer {

  private static final String PIPE_SEPARATOR = "\\|";

  private Class<T> type;

  @Override
  public Set<T> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {

    return Arrays.stream(jsonParser.getText().split(PIPE_SEPARATOR)).map(i -> {
      return T.valueOf(type, i);
    }).collect(
        Collectors.toSet());
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    PipedSetDeserializer<T> deserializer = new PipedSetDeserializer<>();
    deserializer.type = (Class<T>) property.getType().getContentType().getRawClass();
    return deserializer;
  }

}
