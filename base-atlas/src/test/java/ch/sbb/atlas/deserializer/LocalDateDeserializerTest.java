package ch.sbb.atlas.deserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocalDateDeserializerTest {

  @Mock
  private JsonParser jsonParser;

  private final LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void shouldParseInternationalDateFormatCorrectly() throws IOException {
    when(jsonParser.getText()).thenReturn("2020-01-02");

    LocalDate localDate = localDateDeserializer.deserialize(jsonParser, null);
    assertThat(localDate).isEqualTo(LocalDate.of(2020, 1, 2));
  }

  @Test
  void shouldParseSwissDateFormatCorrectly() throws IOException {
    when(jsonParser.getText()).thenReturn("02.03.2020");

    LocalDate localDate = localDateDeserializer.deserialize(jsonParser, null);
    assertThat(localDate).isEqualTo(LocalDate.of(2020, 3, 2));
  }

  @Test
  void shouldThrowExceptionWhenPatternUnsupported() throws IOException {
    when(jsonParser.getText()).thenReturn("02-03-2020");

    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
        () -> localDateDeserializer.deserialize(jsonParser, null));
  }
}