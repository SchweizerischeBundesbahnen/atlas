package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;

public class UploadJsonFileTasklet extends FileUploadTasklet<ExportTypeBase> {

  public UploadJsonFileTasklet(ExportTypeBase exportType, BatchExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }
}
