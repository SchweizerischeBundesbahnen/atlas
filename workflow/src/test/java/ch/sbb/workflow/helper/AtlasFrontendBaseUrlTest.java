package ch.sbb.workflow.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AtlasFrontendBaseUrlTest {

  @Test
  public void shouldReturnLocalUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("local");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.LOCAL.getUrl());
  }

  @Test
  public void shouldReturnLocalUrlWhenActiveProfileIsNull() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl(null);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.LOCAL.getUrl());
  }

  @Test
  public void shouldReturnDevUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("dev");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.DEV.getUrl());
  }

  @Test
  public void shouldReturnTestUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("test");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.TEST.getUrl());
  }

  @Test
  public void shouldReturnIntUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("int");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.INT.getUrl());
  }

  @Test
  public void shouldReturnProdUrl() {
    //when
    String result = AtlasFrontendBaseUrl.getUrl("prod");

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(AtlasFrontendBaseUrl.PROD.getUrl());
  }

  @Test
  public void shouldThrowExceptionWithWrongActiveProfile() {
    //when
    assertThrows(IllegalStateException.class, () -> AtlasFrontendBaseUrl.getUrl("napoli"));

  }

}