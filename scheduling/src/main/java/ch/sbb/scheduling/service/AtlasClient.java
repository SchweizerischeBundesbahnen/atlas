package ch.sbb.scheduling.service;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "atlasClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface AtlasClient {

  @PostMapping(value = "/line-directory/v1/lines/export-csv/full/csv", produces = "application/json")
  Response putLiDiExportFullCsv();

  @GetMapping(value = "/line-directory/v1/lines/covered", produces = "application/json")
  Response getSomething();

}
