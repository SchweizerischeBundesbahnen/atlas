package ch.sbb.line.directory.converter;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * https://stackoverflow.com/questions/6852213/can-jackson-be-configured-to-trim-leading-trailing-whitespace-from-all-string-pr
 */
@Component
public class TrimLeadingTrailingWhitespace extends SimpleModule {

  private final StdScalarDeserializer<String> jsonStringDeserializer = new StdScalarDeserializer<>(
      String.class) {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
        throws IOException {
      return jsonParser.getValueAsString().trim();
    }
  };

  TrimLeadingTrailingWhitespace() {
    addDeserializer(String.class, jsonStringDeserializer);
  }

}
