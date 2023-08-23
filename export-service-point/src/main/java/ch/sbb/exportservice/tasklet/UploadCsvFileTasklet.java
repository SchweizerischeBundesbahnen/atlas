package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.SpExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ExportType exportType, SpExportFileName exportFileName) {
    super(exportType,exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }
}
