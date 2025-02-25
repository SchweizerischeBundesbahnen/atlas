package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class FileDeletingTasklet implements Tasklet {

  @Autowired
  private FileService fileService;

  private final ExportFilePath exportFilePath;

  protected FileDeletingTasklet(ExportFilePath.ExportFilePathBuilder filePathBuilder) {
    this.exportFilePath = filePathBuilder
        .extension(getExportExtensionFileType().getExtension())
        .build();
  }

  @PostConstruct
  public void init() {
    exportFilePath.setSystemDir(fileService.getDir());
  }

  protected abstract ExportExtensionFileType getExportExtensionFileType();

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
      log.info("File could not be deleted", e);
    }
    log.info("File {} deleted!", filePath);
    return RepeatStatus.FINISHED;
  }
}
