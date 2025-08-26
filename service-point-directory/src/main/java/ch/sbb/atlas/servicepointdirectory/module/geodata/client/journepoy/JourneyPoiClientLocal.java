package ch.sbb.atlas.servicepointdirectory.module.geodata.client.journepoy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("github")
@FeignClient(name = "journeyPoiClient", url = "${journeyPoi.client.url}")
public interface JourneyPoiClientLocal extends JourneyPoiClientBase {

}
