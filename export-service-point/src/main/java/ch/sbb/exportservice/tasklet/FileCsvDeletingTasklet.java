package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class FileCsvDeletingTasklet extends FileDeletingTasklet<ExportTypeBase> implements Tasklet {

  public FileCsvDeletingTasklet(ExportTypeBase exportType, BatchExportFileName exportFileName) {
    super(exportType, exportFileName);
  }

  @Override
  protected ExportExtensionFileType getExportFileType() {
    return ExportExtensionFileType.CSV_EXTENSION;
  }

}
