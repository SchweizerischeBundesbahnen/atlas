package ch.sbb.atlas.servicepointdirectory.module.servicepoint.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import org.junit.jupiter.api.Test;

class GlobalIdTest {

  @Test
  void shouldRejectNullValue() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of(null, Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ILLEGAL_ARGUMENT"));
  }

  @Test
  void shouldRejectEmptyValue() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("", Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ILLEGAL_ARGUMENT"));
  }

  @Test
  void shouldRejectBlankValue() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("   ", Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ILLEGAL_ARGUMENT"));
  }

  @Test
  void shouldAcceptGermanGlobalIdWithDePrefix() {
    // When / Then
    assertThat(GlobalId.of("de:05770:1282", Country.GERMANY).value()).isEqualTo("de:05770:1282");
    assertThat(GlobalId.of("de:05770:1282", Country.GERMANY_BUS).value()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldAcceptAustrianGlobalIdWithAtPrefix() {
    // When / Then
    assertThat(GlobalId.of("at:42:9379", Country.AUSTRIA).value()).isEqualTo("at:42:9379");
    assertThat(GlobalId.of("at:42:9379", Country.AUSTRIA_BUS).value()).isEqualTo("at:42:9379");
  }

  @Test
  void shouldRejectGermanGlobalIdWithAtPrefix() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("at:42:9379", Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.COUNTRY_MISMATCH"));
  }

  @Test
  void shouldRejectAustrianGlobalIdWithDePrefix() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("de:05770:1282", Country.AUSTRIA))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.COUNTRY_MISMATCH"));
  }

  @Test
  void shouldRejectGlobalIdForOtherCountry() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("de:05770:1282", Country.SWITZERLAND))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.NOT_ALLOWED_FOR_COUNTRY"));
  }

  @Test
  void shouldRejectGlobalIdWithLeadingWhitespace() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of(" de:05770:1282", Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.WHITESPACE"));
  }

  @Test
  void shouldRejectGlobalIdWithTrailingWhitespace() {
    // When / Then
    assertThatThrownBy(() -> GlobalId.of("de:05770:1282 ", Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.WHITESPACE"));
  }

  @Test
  void shouldRejectGlobalIdExceedingMaxLength() {
    // Given
    String tooLong = "de:" + "1".repeat(126);

    // When / Then
    assertThatThrownBy(() -> GlobalId.of(tooLong, Country.GERMANY))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.MAX_LENGTH"));
  }

}
