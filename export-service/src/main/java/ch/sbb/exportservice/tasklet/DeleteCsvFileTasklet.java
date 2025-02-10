package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteCsvFileTasklet extends FileDeletingTasklet implements Tasklet {

  public DeleteCsvFileTasklet(ExportTypeBase exportType, ExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportExtensionFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
