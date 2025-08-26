package ch.sbb.workflow.module.sepodi.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = TokenPassingFeignClientConfig.class)
public interface SePoDiClient extends SePoDiApi {

}
