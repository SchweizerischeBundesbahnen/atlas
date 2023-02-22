package ch.sbb.atlas.base.service.model.validation;

import ch.sbb.atlas.base.service.versioning.model.Versionable;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateValidations {

  public static boolean areOverlapping(List<? extends Versionable> versions) {
    for (int i = 0; i < versions.size() - 1; i++) {
      if (!versions.get(i).getValidTo().isBefore(versions.get(i + 1).getValidFrom()) &&
          !versions.get(i).getValidFrom().isAfter(versions.get(i + 1).getValidTo())) {
        return true;
      }
    }
    return false;
  }

}
