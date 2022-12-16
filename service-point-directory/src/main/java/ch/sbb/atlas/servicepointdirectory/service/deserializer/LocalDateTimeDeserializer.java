package ch.sbb.atlas.servicepointdirectory.service.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
    try {
      return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException e) {
      try {
        return LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
      } catch (DateTimeParseException exception) {
        throw new IllegalArgumentException(
            jsonParser.getText() + " not valid for " + jsonParser.getCurrentName() + " lineNumber:" + jsonParser.currentLocation()
                .getLineNr(), exception);
      }
    }
  }

}
