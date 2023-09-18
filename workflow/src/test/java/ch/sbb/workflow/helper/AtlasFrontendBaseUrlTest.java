package ch.sbb.workflow.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

 class AtlasFrontendBaseUrlTest {

  @Test
   void shouldReturnLocalUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("local");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.LOCAL.getUrl());
  }

  @Test
   void shouldReturnLocalUrlWhenActiveProfileIsNull() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl(null);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.LOCAL.getUrl());
  }

  @Test
   void shouldReturnDevUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("dev");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.DEV.getUrl());
  }

  @Test
   void shouldReturnTestUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("test");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.TEST.getUrl());
  }

  @Test
   void shouldReturnIntUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("int");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.INT.getUrl());
  }

  @Test
   void shouldReturnProdUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("prod");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.PROD.getUrl());
  }

  @Test
   void shouldThrowExceptionWithWrongActiveProfile() {
    //when
    assertThrows(IllegalStateException.class, () -> AtlasFrontendBaseUrl.getUrl("napoli"));

  }

}