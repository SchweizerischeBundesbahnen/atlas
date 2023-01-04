package ch.sbb.importservice.client;

import ch.sbb.importservice.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-Point-directory/v1/lines/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiLineExportFull();

}
