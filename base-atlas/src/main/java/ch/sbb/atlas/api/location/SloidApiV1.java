package ch.sbb.atlas.api.location;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("v1/sloid")
public interface SloidApiV1 {

  @PostMapping("generate")
  String generateSloid(@RequestBody @Valid GenerateSloidRequestModel generateSloidRequestModel);

  @PostMapping("claim")
  String claimSloid(@RequestBody @Valid ClaimSloidRequestModel claimSloidRequestModel);

  @PostMapping("maintenance/sync")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void sync();

}
