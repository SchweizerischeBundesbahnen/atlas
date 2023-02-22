package ch.sbb.atlas.imports.servicepoint.deserializer;

import ch.sbb.atlas.api.AtlasApiConstants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

  @Override
  public LocalDate deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
    try {
      return LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(jsonParser.getText(), e);
    }
  }

}
