package ch.sbb.exportservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

  public static final String SEMICOLON = ";";
  public static final String NEW_LINE = "\n";
  private static final String COLON = ":";

  public String replaceSemiColonWithColon(String value) {
    return value != null ? value.replace(SEMICOLON, COLON) : null;
  }

  public String removeNewLine(String string) {
    return string == null ? null : string.replaceAll("\r\n|\r|\n", " ");
  }

}
