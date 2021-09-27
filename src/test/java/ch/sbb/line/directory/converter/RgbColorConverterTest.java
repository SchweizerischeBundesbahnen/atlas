package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.model.RgbColor;
import org.junit.jupiter.api.Test;

class RgbColorConverterTest {

  private static final RgbColorConverter RGB_COLOR_CONVERTER = new RgbColorConverter();
  private static final RgbColor COLOR = new RgbColor(10, 11, 12);
  public static final String COLOR_DB_REPRESENTATION = "#0A0B0C";

  @Test
  public void shouldConvertToDbRepresentation() {
    // given

    // when
    String databaseValue = RGB_COLOR_CONVERTER.convertToDatabaseColumn(COLOR);
    // then
    assertThat(databaseValue).isEqualTo(COLOR_DB_REPRESENTATION);
  }

  @Test
  public void shouldConvertFromDbRepresentation() {
    // given

    // when
    RgbColor rgbColor = RGB_COLOR_CONVERTER.convertToEntityAttribute(COLOR_DB_REPRESENTATION);
    // then
    assertThat(rgbColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

}