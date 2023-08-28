package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ServicePointExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;

public class UploadJsonFileTasklet extends FileUploadTasklet {

  public UploadJsonFileTasklet(ExportType exportType, ServicePointExportFileName exportFileName) {
    super(exportType,exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }
}
