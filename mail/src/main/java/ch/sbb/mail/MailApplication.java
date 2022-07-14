package ch.sbb.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.Objects;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailApplication {

  public static void main(String[] args) throws IOException {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    setupTruststore();
    SpringApplication.run(MailApplication.class, args);
  }

  private static void setupTruststore() throws IOException {
    Path truststore = Files.createTempFile("truststore", ".p12");
    Files.copy(Objects.requireNonNull(MailApplication.class.getClassLoader()
                                                           .getResourceAsStream(
                                                               "kafka/" + getTruststoreFileName()
                                                                   + ".p12")), truststore,
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
}
