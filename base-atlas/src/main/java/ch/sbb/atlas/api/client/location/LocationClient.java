package ch.sbb.atlas.api.client.location;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "locationClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LocationClient {

  @PostMapping("/location/v1/sloid/claim")
  String claimSloid(@RequestBody ClaimSloidRequestModel claimSloidRequestModel);

  @PostMapping("/location/v1/sloid/generate")
  String generateSloid(@RequestBody GenerateSloidRequestModel generateSloidRequestModel);

}
