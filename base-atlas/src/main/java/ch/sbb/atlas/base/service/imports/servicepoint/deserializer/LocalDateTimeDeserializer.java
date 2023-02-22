package ch.sbb.atlas.base.service.imports.servicepoint.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
    try {
      return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException ex1) {
      log.debug("LocalDateTime could not be parsed, trying with ISO_LOCAL_DATE_TIME.", ex1);
      try {
        return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      } catch (DateTimeParseException ex2) {
        log.debug("LocalDateTime could not be parsed, trying with LocalDate.\n", ex2);
        try {
          return LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        } catch (DateTimeParseException ex3) {
          throw new IllegalArgumentException(
              jsonParser.getText() + " not valid for " + jsonParser.getCurrentName() + " lineNumber: "
                  + jsonParser.currentLocation()
                  .getLineNr(), ex3);
        }
      }
    }
  }

}

