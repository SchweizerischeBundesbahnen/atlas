package ch.sbb.atlas.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class KafkaTruststorePreparation {

  public static void setupTruststore() {
    String truststoreFileName = getTruststoreFileName();
    log.info("Setting up Kafka Truststore. Using: {}", truststoreFileName);
    try {
      Path truststore = Files.createTempFile("truststore", ".p12");

      // Using the module of this class to look for truststores in resources folder
      ClassLoader classLoader = KafkaTruststorePreparation.class.getClassLoader();

      Files.copy(Objects.requireNonNull(
              classLoader.getResourceAsStream("kafka/" + truststoreFileName + ".p12")),
          truststore,
          StandardCopyOption.REPLACE_EXISTING);
      System.setProperty("KAFKA_TRUSTSTORE_LOCATION", truststore.toUri().toString());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
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
