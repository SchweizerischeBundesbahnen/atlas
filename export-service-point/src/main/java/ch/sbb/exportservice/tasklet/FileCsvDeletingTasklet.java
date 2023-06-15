package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.tasklet.Tasklet;

@Slf4j
public class FileCsvDeletingTasklet extends FileDeletingTasklet implements Tasklet {

  public FileCsvDeletingTasklet(ServicePointExportType exportType) {
    super(exportType);
  }

  @Override
  protected ExportFileType getExportFileType() {
    return ExportFileType.CSV_EXTENSION;
  }

}
