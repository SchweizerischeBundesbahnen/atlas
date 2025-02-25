package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteCsvFileTasklet extends FileDeletingTasklet implements Tasklet {

  public DeleteCsvFileTasklet(ExportFilePath.ExportFilePathBuilder filePathBuilder) {
    super(filePathBuilder);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
