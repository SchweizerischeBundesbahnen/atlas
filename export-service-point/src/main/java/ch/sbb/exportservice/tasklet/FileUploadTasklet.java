package ch.sbb.exportservice.tasklet;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
public abstract class FileUploadTasklet implements Tasklet {

  @Autowired
  private FileExportService fileExportService;
  private final ExportType exportType;
  private final BatchExportFileName exportFileName;

  protected FileUploadTasklet(ExportType exportType, BatchExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportExtensionFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    String fileNamePath = fileExportService.createFileNamePath(getExportExtensionFileType(), exportType, exportFileName);
    File file = Paths.get(fileNamePath).toFile();
    log.info("File {} uploading...", fileNamePath);
    fileExportService.exportFile(file, exportType, exportFileName, getExportExtensionFileType());
    log.info("File {} uploaded!", fileNamePath);
    return RepeatStatus.FINISHED;
  }
}
