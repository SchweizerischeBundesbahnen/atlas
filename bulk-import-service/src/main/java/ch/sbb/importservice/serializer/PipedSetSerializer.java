package ch.sbb.importservice.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class PipedSetSerializer<T extends Set<Enum>> extends JsonSerializer<Set<?>>{

  public static final String PIPE_DELIMITER = "|";

  @Override
  public void serialize(Set<?> objects, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    String collectEnumsToString = objects.stream().map(Object::toString).collect(Collectors.joining(PIPE_DELIMITER));
    jsonGenerator.writeString(collectEnumsToString);
  }

}
