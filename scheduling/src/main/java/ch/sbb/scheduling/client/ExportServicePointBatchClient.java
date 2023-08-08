package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "exportServicePointBatch", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface ExportServicePointBatchClient {

  @PostMapping(value = "/export-service-point/v1/export/service-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postTriggerExportServicePointBatch();

  @PostMapping(value = "/export-service-point/v1/export/traffic-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postTriggerExportTrafficPointBatch();

}
