package ch.sbb.prm.directory.client;

import ch.sbb.atlas.api.client.location.BaseLocationClient;
import ch.sbb.prm.directory.configuration.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "locationClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LocationClient extends BaseLocationClient {

}
