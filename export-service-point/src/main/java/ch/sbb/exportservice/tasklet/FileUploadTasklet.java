package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.service.FileExportService;
import java.io.File;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class FileUploadTasklet<T extends ExportTypeBase> implements Tasklet {

  @Autowired
  private FileExportService<T> fileExportService;
  private final T exportType;
  private final BatchExportFileName exportFileName;

  protected FileUploadTasklet(T exportType, BatchExportFileName exportFileName) {
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
