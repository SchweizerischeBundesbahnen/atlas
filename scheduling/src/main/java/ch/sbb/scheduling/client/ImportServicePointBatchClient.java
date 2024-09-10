package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "importServicePointBatch", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface ImportServicePointBatchClient {

  @PostMapping(value = "/import-service-point/v1/service-point-job/update-geo")
  Response triggerUpdateGeolocationServicePointJob();
}
