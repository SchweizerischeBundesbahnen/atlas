package ch.sbb.line.directory.module.bulkimport.line.service;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.imports.bulk.BulkImportCreateDataMapper;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;

public class LineBulkImportCreate extends BulkImportCreateDataMapper<LineCreateCsvModel, LineVersionModelV2> {

  public static LineVersionModelV2 apply(
      BulkImportUpdateContainer<LineCreateCsvModel> bulkImportContainer) {
    return new LineBulkImportCreate().applyCreate(bulkImportContainer,
        new LineVersionModelV2());
  }
}
