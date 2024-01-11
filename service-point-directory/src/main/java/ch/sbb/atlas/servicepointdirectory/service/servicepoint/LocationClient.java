package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.servicepointdirectory.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "locationClient", url = "http://localhost:8888", configuration = OAuthFeignConfig.class)
public interface LocationClient {

  @PostMapping("/location/v1/sloid/claim")
  String claimSloid(@RequestBody ClaimSloidRequestModel claimSloidRequestModel);

  @PostMapping("/location/v1/sloid/generate")
  String generateSloid(@RequestBody GenerateSloidRequestModel generateSloidRequestModel);

}
