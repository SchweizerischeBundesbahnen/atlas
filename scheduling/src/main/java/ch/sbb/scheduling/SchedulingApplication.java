package ch.sbb.scheduling;

import ch.sbb.atlas.base.service.model.configuration.CorrelationIdFilterConfig;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@Import(CorrelationIdFilterConfig.class)
public class SchedulingApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    SpringApplication.run(SchedulingApplication.class, args);
  }
}
