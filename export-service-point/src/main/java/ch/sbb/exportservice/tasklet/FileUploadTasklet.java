package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.exportservice.service.ExportServicePointDirectory;
import ch.sbb.exportservice.service.FileExportService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Slf4j
public class FileUploadTasklet implements Tasklet {

  @Autowired
  private FileExportService fileExportService;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Resource fileSystemResource = new FileSystemResource(".export/");
    log.info("Res: {}", fileSystemResource);
    try (Stream<Path> walk = Files.walk(Paths.get(fileSystemResource.getFile().getPath()))) {
      List<File> files = walk.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
      fileExportService.exportFiles(files, ExportServicePointDirectory.FULL);
    } catch (IOException e) {
      log.error("error uploading files", e);
      throw new FileException("unable to upload files");
    }

    return RepeatStatus.FINISHED;
  }
}
