package ch.sbb.atlas.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class StringTrimmerDeserializer extends JsonDeserializer<String> {

  @Override
  public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    String result = jsonParser.getValueAsString();
    return (result == null || result.trim().isEmpty()) ? null : result;
  }

}
