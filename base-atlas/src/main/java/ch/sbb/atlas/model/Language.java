package ch.sbb.atlas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Locale;

@Schema(enumAsRef = true)
public enum Language {
  DE, FR, IT;

  public static Locale toLocale(Language language) {
    return switch (language) {
      case DE -> Locale.GERMAN;
      case FR -> Locale.FRENCH;
      case IT -> Locale.ITALIAN;
    };
  }
}
