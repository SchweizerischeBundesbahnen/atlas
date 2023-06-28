package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class FileCsvDeletingTasklet extends FileDeletingTasklet implements Tasklet {

  public FileCsvDeletingTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportExtensionFileType getExportFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
