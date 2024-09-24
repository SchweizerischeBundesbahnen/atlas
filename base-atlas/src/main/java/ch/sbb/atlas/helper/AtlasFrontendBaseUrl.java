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
    if ("dev".equals(activeProfile)) {
      return DEV.getUrl();
    }
    if ("test".equals(activeProfile)) {
      return TEST.getUrl();
    }
    if ("int".equals(activeProfile)) {
      return INT.getUrl();
    }
    if ("prod".equals(activeProfile)) {
      return PROD.getUrl();
    }
    throw new IllegalStateException("Please use a valid profile");
  }
}
