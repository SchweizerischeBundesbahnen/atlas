package ch.sbb.exportservice.tasklet.upload;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.model.ExportFilePathV2;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class FileUploadTaskletV2 implements Tasklet {

  @Autowired
  protected AmazonService amazonService;

  protected final ExportFilePathV2 filePath;

  protected FileUploadTaskletV2(ExportFilePathV2 filePath) {
    this.filePath = filePath;
  }

  protected abstract void putFile() throws IOException;

  protected File file() {
    return Paths.get(filePath.actualDateFilePath()).toFile();
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("File {} uploading...", filePath.actualDateFilePath());
    exportFile();
    log.info("File {} uploaded!", filePath.actualDateFilePath());

    chunkContext
        .getStepContext()
        .getStepExecution()
        .getJobExecution()
        .getExecutionContext()
        .put("filePathV2", filePath);

    return RepeatStatus.FINISHED;
  }

  private void exportFile() {
    try {
      putFile();
    } catch (IOException e) {
      throw new FileException("Error uploading file: " + file().getName() + " to bucket: " + AmazonBucket.EXPORT, e);
    }
  }
}
