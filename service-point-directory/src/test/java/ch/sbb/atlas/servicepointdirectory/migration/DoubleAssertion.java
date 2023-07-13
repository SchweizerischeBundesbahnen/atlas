package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.withPrecision;

import lombok.experimental.UtilityClass;
import org.assertj.core.data.Offset;

@UtilityClass
public class DoubleAssertion {

  public static Offset<Double> equalOnDecimalDigits(int digits) {
    double offsetValue = Math.pow(10.0, -(digits + 1));
    return withPrecision(offsetValue);
  }

}
