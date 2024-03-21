package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
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
public abstract class FileUploadTasklet implements Tasklet {

  @Autowired
  private FileService fileService;

  @Autowired
  protected AmazonService amazonService;

  private final ExportTypeBase exportType;
  private final ExportFileName exportFileName;

  protected ExportFilePath exportFilePath;

  protected FileUploadTasklet(ExportTypeBase exportType, ExportFileName exportFileName) {
    this.exportType = exportType;
    this.exportFileName = exportFileName;
  }

  protected abstract ExportExtensionFileType getExportExtensionFileType();

  protected abstract void putFile() throws IOException;

  protected File file() {
    return Paths.get(exportFilePath.actualDateFilePath()).toFile();
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    exportFilePath = new ExportFilePath(exportType, exportFileName, fileService.getDir(), getExportExtensionFileType());
    log.info("File {} uploading...", exportFilePath.actualDateFilePath());
    exportFile();
    log.info("File {} uploaded!", exportFilePath.actualDateFilePath());
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
