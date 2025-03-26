package ch.sbb.atlas.servicepointdirectory.service.georeference;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("github")
@FeignClient(name = "journeyPoiClient", url = "${journeyPoi.client.url}")
public interface JourneyPoiClientLocal extends JourneyPoiClientBase {

}
