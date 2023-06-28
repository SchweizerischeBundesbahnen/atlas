package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }
}
