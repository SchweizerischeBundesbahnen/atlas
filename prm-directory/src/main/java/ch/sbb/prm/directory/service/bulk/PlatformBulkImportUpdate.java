package ch.sbb.prm.directory.service.bulk;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.prm.directory.entity.PlatformVersion;

public class PlatformBulkImportUpdate extends BulkImportDataMapper<PlatformReducedUpdateCsvModel, PlatformVersion, PlatformVersionModel> {

  public static PlatformVersionModel apply(
      BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> bulkImportContainer,
      PlatformVersion currentVersion) {
    return new PlatformBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new PlatformVersionModel());
  }

  @Override
  protected void applySpecificUpdate(PlatformReducedUpdateCsvModel update, PlatformVersion currentVersion,      PlatformVersionModel updateModel) {
    setNonUpdatableValues(currentVersion, updateModel);
  }

  private static void setNonUpdatableValues(PlatformVersion currentVersion, PlatformVersionModel updateModel) {
    updateModel.setId(currentVersion.getId());
    updateModel.setEtagVersion(currentVersion.getVersion());

    updateModel.setParentServicePointSloid(currentVersion.getParentServicePointSloid());
  }

}
