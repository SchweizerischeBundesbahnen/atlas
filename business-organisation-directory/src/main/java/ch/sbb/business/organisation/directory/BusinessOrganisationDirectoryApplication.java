package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.Objects;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class BusinessOrganisationDirectoryApplication {

  public static void main(String[] args) throws IOException {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    setupTruststore();
    SpringApplication.run(BusinessOrganisationDirectoryApplication.class, args);
  }

  private static void setupTruststore() throws IOException {
    Path truststore = Files.createTempFile("truststore", ".p12");
    Files.copy(
        Objects.requireNonNull(BusinessOrganisationDirectoryApplication.class.getClassLoader()
                                                                             .getResourceAsStream(
                                                                                 "kafka/"
                                                                                     + getTruststoreFileName()
                                                                                     + ".p12")),
        truststore,
        StandardCopyOption.REPLACE_EXISTING);
    System.setProperty("KAFKA_TRUSTSTORE_LOCATION", truststore.toUri().toString());
  }

  private static String getTruststoreFileName() {
    String truststoreFileName = "truststore-test";

    String profilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
    if ("int".equals(profilesActive)) {
      truststoreFileName = "truststore-inte";
    }
    if ("prod".equals(profilesActive)) {
      truststoreFileName = "truststore-prod";
    }
    return truststoreFileName;
  }

  @Bean
  public VersionableService versionableService() {
    return new VersionableServiceImpl();
  }

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

}
