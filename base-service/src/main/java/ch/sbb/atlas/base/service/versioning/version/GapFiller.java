package ch.sbb.atlas.base.service.versioning.version;

import ch.sbb.atlas.base.service.versioning.date.DateHelper;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class GapFiller {

  private GapFiller() {
    throw new IllegalStateException("Utility class");
  }

  public static void fillGapsInToVersioning(VersioningData vd) {
    if (VersioningHelper.isThereGapBetweenVersions(vd.getObjectsToVersioning())) {
      log.info("Found a gap(s) in the update period ... hold on, we will patch them");
      fillGapsInVersions(vd);
    } else {
      log.info("No gap(s) present in the update period, excellent ...");
    }
  }

  private static void fillGapsInVersions(VersioningData versioningData) {
    for (int i = 0; i < versioningData.getObjectsToVersioning().size() - 1; i++) {
      ToVersioning current = versioningData.getObjectsToVersioning().get(i);
      ToVersioning next = versioningData.getObjectsToVersioning().get(i + 1);

      if (!VersioningHelper.areVersionsSequential(current, next)) {
        LocalDate prolongToDate = DateHelper.min(next.getValidFrom().minusDays(1),
            versioningData.getEditedValidTo());
        log.info(
            "Found a gap from version {} (validTo: {}) to version {} (validFrom: {}). Prolonging to {}",
            i,
            current.getValidTo(), i + 1,
            next.getValidFrom(), prolongToDate);
        current.setValidTo(prolongToDate);
      }
    }
  }
}
