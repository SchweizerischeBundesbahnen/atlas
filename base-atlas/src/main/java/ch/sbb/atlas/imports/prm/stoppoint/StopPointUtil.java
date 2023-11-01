package ch.sbb.atlas.imports.prm.stoppoint;

import java.util.Arrays;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointUtil {

  public static final String TILDE_SEPARATOR = "~";
  public static final String EMPTY_CHAR = "";
  public static final int ONE_TRANSPORTATION_MEAN_CODE_LENGTH = 3;

  /**
   * To be able to compare correctly two versions we need to sort the transportationMeans
   */
  public static String sortTransportationMeans(String transportationMeans) {
    if (transportationMeans != null && transportationMeans.length() > ONE_TRANSPORTATION_MEAN_CODE_LENGTH) {
      char[] charArray = transportationMeans.replace(TILDE_SEPARATOR, EMPTY_CHAR).toCharArray();
      Arrays.sort(charArray);
      StringBuilder builder = new StringBuilder(TILDE_SEPARATOR);
      for (char c : charArray) {
        builder.append(c).append(TILDE_SEPARATOR);
      }
      return builder.toString();
    }
    return transportationMeans;
  }

}
