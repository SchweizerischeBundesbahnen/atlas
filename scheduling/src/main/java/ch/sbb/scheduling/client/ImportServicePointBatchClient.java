package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "importServicePointBatch", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface ImportServicePointBatchClient {

  @PostMapping(value = "/import-service-point/v1/import/service-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportServicePointBatch();

  @PostMapping(value = "/import-service-point/v1/import/traffic-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportTrafficPointBatch();

  @PostMapping(value = "/import-service-point/v1/import/loading-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportLoadingPointBatch();

  @PostMapping(value = "/import-service-point/v1/import-prm/stop-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportStopPointBatch();

  @PostMapping(value = "/import-service-point/v1/import-prm/platform-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportPlatformBatch();

  @PostMapping(value = "/import-service-point/v1/import-prm/reference-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportReferencePointBatch();
  @PostMapping(value = "/import-service-point/v1/import-prm/toilet-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response triggerImportToiletBatch();
}
