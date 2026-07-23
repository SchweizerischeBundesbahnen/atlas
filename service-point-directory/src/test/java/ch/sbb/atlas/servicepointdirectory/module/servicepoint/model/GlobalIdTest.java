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
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Global-ID value must not be null");
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

}
