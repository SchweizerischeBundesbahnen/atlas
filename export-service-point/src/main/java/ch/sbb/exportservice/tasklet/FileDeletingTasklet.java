package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ServicePointExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
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
  private ExportType exportType;
  private ServicePointExportFileName exportFileName;

  protected FileDeletingTasklet(ExportType exportType, ServicePointExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    String fileNamePath = fileExportService.createFileNamePath(getExportFileType(), exportType, exportFileName);
    log.info("File {} deleting...", fileNamePath);
    Paths.get(fileNamePath).toFile().delete();
    log.info("File {} deleted!", fileNamePath);
    return RepeatStatus.FINISHED;
  }
}
