package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class FileJsonDeletingTasklet extends FileDeletingTasklet implements Tasklet {

  public FileJsonDeletingTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportExtensionFileType getExportFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }

}
