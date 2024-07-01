package ch.sbb.workflow.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void shouldRedactString(){
    //given
    String string = "asd@bc.ch";
    //when
    String result = StringHelper.redactString(string);
    //then
    assertThat(result).isNotNull().isEqualTo("a*****");
  }

  @Test
  void shouldRedactSingleCharString(){
    //given
    String string = "l";
    //when
    String result = StringHelper.redactString(string);
    //then
    assertThat(result).isNotNull().isEqualTo("l*****");
  }

  @Test
  void shouldNotRedactStringWhenStringIsNull(){
    //given
    //when
    String result = StringHelper.redactString(null);
    //then
    assertThat(result).isNull();
  }

}