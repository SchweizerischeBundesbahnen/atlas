package ch.sbb.line.directory.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * https://stackoverflow.com/questions/6852213/can-jackson-be-configured-to-trim-leading-trailing-whitespace-from-all-string-pr
 */
@Component
public class TrimLeadingTrailingWhitespace extends SimpleModule {

  private static final long serialVersionUID = 1L;

  @PostConstruct
  final void registerDeserializer() {
    addDeserializer(String.class, new StdScalarDeserializer<>(
        String.class) {
      @Override
      public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
          throws IOException {
        return jsonParser.getValueAsString().trim();
      }
    });
  }
}
