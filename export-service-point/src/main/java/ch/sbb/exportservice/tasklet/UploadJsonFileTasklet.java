package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportFileType;
import ch.sbb.exportservice.model.ServicePointExportType;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportFileType getExportFileType() {
    return ExportFileType.JSON_EXTENSION;
  }
}
