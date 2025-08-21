package ch.sbb.line.directory.module.bulkimport.line.service;

import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.line.directory.module.line.entity.LineVersion;

public class LineBulkImportUpdate extends BulkImportUpdateDataMapper<LineUpdateCsvModel, LineVersion, UpdateLineVersionModelV2> {

  public static UpdateLineVersionModelV2 apply(
      BulkImportUpdateContainer<LineUpdateCsvModel> bulkImportContainer,
      LineVersion currentVersion) {
    return new LineBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new UpdateLineVersionModelV2());
  }

  @Override
  protected void applySpecificUpdate(LineUpdateCsvModel update, LineVersion currentEntity, UpdateLineVersionModelV2 targetModel) {
    if (update.getLineConcessionType() != null) {
      targetModel.setLineConcessionType(update.getLineConcessionType());
    } else {
      targetModel.setLineConcessionType(currentEntity.getConcessionType());
    }
  }
}
