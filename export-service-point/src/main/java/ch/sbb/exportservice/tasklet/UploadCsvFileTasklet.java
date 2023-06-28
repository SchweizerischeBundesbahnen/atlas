package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }
}
