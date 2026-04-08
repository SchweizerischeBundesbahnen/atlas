package ch.sbb.atlas.servicepointdirectory.client;

import ch.sbb.atlas.servicepointdirectory.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "locationClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LocationSloidClient extends ch.sbb.atlas.api.client.location.LocationSloidClient {

}
