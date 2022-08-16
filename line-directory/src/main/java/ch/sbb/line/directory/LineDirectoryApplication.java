package ch.sbb.line.directory;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LineDirectoryApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    SpringApplication.run(LineDirectoryApplication.class, args);
  }

  @Bean
  public VersionableService versionableService() {
    return new VersionableServiceImpl();
  }


  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

  @Bean
  public FileService fileService() {
    return new FileService();
  }

}
