package ch.sbb.exportservice;

import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.TimeZone;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

@SpringBootApplication
public class ExportServicePointApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(ExportServicePointApplication.class, args);
  }

}
