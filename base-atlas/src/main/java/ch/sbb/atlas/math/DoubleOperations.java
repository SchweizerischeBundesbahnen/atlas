package ch.sbb.atlas.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DoubleOperations {

  public static Double round(double value, int places){
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }

}
