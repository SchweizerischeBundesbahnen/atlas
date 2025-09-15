package ch.sbb.prm.directory.module.bulkimport.mapper;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;

public class PlatformBulkImportCompleteUpdate extends BulkImportUpdateDataMapper<PlatformCompleteUpdateCsvModel, PlatformVersion,
    PlatformVersionModel> {

  public static PlatformVersionModel apply(
      BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel> bulkImportContainer,
      PlatformVersion currentVersion) {
    return new PlatformBulkImportCompleteUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new PlatformVersionModel());
  }

}
