package ch.sbb.prm.directory.service.bulk;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.prm.directory.entity.PlatformVersion;

public class PlatformBulkImportUpdate extends BulkImportUpdateDataMapper<PlatformReducedUpdateCsvModel, PlatformVersion, PlatformVersionModel> {

  public static PlatformVersionModel apply(
      BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> bulkImportContainer,
      PlatformVersion currentVersion) {
    return new PlatformBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new PlatformVersionModel());
  }

}
