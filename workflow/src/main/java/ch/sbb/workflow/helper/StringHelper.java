package ch.sbb.workflow.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringHelper {

  private static final String REPLACEMENT = "*****";
  private static final int BEGIN_INDEX = 1;

  public static String redactString(String string) {
    if (string != null && !string.isEmpty()) {
      return string.replaceAll(string.substring(BEGIN_INDEX), REPLACEMENT);
    }
    return string;
  }

}
