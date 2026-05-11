package ch.sbb.line.directory.module.bulkimport.subline.service;

import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;

public class SublineBulkImportUpdate extends
    BulkImportUpdateDataMapper<SublineUpdateCsvModel, SublineVersion, SublineVersionModelV2> {

  public static SublineVersionModelV2 apply(
      BulkImportUpdateContainer<SublineUpdateCsvModel> bulkImportContainer,
      SublineVersion currentVersion) {
    return new SublineBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new SublineVersionModelV2());
  }

  @Override
  protected void applySpecificUpdate(SublineUpdateCsvModel update, SublineVersion currentEntity,
      SublineVersionModelV2 targetModel) {
    targetModel.setMainlineSlnid(currentEntity.getMainlineSlnid());

    if (update.getSublineConcessionType() == null) {
      targetModel.setSublineConcessionType(currentEntity.getConcessionType());
    } else {
      targetModel.setSublineConcessionType(update.getSublineConcessionType());
    }
  }
}

