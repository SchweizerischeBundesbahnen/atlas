package ch.sbb.atlas.imports.servicepoint.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumericBooleanDeserializer extends JsonDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
    try {
      return p.getBooleanValue();
    } catch (JsonParseException e) {
      log.debug("Could not parse boolean, try to parse numeric values 0 and 1.");
      if ("1".equals(p.getText())) {
        return Boolean.TRUE;
      }
      if ("0".equals(p.getText())) {
        return Boolean.FALSE;
      }
      throw new IllegalArgumentException(
          "Could not parse Boolean value: " + p.getText() + " on line: " + p.getCurrentLocation().getLineNr());
    }
  }

}