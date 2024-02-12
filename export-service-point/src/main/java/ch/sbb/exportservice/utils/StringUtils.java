package ch.sbb.exportservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

  public static final String SEMICOLON = ";";
  private static final String COLON = ":";

  public String replaceSemiColonWithColon(String value) {
    return value != null ? value.replaceAll(SEMICOLON, COLON) : null;
  }

}
