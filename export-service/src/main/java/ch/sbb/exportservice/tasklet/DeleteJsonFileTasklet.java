package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteJsonFileTasklet extends FileDeletingTasklet implements Tasklet {

  public DeleteJsonFileTasklet(ExportFilePath.ExportFilePathBuilder filePathBuilder) {
    super(filePathBuilder);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.JSON_EXTENSION;
  }

}
