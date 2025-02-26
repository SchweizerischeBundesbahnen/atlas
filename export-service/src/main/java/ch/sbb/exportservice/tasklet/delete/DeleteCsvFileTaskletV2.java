package ch.sbb.exportservice.tasklet.delete;

import ch.sbb.exportservice.model.ExportFilePathV2;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteCsvFileTaskletV2 extends FileDeletingTaskletV2 implements Tasklet {

  public DeleteCsvFileTaskletV2(ExportFilePathV2 filePath) {
    super(filePath);
  }

}
