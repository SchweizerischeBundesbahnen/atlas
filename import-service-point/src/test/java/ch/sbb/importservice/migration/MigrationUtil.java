package ch.sbb.importservice.migration;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MigrationUtil {


  public static int removeCheckDigit(StopPointCsvModel stopPointCsvModel) {
    return removeCheckDigit(stopPointCsvModel.getDidokCode());
  }
  public static int removeCheckDigit(Integer didokCode) {
    String didokCodeAsString = String.valueOf(didokCode);
    return Integer.parseInt(didokCodeAsString.substring(0, didokCodeAsString.length() - 1));
  }

  public static int removeCheckDigit(PlatformCsvModel platformCsvModel) {
    return removeCheckDigit(platformCsvModel.getDidokCode());
  }

  public static int removeCheckDigit(ReferencePointCsvModel referencePointCsvModel) {
    return removeCheckDigit(referencePointCsvModel.getDidokCode());
  }
}
