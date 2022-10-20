package ch.sbb.line.directory;

import ch.sbb.atlas.base.service.amazon.service.FileServiceImpl;
import ch.sbb.atlas.base.service.aspect.RunAsUserAspect;
import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.base.service.versioning.service.VersionableServiceImpl;
import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LineDirectoryApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(LineDirectoryApplication.class, args);
  }

  @Bean
  public RunAsUserAspect runAsUseAspect() {
    return new RunAsUserAspect();
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
  public FileServiceImpl fileService() {
    return new FileServiceImpl();
  }

}
