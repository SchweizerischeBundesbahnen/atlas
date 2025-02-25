package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import jakarta.annotation.PostConstruct;
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

  private final ExportFilePath systemFile;

  protected final ExportFilePath s3File;

  protected FileUploadTasklet(
      ExportFilePath.ExportFilePathBuilder systemFile,
      ExportFilePath.ExportFilePathBuilder s3File) {
    this.systemFile = systemFile.extension(getExportExtensionFileType().getExtension()).build();
    this.s3File = s3File.extension(getExportExtensionFileType().getExtension()).build();
  }

  @PostConstruct
  private void init() {
    systemFile.setSystemDir(fileService.getDir());
  }

  protected abstract ExportExtensionFileType getExportExtensionFileType();

  protected abstract void putFile() throws IOException;

  protected File file() {
    return Paths.get(systemFile.actualDateFilePath()).toFile();
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("File {} uploading...", systemFile.actualDateFilePath());
    exportFile();
    log.info("File {} uploaded!", systemFile.actualDateFilePath());
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
