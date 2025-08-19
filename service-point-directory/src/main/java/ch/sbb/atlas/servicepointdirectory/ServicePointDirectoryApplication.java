package ch.sbb.atlas.servicepointdirectory;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import ch.sbb.atlas.validation.CreateCheckAspect;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CreateCheckAspect.class})
public class ServicePointDirectoryApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(ServicePointDirectoryApplication.class, args);
  }

}
