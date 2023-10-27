package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.service.FileExportService;
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
public abstract class FileDeletingTasklet<T extends ExportTypeBase> implements Tasklet {

  @Autowired
  private FileExportService<T> fileExportService;
  private final T exportType;
  private final BatchExportFileName exportFileName;

  protected FileDeletingTasklet(T exportType, BatchExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    String fileNamePath = fileExportService.createFileNamePath(getExportFileType(), exportType, exportFileName);
    log.info("File {} deleting...", fileNamePath);
    try {
      Path path = Paths.get(fileNamePath);
      if(Files.exists(path)){
        Files.delete(path);
      }
    } catch (IOException e) {
      log.info("File could not be deleted", e);
    }
    log.info("File {} deleted!", fileNamePath);
    return RepeatStatus.FINISHED;
  }
}
