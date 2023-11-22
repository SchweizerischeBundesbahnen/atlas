package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.journey.poi.api.V1Api;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "journeyPoiClient", url = "https://journey-pois-int.api.sbb.ch:443", configuration = JourneyPoiConfig.class)
public interface JourneyPoiClient extends V1Api {

}
