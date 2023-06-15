package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.service.FileExportService;
import java.io.File;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Slf4j
public abstract class FileUploadTasklet implements Tasklet {

  @Autowired
  private FileService fileService;
  @Autowired
  private FileExportService fileExportService;
  private ServicePointExportType exportType;

  public FileUploadTasklet(ServicePointExportType exportType) {
    this.exportType = exportType;
  }

  protected abstract ExportFileType getExportFileType();

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.warn("FileUploadTasklet: {}", this.exportType.getDir());
    String fileNamePath = fileExportService.createFileNamePath(getExportFileType(), exportType);
    Resource fileSystemResource = new FileSystemResource(fileService.getDir());
    log.info("Res: {}", fileSystemResource);
    File file = Paths.get(fileNamePath).toFile();
    fileExportService.exportFile(file, exportType);
    return RepeatStatus.FINISHED;
  }
}
