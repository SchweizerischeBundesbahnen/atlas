package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;

public class UploadCsvFileTasklet extends FileUploadTasklet<ExportTypeBase> {

  public UploadCsvFileTasklet(ExportTypeBase exportType, BatchExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
