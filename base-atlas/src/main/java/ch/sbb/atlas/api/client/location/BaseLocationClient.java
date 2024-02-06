package ch.sbb.atlas.api.client.location;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import feign.Response;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BaseLocationClient {

  @PostMapping("/location/v1/sloid/generate")
  String generateSloid(@RequestBody @Valid GenerateSloidRequestModel generateSloidRequestModel);

  @PostMapping("/location/v1/sloid/claim")
  String claimSloid(@RequestBody @Valid ClaimSloidRequestModel claimSloidRequestModel);

  @PostMapping(value = "/location/v1/sloid/maintenance/sync")
  Response syncSloid();

}
