package ch.sbb.atlas.math;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DoubleOperations {

  public static Double round(double value, int places) {
    final byte BASE = 10;
    double scale = Math.pow(BASE, places);
    return Math.round(value * scale) / scale;
  }

  public static int getFractions(double value) {
    BigDecimal bigDecimal = BigDecimal.valueOf(value);
    return bigDecimal.scale();
  }

}
