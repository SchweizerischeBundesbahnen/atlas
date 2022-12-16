package ch.sbb.atlas.servicepointdirectory.service.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class NumericBooleanDeserializer extends JsonDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
    if ("1".equals(p.getText())) {
      return Boolean.TRUE;
    }
    if ("0".equals(p.getText())) {
      return Boolean.FALSE;
    }
    throw new IllegalArgumentException(p.getText());
  }

}
