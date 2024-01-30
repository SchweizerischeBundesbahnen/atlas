package ch.sbb.exportservice.processor;

import lombok.experimental.UtilityClass;

@UtilityClass
class CsvProcessorUtil {

  static String removeNewLine(String string) {
    return string == null ? null : string.replaceAll("\r\n|\r|\n", " ");
  }

}
