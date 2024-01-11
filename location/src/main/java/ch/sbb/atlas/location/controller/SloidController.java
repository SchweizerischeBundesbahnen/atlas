package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import ch.sbb.atlas.location.service.SloidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SloidController implements SloidApiV1 {

  private final SloidService sloidService;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel request) {
    final String sloidPrefix = request.sloidType().getSloidPrefix(request.sloidPrefix());
    final String seqName = request.sloidType().getSeqName();
    return ResponseEntity.ok(sloidService.generateNewSloid(sloidPrefix, seqName));
  }

  @Override
  public ResponseEntity<String> claimSloid(ClaimSloidRequestModel request) {
    final String sloid = request.sloid();
    final boolean sloidApproved = sloidService.claimSloid(sloid);
    return sloidApproved ? ResponseEntity.ok(sloid)
        : ResponseEntity.status(HttpStatus.CONFLICT).body(sloid + " is already used.");
  }

}
