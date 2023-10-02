package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;

@Slf4j
public abstract class FileDeletingTasklet implements Tasklet {

  @Autowired
  private FileExportService fileExportService;
  private final ExportType exportType;
  private final BatchExportFileName exportFileName;

  protected FileDeletingTasklet(ExportType exportType, BatchExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    String fileNamePath = fileExportService.createFileNamePath(getExportFileType(), exportType, exportFileName);
    log.info("File {} deleting...", fileNamePath);
    try {
      Files.delete(Paths.get(fileNamePath));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    log.info("File {} deleted!", fileNamePath);
    return RepeatStatus.FINISHED;
  }
}
