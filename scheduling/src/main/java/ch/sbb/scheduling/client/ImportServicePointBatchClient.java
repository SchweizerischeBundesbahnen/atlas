package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "imposrtServicePointBatch", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface ImportServicePointBatchClient {

  @PostMapping(value = "/import-service-point/v1/import/service-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postTriggerImportServicePointBatch();

}
