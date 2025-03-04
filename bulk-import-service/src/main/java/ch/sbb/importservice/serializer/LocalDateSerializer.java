package ch.sbb.importservice.serializer;

import ch.sbb.atlas.api.AtlasApiConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {

  @Override
  public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeString(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH).format(localDate));
  }
}
