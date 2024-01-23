package ch.sbb.atlas.api.location;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/sloid")
public interface SloidApiV1 {

  @PostMapping("generate")
  ResponseEntity<String> generateSloid(@RequestBody @Valid GenerateSloidRequestModel generateSloidRequestModel);

  @PostMapping("claim")
  ResponseEntity<String> claimSloid(@RequestBody @Valid ClaimSloidRequestModel claimSloidRequestModel);

  @GetMapping
  ResponseEntity<Void> sync();

}
