package ch.sbb.exportservice.tasklet.delete;

import ch.sbb.exportservice.model.ExportFilePathV2;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public abstract class FileDeletingTaskletV2 implements Tasklet {

  private final ExportFilePathV2 exportFilePath;

  protected FileDeletingTaskletV2(ExportFilePathV2 filePath) {
    this.exportFilePath = filePath;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    final String filePath = exportFilePath.actualDateFilePath();
    log.info("File {} deleting...", filePath);
    try {
      Path path = Paths.get(filePath);
      if (Files.exists(path)) {
        Files.deleteIfExists(path);
      }
    } catch (IOException e) {
      log.error("File could not be deleted", e);
    }
    log.info("File {} deleted!", filePath);
    return RepeatStatus.FINISHED;
  }

}
