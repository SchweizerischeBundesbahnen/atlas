package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CsvProcessorUtilTest {

  @Test
  void shouldRemoveNewline() {
    String text = """
        Multi
        Line
        Text""";
    String result = CsvProcessorUtil.removeNewLine(text);
    assertThat(result).isEqualTo("Multi Line Text");
  }

  @Test
  void shouldDoNothingOnNull() {
    assertThat(CsvProcessorUtil.removeNewLine(null)).isNull();
  }
}