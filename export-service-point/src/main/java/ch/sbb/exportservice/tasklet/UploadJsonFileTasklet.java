package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }
}
