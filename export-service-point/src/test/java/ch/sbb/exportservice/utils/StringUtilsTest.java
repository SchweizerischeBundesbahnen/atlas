package ch.sbb.exportservice.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void shouldReplaceSemiColonWithColon(){
    //when
    String result = StringUtils.replaceSemiColonWithColon("123;123");
    //then
    assertThat(result).isEqualTo("123:123");
  }

  @Test
  void shouldNotReplaceWhenValueIsNUll(){
    //when
    String result = StringUtils.replaceSemiColonWithColon(null);
    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldRemoveNewline() {
    String text = """
        Multi
        Line
        Text""";
    String result = StringUtils.removeNewLine(text);
    Assertions.assertThat(result).isEqualTo("Multi Line Text");
  }

  @Test
  void shouldDoNothingOnNull() {
    assertThat(StringUtils.removeNewLine(null)).isNull();
  }

}