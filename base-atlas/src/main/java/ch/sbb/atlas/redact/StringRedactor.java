package ch.sbb.atlas.redact;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class StringRedactor {

  private static final String REPLACEMENT = "*****";

  public static String redactString(String string, boolean showFirstChar) {
    if (string == null) {
      return null;
    }
    if (StringUtils.isBlank(string)) {
      return string;
    }
    if (showFirstChar) {
      return string.charAt(0) + REPLACEMENT;
    }
    return REPLACEMENT;
  }

}
