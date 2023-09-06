package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class FileCsvDeletingTasklet extends FileDeletingTasklet implements Tasklet {

  public FileCsvDeletingTasklet(ExportType exportType, BatchExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
