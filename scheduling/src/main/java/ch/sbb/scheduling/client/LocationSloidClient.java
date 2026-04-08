package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "locationClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LocationSloidClient extends ch.sbb.atlas.api.client.location.LocationSloidClient {

}
