package ch.sbb.atlas.api.bodi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SboidToSaidConverter {

  private static final int INDEX_SBOID = 11;

  public static String toSaid(String sboid) {
    return sboid.substring(INDEX_SBOID);
  }
}
