package ch.sbb.workflow.sepodi.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = TokenPassingFeignClientConfig.class)
public interface SePoDiClient extends SePoDiApi {

}
