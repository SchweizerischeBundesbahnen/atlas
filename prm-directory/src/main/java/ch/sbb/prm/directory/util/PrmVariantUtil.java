package ch.sbb.prm.directory.util;

import ch.sbb.prm.directory.entity.StopPointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PrmVariantUtil {

  public static boolean isChangingFromReducedToComplete(StopPointVersion stopPointVersionToUpdate, StopPointVersion editedVersion) {
    return stopPointVersionToUpdate.isReduced() && !editedVersion.isReduced();
  }

  public static boolean isChangingFromCompleteToReduced(StopPointVersion stopPointVersionToUpdate, StopPointVersion editedVersion) {
    return !stopPointVersionToUpdate.isReduced() && editedVersion.isReduced();
  }

  public static boolean isPrmVariantChanging(StopPointVersion currentVersion, StopPointVersion editedVersion) {
    return currentVersion.isReduced() !=  editedVersion.isReduced();
  }


}
