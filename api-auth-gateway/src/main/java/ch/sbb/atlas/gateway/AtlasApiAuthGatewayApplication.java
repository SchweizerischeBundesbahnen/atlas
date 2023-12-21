package ch.sbb.atlas.gateway;

import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtlasApiAuthGatewayApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZURICH_ZONE_ID)));
    SpringApplication.run(AtlasApiAuthGatewayApplication.class, args);
  }

}
