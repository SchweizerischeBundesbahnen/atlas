package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.model.CmykColor;
import org.junit.jupiter.api.Test;

class CmykColorConverterTest {

  private static final CmykColorConverter CYMK_COLOR_CONVERTER = new CmykColorConverter();
  private static final CmykColor COLOR = new CmykColor(10, 11, 12, 13);
  public static final String COLOR_DB_REPRESENTATION = "10,11,12,13";

  @Test
  public void shouldConvertToDbRepresentation() {
    // given

    // when
    String databaseValue = CYMK_COLOR_CONVERTER.convertToDatabaseColumn(COLOR);
    // then
    assertThat(databaseValue).isEqualTo(COLOR_DB_REPRESENTATION);
  }

  @Test
  public void shouldConvertNullToDbRepresentation() {
    // given

    // when
    String databaseValue = CYMK_COLOR_CONVERTER.convertToDatabaseColumn(null);
    // then
    assertThat(databaseValue).isNull();
  }

  @Test
  public void shouldConvertFromDbRepresentation() {
    // given

    // when
    CmykColor cmykColor = CYMK_COLOR_CONVERTER.convertToEntityAttribute(COLOR_DB_REPRESENTATION);
    // then
    assertThat(cmykColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

  @Test
  public void shouldConvertNullFromDbRepresentation() {
    // given

    // when
    CmykColor cmykColor = CYMK_COLOR_CONVERTER.convertToEntityAttribute(null);
    // then
    assertThat(cmykColor).isNull();
  }

  @Test
  public void shouldConvertFromJsonRepresentation() {
    // given

    // when
    CmykColor cmykColor = CmykColorConverter.fromCmykString(COLOR_DB_REPRESENTATION);
    // then
    assertThat(cmykColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

  @Test
  public void shouldConvertToJsonRepresentation() {
    // given

    // when
    String cmykColorString = CmykColorConverter.toCmykString(COLOR);
    // then
    assertThat(cmykColorString).isEqualTo(COLOR_DB_REPRESENTATION);
  }
}