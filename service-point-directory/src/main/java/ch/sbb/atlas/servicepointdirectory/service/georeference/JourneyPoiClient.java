package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("!local")
@FeignClient(name = "journeyPoiClient", url = "${journeyPoi.client.url}", configuration = JourneyPoiConfig.class)
public interface JourneyPoiClient extends JourneyPoiClientBase {

}
