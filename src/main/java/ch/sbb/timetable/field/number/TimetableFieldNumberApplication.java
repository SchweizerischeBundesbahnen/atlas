package ch.sbb.timetable.field.number;

import ch.sbb.timetable.field.number.versioning.service.VersionableService;
import ch.sbb.timetable.field.number.versioning.service.VersionableServiceImpl;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TimetableFieldNumberApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    SpringApplication.run(TimetableFieldNumberApplication.class, args);
  }

  @Bean public VersionableService versionableService() {
    return new VersionableServiceImpl();
  }

}
