package ch.sbb.atlas.api.client.location;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import org.springframework.web.bind.annotation.RequestBody;

public interface LocationClientV1 {

  String claimSloid(@RequestBody ClaimSloidRequestModel claimSloidRequestModel);

  String generateSloid(@RequestBody GenerateSloidRequestModel generateSloidRequestModel);

}
