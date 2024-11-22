package ch.sbb.atlas.redact;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.redact.StringRedactor;
import org.junit.jupiter.api.Test;

class StringRedactorTest {

  @Test
  void shouldRedactString(){
    //given
    String string = "asd@bc.ch";
    //when
    String result = StringRedactor.redactString(string, true);
    //then
    assertThat(result).isNotNull().isEqualTo("a*****");
  }

  @Test
  void shouldRedactSingleCharString(){
    //given
    String string = "l";
    //when
    String result = StringRedactor.redactString(string, true);
    //then
    assertThat(result).isNotNull().isEqualTo("l*****");
  }

  @Test
  void shouldNotRedactStringWhenStringIsNull(){
    //given
    //when
    String result = StringRedactor.redactString(null, true);
    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldRedactEmptyCharString(){
    //given
    String string = "";
    //when
    String result = StringRedactor.redactString(string, true);
    //then
    assertThat(result).isNotNull().isEqualTo("");
  }

  @Test
  void shouldRedactSingleCharStringNotShowingFirstChar(){
    //given
    String string = "l";
    //when
    String result = StringRedactor.redactString(string, false);
    //then
    assertThat(result).isNotNull().isEqualTo("*****");
  }

}