package ch.sbb.importservice.client;

import ch.sbb.importservice.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-point-directory/v1/service-points/to/be/define", produces = MediaType.APPLICATION_JSON_VALUE)
  Response postServicePoints();

  @GetMapping(value = "/service-point-directory/v1/service-points/versions/{id}")
  Response getServicePoints(@PathVariable Integer id);

}
