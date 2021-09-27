package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.model.CymkColor;
import org.junit.jupiter.api.Test;

class CymkColorConverterTest {

  private static final CymkColorConverter CYMK_COLOR_CONVERTER = new CymkColorConverter();
  private static final CymkColor COLOR = new CymkColor(10, 11, 12, 13);
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
  public void shouldConvertFromDbRepresentation() {
    // given

    // when
    CymkColor cymkColor = CYMK_COLOR_CONVERTER.convertToEntityAttribute(COLOR_DB_REPRESENTATION);
    // then
    assertThat(cymkColor).usingRecursiveComparison().isEqualTo(COLOR);
  }

}