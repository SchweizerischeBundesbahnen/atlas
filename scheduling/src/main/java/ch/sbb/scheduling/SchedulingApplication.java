package ch.sbb.scheduling;

import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

import java.time.ZoneId;
import java.util.TimeZone;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class SchedulingApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(SchedulingApplication.class, args);
  }

}
