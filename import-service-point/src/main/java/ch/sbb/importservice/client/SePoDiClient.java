package ch.sbb.importservice.client;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-point-directory/v1/service-points/to/be/define", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postServicePoints();

  @PostMapping(value = "/service-point-directory/v1/service-points/import")
  Response postServicePointsImport(@RequestBody ServicePointImportReqModel servicePoints);

}
