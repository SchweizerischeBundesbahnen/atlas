package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportFileType;
import ch.sbb.exportservice.model.ServicePointExportType;

public class UploadCsvFileTasklet extends FileUploadTasklet {

  public UploadCsvFileTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportFileType getExportFileType() {
    return ExportFileType.CSV_EXTENSION;
  }
}
