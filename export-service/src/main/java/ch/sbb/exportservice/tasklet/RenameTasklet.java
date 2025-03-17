package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportFilePathV2;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Deprecated(forRemoval = true)
@Slf4j
public class RenameTasklet implements Tasklet {

  private final ExportFilePathV2 filePathV2;
  private final ExportFilePathV1 filePathV1;

  public RenameTasklet(ExportFilePathV2 filePathV2, ExportFilePathV1 filePathV1) {
    this.filePathV2 = filePathV2;
    this.filePathV1 = filePathV1;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    final File v2File = new File(filePathV2.actualDateFilePath());
    final File v1File = new File(filePathV1.actualDateFilePath());
    final boolean renamed = v2File.renameTo(v1File);
    if (!renamed) {
      log.error("Error during execution of RenameTasklet");
      throw new RuntimeException("Could not rename!");
    }
    return RepeatStatus.FINISHED;
  }

}
