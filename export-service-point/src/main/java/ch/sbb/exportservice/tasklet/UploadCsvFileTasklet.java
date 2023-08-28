package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ServicePointExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ExportType exportType, ServicePointExportFileName exportFileName) {
    super(exportType,exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }
}
