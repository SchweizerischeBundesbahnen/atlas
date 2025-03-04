package ch.sbb.exportservice.tasklet.delete;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV1;
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

  private final ExportTypeBase exportType;
  private final ExportFileName exportFileName;

  protected FileDeletingTasklet(ExportTypeBase exportType, ExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportExtensionFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    String filePath = new ExportFilePathV1(exportType, exportFileName, fileService.getDir(),
        getExportExtensionFileType()).actualDateFilePath();
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
