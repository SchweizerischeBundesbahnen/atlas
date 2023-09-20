package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.model.CmykColor;
import org.junit.jupiter.api.Test;

class CmykColorConverterTest {

  private static final CmykColorConverter CYMK_COLOR_CONVERTER = new CmykColorConverter();
  private static final CmykColor COLOR = new CmykColor(10, 11, 12, 13);
   static final String COLOR_DB_REPRESENTATION = "10,11,12,13";

  @Test
   void shouldConvertToDbRepresentation() {
    // given

    // when
    String databaseValue = CYMK_COLOR_CONVERTER.convertToDatabaseColumn(COLOR);
    // then
    assertThat(databaseValue).isEqualTo(COLOR_DB_REPRESENTATION);
  }

  @Test
   void shouldConvertNullToDbRepresentation() {
    // given

    // when
    String databaseValue = CYMK_COLOR_CONVERTER.convertToDatabaseColumn(null);
    // then
    assertThat(databaseValue).isNull();
  }

  @Test
   void shouldConvertFromDbRepresentation() {
    // given

    // when
    CmykColor cmykColor = CYMK_COLOR_CONVERTER.convertToEntityAttribute(COLOR_DB_REPRESENTATION);
    // then
    assertThat(cmykColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

  @Test
   void shouldConvertNullFromDbRepresentation() {
    // given

    // when
    CmykColor cmykColor = CYMK_COLOR_CONVERTER.convertToEntityAttribute(null);
    // then
    assertThat(cmykColor).isNull();
  }

  @Test
   void shouldConvertFromJsonRepresentation() {
    // given

    // when
    CmykColor cmykColor = CmykColorConverter.fromCmykString(COLOR_DB_REPRESENTATION);
    // then
    assertThat(cmykColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

  @Test
   void shouldConvertToJsonRepresentation() {
    // given

    // when
    String cmykColorString = CmykColorConverter.toCmykString(COLOR);
    // then
    assertThat(cmykColorString).isEqualTo(COLOR_DB_REPRESENTATION);
  }
}