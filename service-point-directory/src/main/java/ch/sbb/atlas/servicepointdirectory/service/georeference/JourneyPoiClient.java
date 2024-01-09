package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.journey.poi.api.V1Api;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "journeyPoiClient", url = "${spring.journeyPoi.client.gateway.url}", configuration = JourneyPoiConfig.class)
public interface JourneyPoiClient extends V1Api {

}
