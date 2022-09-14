package ch.sbb.mail;

import ch.sbb.atlas.base.service.model.service.KafkaTruststorePreparation;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(MailApplication.class, args);
  }
}
