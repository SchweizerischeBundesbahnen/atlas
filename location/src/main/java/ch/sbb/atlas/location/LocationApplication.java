package ch.sbb.atlas.location;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocationApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    SpringApplication.run(LocationApplication.class, args);
  }

}
