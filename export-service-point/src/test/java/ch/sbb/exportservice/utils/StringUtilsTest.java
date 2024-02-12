package ch.sbb.exportservice.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

}