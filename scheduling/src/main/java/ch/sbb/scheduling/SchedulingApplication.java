package ch.sbb.scheduling;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class SchedulingApplication {

  static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    SpringApplication.run(SchedulingApplication.class, args);
  }

}
