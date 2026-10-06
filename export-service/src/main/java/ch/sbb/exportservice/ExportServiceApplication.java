package ch.sbb.exportservice;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ExportServiceApplication {

  static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    SpringApplication.run(ExportServiceApplication.class, args);
  }

}
