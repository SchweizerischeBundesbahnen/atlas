package ch.sbb.exportservice.tasklet;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class FileDeletingTasklet implements Tasklet {

  @Autowired
  private FileService fileService;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Resource fileSystemResource = new FileSystemResource(fileService.getDir());
    try (Stream<Path> walk =
        Files.walk(Paths.get(fileSystemResource.getFile().getPath()))) {
      walk.filter(Files::isRegularFile).map(Path::toFile)
          .forEach(File::delete);
    } catch (IOException e) {
      log.error("error deleting files", e);
      throw new FileException("unable to delete files");
    }
    return RepeatStatus.FINISHED;
  }
}
