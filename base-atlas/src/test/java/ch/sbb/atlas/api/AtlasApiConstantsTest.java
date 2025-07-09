package ch.sbb.atlas.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AtlasApiConstantsTest {

  @ParameterizedTest
  @ValueSource(strings = {"2025-07-08T14:34:44.123",
      "2025-07-08T14:34:44.123456",
      "2025-07-08T14:34:44.123456789",
      "2025-07-08T14:34:44.123Z",
      "2025-07-08T14:34:44.123456Z",
      "2025-07-08T14:34:44.123456789Z",})
  void shouldConvertStringToLocalDateTimeWithDateTimeParsePattern(String value) {
    DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofPattern(AtlasApiConstants.ISO_DATE_TIME_PARSE_PATTERN).withResolverStyle(ResolverStyle.STRICT);

    LocalDateTime localDateTime = LocalDateTime.parse(value, dateTimeFormatter);
    assertThat(localDateTime).isNotNull();
  }

}