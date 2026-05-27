package ch.sbb.atlas.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class LanguageTest {

  @Test
  void shouldMapLanguageToLocale() {
    assertThat(Language.toLocale(Language.DE)).isEqualTo(Locale.GERMAN);
    assertThat(Language.toLocale(Language.FR)).isEqualTo(Locale.FRENCH);
    assertThat(Language.toLocale(Language.IT)).isEqualTo(Locale.ITALIAN);
  }
}
