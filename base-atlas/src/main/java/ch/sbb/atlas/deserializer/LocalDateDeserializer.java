package ch.sbb.atlas.deserializer;

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
      try {
        return LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH));
      } catch (DateTimeParseException e2) {
        return (LocalDate) ctx.handleWeirdStringValue(LocalDate.class, jsonParser.getText(),
            "Expected Date in format " + AtlasApiConstants.DATE_FORMAT_PATTERN + " or "
                + AtlasApiConstants.DATE_FORMAT_PATTERN_CH);
      }
    }
  }

}
