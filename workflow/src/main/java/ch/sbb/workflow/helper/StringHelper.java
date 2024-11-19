package ch.sbb.workflow.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringHelper {

  private static final String REPLACEMENT = "*****";

  public static String redactString(String string) {
    return redactString(string, false);
  }

  public static String redactString(String string, boolean showFirstChar) {
    if (showFirstChar) {
      if (string != null && !string.isEmpty()) {
        return string.charAt(0) + REPLACEMENT;
      }
      return string;
    } else {
      return REPLACEMENT;
    }
  }

}
