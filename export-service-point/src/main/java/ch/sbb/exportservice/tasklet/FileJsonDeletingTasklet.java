package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class FileJsonDeletingTasklet extends FileDeletingTasklet implements Tasklet {

  public FileJsonDeletingTasklet(ExportType exportType, ExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }

}
