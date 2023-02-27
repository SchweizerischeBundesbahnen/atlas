package ch.sbb.atlas.timetable.hearing;

import ch.sbb.atlas.kafka.KafkaTruststorePreparation;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimetableHearingApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(TimetableHearingApplication.class, args);
  }

}
