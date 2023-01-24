package ch.sbb.workflow;

import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class WorkflowApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(WorkflowApplication.class, args);
  }

  @Bean
  public AtlasExceptionHandler atlasExceptionHandler() {
    return new AtlasExceptionHandler();
  }

}
