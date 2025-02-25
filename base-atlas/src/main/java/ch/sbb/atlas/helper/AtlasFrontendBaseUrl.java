package ch.sbb.atlas.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AtlasFrontendBaseUrl {

  LOCAL("http://localhost:4200/"),
  DEV("https://atlas.dev.app.sbb.ch/"),
  TEST("https://atlas.test.app.sbb.ch/"),
  INT("https://atlas.int.app.sbb.ch/"),
  PROD("https://atlas.app.sbb.ch/");

  private final String url;

  public static String getUrl(String activeProfile) {
    if (activeProfile == null || "local".equals(activeProfile)) {
      return LOCAL.getUrl();
    }
    return switch (activeProfile) {
      case "dev" -> DEV.getUrl();
      case "test" -> TEST.getUrl();
      case "int" -> INT.getUrl();
      case "prod" -> PROD.getUrl();
      default -> throw new IllegalStateException("Please use a valid profile");
    };
  }
}
