package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "bodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface BoDiClient {

  @PostMapping(value = "/business-organisation-directory/v1/companies/loadFromCRD", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postLoadCompaniesFromCRD();

  @PostMapping(value = "/business-organisation-directory/v1/transport-companies/loadFromBAV", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postLoadTransportCompaniesFromBav();

}
